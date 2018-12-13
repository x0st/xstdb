package database;

import java.util.LinkedList;

import database.scheme.ColumnScheme;

public class SelectRowsQueryOutput {
    private final ColumnScheme[] mColumnSchemes;
    private final LinkedList<Row> mRecords;

    public SelectRowsQueryOutput(ColumnScheme[] columnSchemes, LinkedList<Row> records) {
        mColumnSchemes = columnSchemes;
        mRecords = records;
    }

    public ColumnScheme[] getColumnSchemes() {
        return mColumnSchemes;
    }

    public LinkedList<Row> getRecords() {
        return mRecords;
    }
}
