package database.query.entity;

import database.contract.HasTableName;
import database.contract.Query;
import database.query.expression.PlaceholderObject;
import database.query.expression.parser.ASTNode;

public class SelectRowsQuery implements Query, HasTableName {
    private String mTableName;
    private ASTNode mExpression;
    private PlaceholderObject mPlaceholders;

    public SelectRowsQuery(String table, PlaceholderObject placeholderObject, ASTNode exp) {
        mTableName = table;
        mPlaceholders = placeholderObject;
        mExpression = exp;
    }

    public boolean hasPredicate() {
        return mExpression != null;
    }

    public String getTableName() {
        return mTableName;
    }

    public ASTNode getExpression() {
        return mExpression;
    }

    public PlaceholderObject getAttributes() {
        return mPlaceholders;
    }
}
