package database.queryexecutor;

import java.io.File;
import java.io.IOException;
import java.util.List;

import database.contract.QueryExecutor;
import database.RandomAccessFile;

import database.DatabaseDir;
import database.exception.TableAlreadyExistsException;
import database.exception.UnexpectedErrorException;
import database.query.CreateTableQuery;
import database.scheme.ColumnScheme;

public class CreateTableQueryExecutor implements QueryExecutor<Void, CreateTableQuery>{
    private final DatabaseDir databaseDir;

    public CreateTableQueryExecutor(DatabaseDir databaseDir) {
        this.databaseDir = databaseDir;
    }

    public Void execute(CreateTableQuery query) throws TableAlreadyExistsException, UnexpectedErrorException {
        List<ColumnScheme> columnSchemeList;
        RandomAccessFile table;
        File tableFile;

        tableFile = new File(databaseDir.getDirectory(), query.getTableScheme().getName());

        try {
            if (!tableFile.createNewFile()) {
                throw new TableAlreadyExistsException("A table with this name already exists.");
            }
        } catch (IOException e) {
            throw new UnexpectedErrorException("Failed to create a file for a table.", e);
        }

        try {
            table = new RandomAccessFile(tableFile, "rw");
            columnSchemeList = query.getTableScheme().getColumns();

            // 0.
            table.write4BytesNumber(0);
            // 1. number of rows
            table.write4BytesNumber(0);
            // 2. table name
            table.writeASCII(query.getTableScheme().getName());
            // 3. number of columns
            table.write4BytesNumber(columnSchemeList.size());

            for (ColumnScheme columnScheme : columnSchemeList) {
                // 3.1 column name
                table.writeASCII(columnScheme.getName());
                // 3.2 column type
                table.write4BytesNumber(columnScheme.getType());
                // 3.3 column size
                table.write4BytesNumber(columnScheme.getSize());
            }

            table.seek(0);

            table.write4BytesNumber((int) table.length());

            table.close();
        } catch (Throwable e) {
            tableFile.delete();

            throw new UnexpectedErrorException("An error has occurred while writing into the table file.", e);
        }

        return null;
    }
}
