package database.queryexecutor;

import java.io.File;
import java.io.IOException;

import database.contract.QueryExecutor;
import database.DatabaseDir;
import database.RandomAccessFile;
import database.exception.BadQueryException;
import database.exception.TableDoesNotExistException;
import database.exception.UnexpectedErrorException;
import database.query.DescribeTableQuery;
import database.query.InsertRowQuery;
import database.scheme.ColumnScheme;
import database.scheme.TableScheme;

public class InsertRowQueryExecutor implements QueryExecutor<Void, InsertRowQuery> {
    private final DatabaseDir databaseDir;
    private final QueryExecutor<TableScheme, DescribeTableQuery> describeTableQueryExecutor;

    public InsertRowQueryExecutor(DatabaseDir databaseDir, QueryExecutor<TableScheme, DescribeTableQuery> describeTableQueryExecutor) {
        this.databaseDir = databaseDir;
        this.describeTableQueryExecutor = describeTableQueryExecutor;
    }

    public Void execute(InsertRowQuery query) throws UnexpectedErrorException, BadQueryException {
        TableScheme tableScheme;
        RandomAccessFile table;
        long rollbackPoint;
        Object cellValue;
        File tableFile;

        tableScheme = describeTableQueryExecutor.execute(new DescribeTableQuery(query.getTableName()));

        validateQuery(tableScheme, query);

        tableFile = new File(databaseDir.getDirectory(), query.getTableName());

        if (!tableFile.exists()) {
            throw new TableDoesNotExistException("A table with this name does not exist.");
        }

        try {
            table = new RandomAccessFile(tableFile, "rw");
            rollbackPoint = table.length();
        } catch (IOException e) {
            throw new UnexpectedErrorException("An error has occurred while writing into table file.", e);
        }

        try {

            // write to the end of the file
            table.seek(table.length());

            for (ColumnScheme columnScheme : tableScheme.getColumns()) {
                cellValue = query.getData().get(columnScheme.getName());

                if (cellValue instanceof Integer && columnScheme.getType() == 1) {
                     table.writeInt((Integer) cellValue);
                } else if (cellValue instanceof String && columnScheme.getType() == 2) {
                    table.writeASCII((String) cellValue, columnScheme.getSize());
                }
            }

            // update the number of records
            table.seek(4);
            table.writeInt(tableScheme.getRowsCount() + 1);

            table.close();
        } catch (IOException e) {
            try {
                table.setLength(rollbackPoint);
                table.close();
            } catch (IOException ignored) { }

            throw new UnexpectedErrorException("An error has occurred while writing into table file.", e);
        }

        return null;
    }

    private void validateQuery(TableScheme tableScheme, InsertRowQuery query) throws BadQueryException {
        Object cellValue;

        if (query.getData().size() != tableScheme.getColumns().size()) {
            throw new BadQueryException("The given data cannot be read.");
        }

        for (ColumnScheme columnScheme : tableScheme.getColumns()) {
            cellValue = query.getData().get(columnScheme.getName());

            if (cellValue == null) {
                throw new BadQueryException("All columns must have a value.");
            } else if (cellValue instanceof Integer && columnScheme.getType() != 1) {
                throw new BadQueryException("The given data cannot be read.");
            } else if (cellValue instanceof String && columnScheme.getType() != 2) {
                throw new BadQueryException("The given data cannot be read.");
            }
        }

        cellValue = null;
    }
}
