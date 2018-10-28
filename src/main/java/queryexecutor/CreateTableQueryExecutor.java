package queryexecutor;

import java.io.File;
import java.io.IOException;
import java.util.List;

import core.RandomAccessFile;

import core.DatabaseDir;
import exception.TableAlreadyExistsException;
import exception.UnexpectedErrorException;
import query.CreateTableQuery;
import scheme.ColumnScheme;

public class CreateTableQueryExecutor {
    private final DatabaseDir databaseDir;

    public CreateTableQueryExecutor(DatabaseDir databaseDir) {
        this.databaseDir = databaseDir;
    }

    public void execute(CreateTableQuery query) throws TableAlreadyExistsException, UnexpectedErrorException {
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

            table.writeInt(techStuffLength);

            // write length of table's name
            table.writeInt(query.getTableScheme().getName().getBytes().length);
            // write table name
            table.writeASCII(query.getTableScheme().getName());
            // write number of rows
            table.writeInt(0);
            // write number of columns
            table.writeInt(columnSchemeList.size());

            for (ColumnScheme columnScheme : columnSchemeList) {
                // write length of column's name
                table.writeInt(columnScheme.getName().getBytes().length);
                // write column name
                table.writeASCII(columnScheme.getName());
                // write column type
                table.writeInt(columnScheme.getType());
                // write column size
                table.writeInt(columnScheme.getSize());
            }

            table.close();
        } catch (Throwable e) {
            throw new UnexpectedErrorException("An error has occurred while writing into the table file.", e);
        }

    }
}
