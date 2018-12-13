package database.query.executor;

import java.io.IOException;

import database.DataType;
import java.util.LinkedList;
import database.Row;
import database.SelectRowsQueryOutput;
import database.Table;
import database.TableFactory;
import database.Value;
import database.contract.PlaceholderMap;
import database.contract.QueryExecutor;
import database.exception.ExpressionSyntaxException;
import database.exception.TokenizationException;
import database.exception.BadQueryException;
import database.io.IOFacilityFactory;
import database.io.Util;
import database.query.expression.parser.ASTNode;
import database.query.expression.parser.Evaluator;
import database.query.expression.parser.Lexer;
import database.query.expression.parser.SyntaxAnalyzer;
import database.query.entity.DescribeTableQuery;
import database.query.entity.SelectRowsQuery;
import database.scheme.ColumnScheme;
import database.scheme.TableScheme;
import database.io.Reader;

public class SelectRowsQueryExecutor implements QueryExecutor<SelectRowsQueryOutput, SelectRowsQuery> {
    private final TableFactory mTableFactory;
    private final QueryExecutor<TableScheme, DescribeTableQuery> mDescribeTableQueryExecutor;
    private final IOFacilityFactory mIOFacilityFactory;

    public SelectRowsQueryExecutor(IOFacilityFactory ioFacilityFactory, TableFactory factory, QueryExecutor<TableScheme, DescribeTableQuery> describeTableQueryExecutor) {
        mIOFacilityFactory = ioFacilityFactory;
        mTableFactory = factory;
        mDescribeTableQueryExecutor = describeTableQueryExecutor;
    }

    @Override
    public SelectRowsQueryOutput execute(SelectRowsQuery query) throws BadQueryException, IOException {
        int rowSize; // indicates how many bytes a row takes

        Table table;
        Reader reader;
        TableScheme tableScheme;

        table = mTableFactory.make(query);
        reader = mIOFacilityFactory.reader(table.getDataFile());
        tableScheme = mDescribeTableQueryExecutor.execute(new DescribeTableQuery(query.getTableName()));

        rowSize = calculateRowSize(tableScheme.getColumns());

//        if (query.hasPredicate()) {
//            return withPredicate(query, reader, tableScheme);
//        } else {
//        }

        return select(query, reader, tableScheme, rowSize);
    }

    private int calculateRowSize(ColumnScheme[] columns) {
        int res = 0;

        for (ColumnScheme columnScheme : columns) {
            if (columnScheme.getType() == DataType.STRING) {
                res += DataType.INTEGER.getSize(); // hash
                res += DataType.INTEGER.getSize(); // number of bytes actually written
                res += DataType.STRING.getSize(); // string
            } else if (columnScheme.getType() == DataType.INTEGER) {
                res += DataType.INTEGER.getSize(); // integer
            }
        }

        return res;
    }

    private SelectRowsQueryOutput select(SelectRowsQuery query, Reader reader, TableScheme tableScheme, int rowSize) throws IOException, TokenizationException, ExpressionSyntaxException {
        final int INTEGER_SIZE = DataType.INTEGER.getSize();
        final int STRING_SIZE = DataType.STRING.getSize();

        int stringHashCode;
        int stringLength;
        int bufferPointerPos; // pointer position in buffer
        int fileSize; // amount of bytes all rows take
        int bytesRead; // amount of bytes already read
        int rowsCount; // number of rows in table
        int rowsPerRead; // number of rows to be read per one read
        int columnsCount; // number of columns in table
        byte[] buffer; // a buffer containing bytes read
        byte[] integerBuffer; // a buffer to place an integer
        byte[] stringBuffer; // a buffer to place a string

        Lexer lexer;
        ASTNode astNode;
        Evaluator evaluator;
        SyntaxAnalyzer syntaxAnalyzer;
        LinkedList<Row> result;
        ColumnScheme[] columns; // columns of table
        Row row; // a representation of one record

        if (query.hasPredicate()) {
            lexer = new Lexer(query.getExpression().toCharArray());
            syntaxAnalyzer = new SyntaxAnalyzer(lexer);
            astNode = syntaxAnalyzer.ast();
            evaluator = new Evaluator(query.getAttributes(), tableScheme.getColumns(), astNode);
        } else {
            evaluator = null;
        }

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
            bytesRead += reader.readBytes(buffer);

            for (int i = 0; i < rowsPerRead; i++) {

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

                if (query.hasPredicate()) {
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
}
