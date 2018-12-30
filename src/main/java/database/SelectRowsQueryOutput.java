package database;

import java.util.LinkedList;

import database.contract.Record;
import database.scheme.ColumnScheme;

public class SelectRowsQueryOutput {
    private final ColumnScheme[] mColumnSchemes;
    private final LinkedList<Record> mRecords;

    public SelectRowsQueryOutput(ColumnScheme[] columnSchemes, LinkedList<Record> records) {
        mColumnSchemes = columnSchemes;
        mRecords = records;
    }

    public ColumnScheme[] getColumnSchemes() {
        return mColumnSchemes;
    }

    public LinkedList<Record> getRecords() {
        return mRecords;
    }
}
