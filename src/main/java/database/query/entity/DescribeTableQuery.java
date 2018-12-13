package database.query.entity;

import database.contract.HasTableName;
import database.contract.Query;

public class DescribeTableQuery implements Query, HasTableName {
    private String tableName;

    public DescribeTableQuery(String table) {
        tableName = table;
    }

    public String getTableName() {
        return tableName;
    }
}
