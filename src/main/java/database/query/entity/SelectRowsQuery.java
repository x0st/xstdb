package database.query.entity;

import database.contract.HasTableName;
import database.contract.PlaceholderMap;
import database.contract.Query;
import database.query.expression.PlaceholderObject;

public class SelectRowsQuery implements Query, HasTableName {
    private String tableName;
    private String expression;
    private PlaceholderObject attributes;

    public SelectRowsQuery(String table, PlaceholderObject attributesMap, String expressionString) {
        tableName = table;
        attributes = attributesMap;
        expression = expressionString;
    }

    public boolean hasPredicate() {
        return expression.length() != 0;
    }

    public String getTableName() {
        return tableName;
    }

    public String getExpression() {
        return expression;
    }

    public PlaceholderObject getAttributes() {
        return attributes;
    }
}
