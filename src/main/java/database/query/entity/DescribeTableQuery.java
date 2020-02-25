package database.query.entity;

import database.contract.HasTableName;
import database.contract.Query;
import io.mappedbus.MemoryMappedFile;

public class DescribeTableQuery extends AbstractQuery {
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
    protected void writeIntoMemoryMappedFile(MemoryMappedFile mem, long pos) {

    }

    @Override
    protected void recreateFromMemoryMappedFile(MemoryMappedFile mem, long pos) {

    }

    @Override
    public int type() {
        return 0;
    }
}
