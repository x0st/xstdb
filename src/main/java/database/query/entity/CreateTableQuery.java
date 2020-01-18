package database.query.entity;

import java.util.ArrayList;
import java.util.List;

import database.DataType;
import database.QueryType;
import database.contract.Query;
import database.exception.BuilderException;
import database.scheme.ColumnScheme;
import database.scheme.TableScheme;
import io.mappedbus.MemoryMappedFile;

public class CreateTableQuery implements Query {
    private TableScheme tableScheme;

    public CreateTableQuery(String table, ColumnScheme[] columnSchemeList) {
        tableScheme = new TableScheme(table.toCharArray(), 0, columnSchemeList);
    }

    public CreateTableQuery(char[] table, ColumnScheme[] columnSchemeList) {
        tableScheme = new TableScheme(table, 0, columnSchemeList);
    }

    public CreateTableQuery() {

    }

    public TableScheme getTableScheme() {
        return tableScheme;
    }

    @Override
    public String getTableName() {
        return String.valueOf(tableScheme.getName());
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void write(MemoryMappedFile mem, long pos) {
        // contains the number of bytes that have been already written for column's definition
        int writtenForColumns = 0;

        // writing down the name
        mem.putInt(pos, tableScheme.getName().length);
        mem.setBytes(pos + 4L, new String(tableScheme.getName()).getBytes(), 0, tableScheme.getName().length);

        // column's count
        mem.putInt(pos + 4L + tableScheme.getName().length, tableScheme.getColumns().length);

        for (int i = 0; i < tableScheme.getColumns().length; i++) {
            // column's name length
            mem.putInt(pos + 4L + tableScheme.getName().length + 4L + writtenForColumns, tableScheme.getColumns()[i].getName().length);
            writtenForColumns += 4;
            // column's name
            mem.setBytes(pos + 4L + tableScheme.getName().length + 4L + writtenForColumns, new String(tableScheme.getColumns()[i].getName()).getBytes(), 0, tableScheme.getColumns()[i].getName().length);
            writtenForColumns += tableScheme.getColumns()[i].getName().length;
            // column's type
            mem.putInt(pos + 4L + tableScheme.getName().length + 4L + writtenForColumns, tableScheme.getColumns()[i].getType().getType());
            writtenForColumns += 4;
            // column's size
            mem.putInt(pos + 4L + tableScheme.getName().length + 4L + writtenForColumns, tableScheme.getColumns()[i].getSize());
            writtenForColumns += 4;
        }
    }

    @Override
    public void read(MemoryMappedFile mem, long pos) {
        // indicates how many bytes have been read for the definition of columns
        int readForColumns = 0;

        // the length of table's name
        int tableNameLength = mem.getInt(pos);
        // in this variable a table name will be stored
        byte[] tableNameInBytes = new byte[tableNameLength];

        // read table's name
        mem.getBytes(pos + 4L, tableNameInBytes, 0, tableNameLength);

        // read the column's count
        int columnsCount = mem.getInt(pos + 4L + tableNameLength);

        int columnNameLength;
        byte[] columnNameInBytes;
        int columnType;
        int columnSize;

        ColumnScheme[] columnSchemes = new ColumnScheme[columnsCount];

        for (int i = 0; i < columnsCount; i++) {
            // column's name length
            columnNameLength = mem.getInt(pos + 4L + tableNameLength + 4L + readForColumns);
            readForColumns += 4;
            // column's name
            columnNameInBytes = new byte[columnNameLength];
            mem.getBytes(pos + 4L + tableNameLength + 4L + readForColumns, columnNameInBytes, 0, columnNameLength);
            readForColumns += columnNameLength;
            // column's type
            columnType = mem.getInt(pos + 4L + tableNameLength + 4L + readForColumns);
            readForColumns += 4;
            // column's size
            columnSize = mem.getInt(pos + 4L + tableNameLength + 4L + readForColumns);
            readForColumns += 4;

            columnSchemes[i] = new ColumnScheme(new String(columnNameInBytes).toCharArray(), DataType.valueOf((byte)columnType), columnSize);
        }

        tableScheme = new TableScheme(new String(tableNameInBytes).toCharArray(), 0, columnSchemes);
    }

    @Override
    public int type() {
        return QueryType.ADD.getType();
    }

    public static class Builder {
        private char[] table = null;
        private List<ColumnScheme> columnSchemeList = new ArrayList<>(10);

        public Builder table(char[] value) {
            table = value;
            return this;
        }

        public Builder column(ColumnScheme value) {
            columnSchemeList.add(value);
            return this;
        }

        public CreateTableQuery build() throws BuilderException {
            if (table == null || columnSchemeList.size() == 0) {
                throw new BuilderException();
            }

            ColumnScheme[] columnSchemes = new ColumnScheme[columnSchemeList.size()];

            for (int i = 0; i < columnSchemeList.size(); i++) {
                columnSchemes[i] = columnSchemeList.get(i);
            }

            return new CreateTableQuery(table, columnSchemes);
        }
    }
}
