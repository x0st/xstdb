package database.query.entity;

import java.util.LinkedList;
import java.util.List;

import database.contract.Record;
import database.contract.HasTableName;
import database.contract.Query;
import database.exception.BuilderException;
import io.mappedbus.MemoryMappedFile;

public class InsertRowsQuery implements Query {
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

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void write(MemoryMappedFile mem, long pos) {

    }

    @Override
    public void read(MemoryMappedFile mem, long pos) {

    }

    @Override
    public int type() {
        return 0;
    }

    public static class Builder {
        private char[] table = null;
        private List<Record> rows = new LinkedList<>();
        private List<String> columns = new LinkedList<>();

        public Builder table(char[] value) {
            table = value;
            return this;
        }

        public Builder row(Record row) {
            rows.add(row);
            return this;
        }

        public Builder columns(List<String> value) {
            columns = value;
            return this;
        }

        public InsertRowsQuery build() throws BuilderException {
            if (table == null || rows.size() == 0 || columns.size() == 0) {
                throw new BuilderException();
            }

            return new InsertRowsQuery(String.valueOf(table), columns, rows);
        }
    }
}
