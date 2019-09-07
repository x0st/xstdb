package database.scheme;

import database.DataType;

public class TableScheme {
    private char[] mName;
    private int mRowsCount;
    private ColumnScheme[] mColumns;

    public TableScheme(char[] name, int rowsCount, ColumnScheme[] columns) {
        mName = name;
        mColumns = columns;
        mRowsCount = rowsCount;
    }

    public char[] getName() {
        return mName;
    }

    public int getRowsCount() {
        return mRowsCount;
    }

    public ColumnScheme[] getColumns() {
        return mColumns;
    }

    public int getRowSize() {
        int res = 0;

        res += 1; // 1 byte reserved for the 'deleted' mark

        for (ColumnScheme columnScheme : mColumns) {
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
}
