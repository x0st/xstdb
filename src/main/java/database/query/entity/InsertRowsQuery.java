package database.query.entity;

import java.util.LinkedList;
import java.util.List;

import database.ExternalRow;
import database.contract.HasTableName;
import database.contract.Query;

public class InsertRowsQuery implements Query, HasTableName {
    private final String mTableName;
    private final List<ExternalRow> mData;
    private final List<String> mColumnOrder;

    public InsertRowsQuery(String table, List<String> columnOrder, List<ExternalRow> records) {
        mTableName = table;
        mColumnOrder = columnOrder;
        mData = records;
    }

    public List<ExternalRow> getData() {
        return mData;
    }

    public String getTableName() {
        return mTableName;
    }

    public List<String> getColumnOrder() {
        return mColumnOrder;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private char[] table;
        private List<ExternalRow> rows = new LinkedList<>();
        private List<String> columns = new LinkedList<>();

        public Builder table(char[] value) {
            table = value;
            return this;
        }

        public Builder row(ExternalRow row) {
            rows.add(row);
            return this;
        }

        public Builder rows(List<ExternalRow> value) {
            rows = value;
            return this;
        }

        public Builder columns(List<String> value) {
            columns = value;
            return this;
        }

        public Builder column(String column) {
            columns.add(column);
            return this;
        }

        public InsertRowsQuery build() {
            if (table == null || rows.size() == 0 || columns.size() == 0) {
                throw new RuntimeException();
            }

            return new InsertRowsQuery(String.valueOf(table), columns, rows);
        }
    }
}
