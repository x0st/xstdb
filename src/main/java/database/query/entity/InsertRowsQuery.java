package database.query.entity;

import java.util.List;

import database.contract.Record;
import database.contract.HasTableName;
import database.contract.Query;

public class InsertRowsQuery implements Query, HasTableName {
    private final String mTableName;
    private final List<Record> mData;
    private final List<String> mColumnOrder;

    public InsertRowsQuery(String table, List<String> columnOrder, List<Record> records) {
        mTableName = table;
        mColumnOrder = columnOrder;
        mData = records;
    }

    public List<Record> getData() {
        return mData;
    }

    public String getTableName() {
        return mTableName;
    }

    public List<String> getColumnOrder() {
        return mColumnOrder;
    }
}
