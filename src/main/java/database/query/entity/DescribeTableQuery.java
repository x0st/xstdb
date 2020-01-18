package database.query.entity;

import database.contract.HasTableName;
import database.contract.Query;
import io.mappedbus.MemoryMappedFile;

public class DescribeTableQuery implements Query {
    private String tableName;

    public DescribeTableQuery(String table) {
        tableName = table;
    }

    public DescribeTableQuery(char[] table) {
        tableName = String.valueOf(table);
    }

    public String getTableName() {
        return tableName;
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
}
