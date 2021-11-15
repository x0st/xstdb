package database.query.executor;

import java.io.IOException;

import database.DataType;
import database.Row;
import database.Value;
import database.contract.Query;
import database.contract.QueryExecutorInterface;
import database.contract.Record;
import database.exception.BadQueryException;
import database.io.IOFacilityFactory;
import database.io.RAF;
import database.io.Reader;
import database.io.Util;
import database.Table;
import database.TableFactory;
import database.query.entity.DeleteRowsQuery;
import database.query.entity.DescribeTableQuery;
import database.query.expression.parser.Evaluator;
import database.scheme.ColumnScheme;
import database.scheme.TableScheme;

public class DeleteRowsQueryExecutor implements QueryExecutorInterface<Void, DeleteRowsQuery> {
    private IOFacilityFactory mIOFacilityFactory;
    private TableFactory mTableFactory;
    private final QueryExecutorInterface<TableScheme, DescribeTableQuery> mDescribeTableQueryQueryExecutor;

    public DeleteRowsQueryExecutor(
            TableFactory tableFactory,
            IOFacilityFactory ioFacilityFactory,
            QueryExecutorInterface<TableScheme, DescribeTableQuery> describeTableQueryQueryExecutor
    ) {
        mTableFactory = tableFactory;
        mIOFacilityFactory = ioFacilityFactory;
        mDescribeTableQueryQueryExecutor = describeTableQueryQueryExecutor;
    }

    @Override
    public Void execute(DeleteRowsQuery query) throws BadQueryException, IOException {
        try {
            execute0(query);
        } catch (Throwable e) {
            throw BadQueryException.badSyntax();
        }

        return null;
    }

    private void execute0(DeleteRowsQuery query) throws IOException, BadQueryException {
        RAF raf;
        Table table;
        Reader reader;
        TableScheme tableScheme;

        table = mTableFactory.make(query);
        reader = mIOFacilityFactory.reader(table.getDataFile());
        tableScheme = mDescribeTableQueryQueryExecutor.execute(new DescribeTableQuery(query.getTableName()));
        raf = mIOFacilityFactory.randomAccessFile(table.getDataFile());

        delete(query, reader, raf, tableScheme);
    }

    private void delete(DeleteRowsQuery query, Reader reader, RAF raf, TableScheme tableScheme) throws IOException {
        // how many bytes an integer takes
        final int INTEGER_SIZE = DataType.INTEGER.getSize();
        // how many bytes a string takes
        final int STRING_SIZE = DataType.STRING.getSize();

        int rowsRead; // the number of rows already read
        int rowSize; // the number of bytes one row takes
        int stringHashCode; // contains the has code of a string
        int stringLength; // indicates how many bytes a string takes
        int bufferPointerPos; // pointer position in the chunk buffer
        int fileSize; // number of bytes all rows take
        int bytesRead; // amount of bytes already read
        int rowsCount; // number of rows in table
        int rowsPerRead; // number of rows to be read per one read
        int columnsCount; // number of columns in table
        byte[] buffer; // the chunk buffer
        byte[] integerBuffer; // a temp buffer to place an integer
        byte[] stringBuffer; // a temp buffer to place a string

        Evaluator evaluator; // will resolve the conditions (predicate)
        ColumnScheme[] columns; // columns of table
        Record row; // a representation of one record

        if (query.hasPredicate()) {
            evaluator = new Evaluator(query.getAttributes(), tableScheme.getColumns(), query.getPredicate());
        } else {
            evaluator = null;
        }

        rowsRead = 0;
        rowSize = tableScheme.getRowSize();
        bufferPointerPos = 0;
        integerBuffer = new byte[4];
        columnsCount = tableScheme.getColumns().length;
        rowsCount = tableScheme.getRowsCount();
        rowsPerRead = 10;
        rowsPerRead = rowsCount < rowsPerRead ? rowsCount : rowsPerRead;
        bytesRead = 0;
        fileSize = rowsCount * rowSize;
        buffer = new byte[rowSize * rowsPerRead];
        columns = tableScheme.getColumns();

        while (bytesRead < fileSize) {
            // read a chunk of data.
            // the chunk contains %rowsPerRead% * %rowSize% bytes.
            // so we then will be reading %rowSize% bytes %rowsPerRead% times.
            bytesRead += reader.readBytes(buffer);

            for (int i = 0; i < rowsPerRead; i++) {
                // if the first byte of the row says it's been deleted
                // then we skip the entire row and move on to the next one
                if (buffer[i * rowSize] == 1) {
                    bufferPointerPos += rowSize;
                    continue;
                }
                // the first byte of the row says it's not deleted
                // so we shift the pointer forward and proceed with the row
                else {
                    bufferPointerPos += 1;
                }

                row = new Row(columnsCount);

                for (int columnIndex = 0; columnIndex < columnsCount; columnIndex++) {
                    if (columns[columnIndex].getType() == DataType.INTEGER) {
                        // take an integer
                        {
                            System.arraycopy(buffer, bufferPointerPos, integerBuffer, 0, INTEGER_SIZE);
                            bufferPointerPos += INTEGER_SIZE;
                        }

                        row.set(columnIndex, new Value(Util.integerOutOfBytes(integerBuffer), DataType.INTEGER));
                    } else if (columns[columnIndex].getType() == DataType.STRING) {
                        // take an integer
                        {
                            System.arraycopy(buffer, bufferPointerPos, integerBuffer, 0, INTEGER_SIZE);
                            bufferPointerPos += INTEGER_SIZE;
                        }

                        stringHashCode = Util.integerOutOfBytes(integerBuffer);

                        // take an integer
                        {
                            System.arraycopy(buffer, bufferPointerPos, integerBuffer, 0, INTEGER_SIZE);
                            bufferPointerPos += INTEGER_SIZE;
                        }

                        stringLength = Util.integerOutOfBytes(integerBuffer);
                        stringBuffer = new byte[stringLength];

                        // take a char sequence
                        {
                            System.arraycopy(buffer, bufferPointerPos, stringBuffer, 0, stringLength);
                            bufferPointerPos += STRING_SIZE;
                        }

                        row.set(columnIndex, new Value(stringHashCode, Util.charSequenceOutOfBytes(stringBuffer), DataType.STRING));
                    }
                }

                if (evaluator != null) {
                    if (evaluator.evaluate(row)) {
                        // move pointer to the
                        raf.move(rowsRead * rowSize);
                        raf.writeByte((byte)1);
                    }
                } else {
                    raf.move(rowsRead * rowSize);
                    raf.writeByte((byte)1);
                }

                rowsRead +=1;
            }

            bufferPointerPos = 0;
        }
    }

    @Override
    public boolean executes(Query query) {
        return query instanceof DeleteRowsQuery;
    }
}
