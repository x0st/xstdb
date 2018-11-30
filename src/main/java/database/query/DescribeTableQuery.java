package database.query;

import database.contract.Query;

public class DescribeTableQuery implements Query {
    private String tableName;

    public DescribeTableQuery(String table) {
        tableName = table;
    }

    public String getTableName() {
        return tableName;
    }
}
