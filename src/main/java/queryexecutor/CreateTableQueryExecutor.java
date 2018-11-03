package queryexecutor;

import java.io.File;
import java.io.IOException;
import java.util.List;

import contract.QueryExecutor;
import core.RandomAccessFile;

import core.DatabaseDir;
import exception.TableAlreadyExistsException;
import exception.UnexpectedErrorException;
import query.CreateTableQuery;
import scheme.ColumnScheme;

public class CreateTableQueryExecutor implements QueryExecutor<Void, CreateTableQuery>{
    private final DatabaseDir databaseDir;

    public CreateTableQueryExecutor(DatabaseDir databaseDir) {
        this.databaseDir = databaseDir;
    }

    public Void execute(CreateTableQuery query) throws TableAlreadyExistsException, UnexpectedErrorException {
        Integer techStuffLength = 0;
        RandomAccessFile table;
        File tableFile = new File(databaseDir.getDirectory(), query.getTableScheme().getName());

        try {
            if (!tableFile.createNewFile()) {
                throw new TableAlreadyExistsException("A table with this name already exists.");
            }
        } catch (IOException e) {
            throw new UnexpectedErrorException("Failed to create a file for a table.", e);
        }

        try {
            table = new RandomAccessFile(tableFile, "rw");

            List<ColumnScheme> columnSchemeList = query.getTableScheme().getColumns();

            techStuffLength += 4;
            // 4 bytes for the int indicating length of table's name
            techStuffLength += 4;
            // table's name length
            techStuffLength += query.getTableScheme().getName().getBytes().length;
            // 4 bytes for the int indicating number of rows
            techStuffLength += 4;
            // 4 bytes for the int indicating number of columns
            techStuffLength += 4;

            for (ColumnScheme columnScheme : columnSchemeList) {
                // 4 bytes for the int indicating length of column's name
                techStuffLength += 4;
                // column's name length
                techStuffLength += columnScheme.getName().getBytes().length;
                // 4 bytes for the int indicating column type
                techStuffLength += 4;
                // 4 bytes for the int indicating column size
                techStuffLength += 4;
            }

            table.write4BytesNumber(techStuffLength);
            // write number of rows
            table.write4BytesNumber(0);
            // write length of table's name
            table.write4BytesNumber(query.getTableScheme().getName().getBytes().length);
            // write table name
            table.writeASCII(query.getTableScheme().getName());
            // write number of columns
            table.write4BytesNumber(columnSchemeList.size());

            for (ColumnScheme columnScheme : columnSchemeList) {
                // write length of column's name
                table.write4BytesNumber(columnScheme.getName().getBytes().length);
                // write column name
                table.writeASCII(columnScheme.getName());
                // write column type
                table.write4BytesNumber(columnScheme.getType());
                // write column size
                table.write4BytesNumber(columnScheme.getSize());
            }

            table.close();
        } catch (Throwable e) {
            throw new UnexpectedErrorException("An error has occurred while writing into the table file.", e);
        }

        return null;
    }
}
