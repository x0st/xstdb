package queryexecutor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import contract.QueryExecutor;
import core.DatabaseDir;
import core.RandomAccessFile;
import exception.TableDoesNotExistException;
import exception.UnexpectedErrorException;
import query.DescribeTableQuery;
import scheme.ColumnScheme;
import scheme.TableScheme;

public class DescribeTableQueryExecutor implements QueryExecutor<TableScheme, DescribeTableQuery> {
    private final DatabaseDir databaseDir;

    public DescribeTableQueryExecutor(DatabaseDir databaseDir) {
        this.databaseDir = databaseDir;
    }

    public TableScheme execute(DescribeTableQuery query) throws TableDoesNotExistException, UnexpectedErrorException {
        RandomAccessFile table;
        File tableFile = new File(databaseDir.getDirectory(), query.getTableName());

        if (!tableFile.exists()) {
            throw new TableDoesNotExistException("A table with this name does not exist.");
        }

        try {
            table = new RandomAccessFile(tableFile, "rw");
            List<ColumnScheme> columnSchemeList = new ArrayList<>();

            Integer lengthOfColumnsName;
            Integer lengthOfTablesName;
            Integer numberOfRows;
            Integer numberOfColumns;
            Integer columnType;
            Integer columnSize;

            // set pointer at the beginning
            table.seek(4);
            // number of rows the table contains
            numberOfRows = table.read4BytesNumber();
            // this is needed to allocate the exact amount of bytes for name
            lengthOfTablesName = table.read4BytesNumber();
            // where table's name is stored
            byte[] tableNameAsBytes = new byte[lengthOfTablesName];
            // read table's name
            table.read(tableNameAsBytes, 0, lengthOfTablesName);
            // number of columns the table contains
            numberOfColumns = table.read4BytesNumber();

            // gather columns
            for (int i = 0; i < numberOfColumns; i++) {
                // this is needed to allocate the exact amount of bytes for name
                lengthOfColumnsName = table.read4BytesNumber();
                // where column's name is stored
                byte[] columnName = new byte[lengthOfColumnsName];
                // read column's name
                table.read(columnName, 0, lengthOfColumnsName);
                // type of the column (int, char)
                columnType = table.read4BytesNumber();
                // the max number of bytes the column can hold
                columnSize = table.read4BytesNumber();

                // complement the column's list
                columnSchemeList.add(
                        new ColumnScheme(
                                new String(columnName, StandardCharsets.UTF_8),
                                columnType,
                                columnSize
                        )
                );
            }

            table.close();

            return new TableScheme(new String(tableNameAsBytes, StandardCharsets.UTF_8), numberOfRows, columnSchemeList);
        } catch (IOException e) {
            throw new UnexpectedErrorException("An error has occurred while reading from the table file.", e);
        }
    }
}
