package database.query;

import java.util.Map;

import database.contract.Query;

public class InsertRowQuery implements Query {
    private String tableName;
    private Map<String, Object> data;

    public InsertRowQuery(String table, Map<String, Object> dataMap) {
        tableName = table;
        data = dataMap;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public String getTableName() {
        return tableName;
    }
}
