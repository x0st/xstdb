package database.scheme;

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
}
