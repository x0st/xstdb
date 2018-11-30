package database.queryexecutor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import database.contract.QueryExecutor;
import database.DatabaseDir;
import database.RandomAccessFile;
import database.exception.TableDoesNotExistException;
import database.exception.UnexpectedErrorException;
import database.query.DescribeTableQuery;
import database.scheme.ColumnScheme;
import database.scheme.TableScheme;

public class DescribeTableQueryExecutor implements QueryExecutor<TableScheme, DescribeTableQuery> {
    private final DatabaseDir databaseDir;

    public DescribeTableQueryExecutor(DatabaseDir databaseDir) {
        this.databaseDir = databaseDir;
    }

    public TableScheme execute(DescribeTableQuery query) throws TableDoesNotExistException, UnexpectedErrorException {
        RandomAccessFile table;
        int numberOfRows;
        int numberOfColumns;
        int columnType;
        int columnSize;
        String tableName;
        String columnName;
        File tableFile;
        List<ColumnScheme> columnSchemeList;

        tableFile = new File(databaseDir.getDirectory(), query.getTableName());

        if (!tableFile.exists()) {
            throw new TableDoesNotExistException("A table with this name does not exist.");
        }

        try {
            table = new RandomAccessFile(tableFile, "rw");
            columnSchemeList = new ArrayList<>();

            // first 4 bytes are reserved for the integer that represents the number of bytes allocated for table database.scheme
            table.seek(4);

            numberOfRows = table.read4BytesNumber();

            tableName = table.readASCII();

            numberOfColumns = table.read4BytesNumber();

            // gather columns
            for (int i = 0; i < numberOfColumns; i++) {
                columnName = table.readASCII();
                // type of column (int, char)
                columnType = table.read4BytesNumber();
                // the max number of bytes the column can contain
                columnSize = table.read4BytesNumber();

                columnSchemeList.add(
                        new ColumnScheme(columnName, columnType, columnSize)
                );
            }

            table.close();

            return new TableScheme(tableName, numberOfRows, columnSchemeList);
        } catch (IOException e) {
            throw new UnexpectedErrorException("An error has occurred while reading from the table file.", e);
        }
    }
}
