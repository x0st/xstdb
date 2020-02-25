package database.query.executor;

import java.io.FileNotFoundException;
import java.io.IOException;

import database.DataType;
import java.util.LinkedList;
import database.Row;
import database.query.SelectRowsQueryOutput;
import database.Table;
import database.TableFactory;
import database.Value;
import database.contract.Query;
import database.contract.QueryExecutorInterface;
import database.contract.Record;
import database.exception.BadQueryException;
import database.io.IOFacilityFactory;
import database.io.Util;
import database.query.expression.parser.Evaluator;
import database.query.entity.DescribeTableQuery;
import database.query.entity.SelectRowsQuery;
import database.scheme.ColumnScheme;
import database.scheme.TableScheme;
import database.io.Reader;

public class SelectRowsQueryExecutor implements QueryExecutorInterface<SelectRowsQueryOutput, SelectRowsQuery> {
    private final TableFactory mTableFactory;
    private final QueryExecutorInterface<TableScheme, DescribeTableQuery> mDescribeTableQueryExecutor;
    private final IOFacilityFactory mIOFacilityFactory;

    public SelectRowsQueryExecutor(IOFacilityFactory ioFacilityFactory, TableFactory factory, QueryExecutorInterface<TableScheme, DescribeTableQuery> describeTableQueryExecutor) {
        mIOFacilityFactory = ioFacilityFactory;
        mTableFactory = factory;
        mDescribeTableQueryExecutor = describeTableQueryExecutor;
    }

    @Override
    public SelectRowsQueryOutput execute(SelectRowsQuery query) throws BadQueryException, IOException {
        try {
            return execute0(query);
        } catch (FileNotFoundException e) {
            throw BadQueryException.tableNotFound();
        }
    }

    private SelectRowsQueryOutput execute0(SelectRowsQuery query) throws BadQueryException, IOException {
        Table table;
        Reader reader;
        TableScheme tableScheme;

        table = mTableFactory.make(query);
        reader = mIOFacilityFactory.reader(table.getDataFile());
        tableScheme = mDescribeTableQueryExecutor.execute(new DescribeTableQuery(query.getTableName()));

        return select(query, reader, tableScheme);
    }

    private SelectRowsQueryOutput select(SelectRowsQuery query, Reader reader, TableScheme tableScheme) throws IOException {
        // how many bytes an integer takes
        final int INTEGER_SIZE = DataType.INTEGER.getSize();
        // how many bytes a string takes
        final int STRING_SIZE = DataType.STRING.getSize();

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
        LinkedList<Record> result; // will contain the found records
        ColumnScheme[] columns; // columns of table
        Record row; // a representation of one record

        if (query.hasPredicate()) {
            evaluator = new Evaluator(query.getAttributes(), tableScheme.getColumns(), query.getPredicate());
        } else {
            evaluator = null;
        }

        rowSize = tableScheme.getRowSize();
        bufferPointerPos = 0;
        integerBuffer = new byte[4];
        columnsCount = tableScheme.getColumns().length;
        rowsCount = tableScheme.getRowsCount();
        rowsPerRead = 100;
        rowsPerRead = rowsCount < rowsPerRead ? rowsCount : rowsPerRead;
        bytesRead = 0;
        fileSize = rowsCount * rowSize;
        buffer = new byte[rowSize * rowsPerRead];
        result = new LinkedList<>();
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
                        result.add(row);
                    }
                } else {
                    result.add(row);
                }
            }

            bufferPointerPos = 0;
        }

        return new SelectRowsQueryOutput(columns, result);
    }

    @Override
    public boolean executes(Query query) {
        return query instanceof SelectRowsQuery;
    }
}
