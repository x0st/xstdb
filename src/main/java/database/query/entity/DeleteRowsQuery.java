package database.query.entity;

import database.contract.HasPredicate;
import database.contract.HasTableName;
import database.contract.Query;
import database.query.expression.PlaceholderObject;
import database.query.expression.parser.ASTNode;
import io.mappedbus.MemoryMappedFile;

public class DeleteRowsQuery extends AbstractQuery implements HasPredicate {
    private String mTableName;
    private ASTNode mExpression;
    private PlaceholderObject mPlaceholders;

    public DeleteRowsQuery(String table, PlaceholderObject placeholderObject, ASTNode exp) {
        mTableName = table;
        mExpression = exp;
        mPlaceholders = placeholderObject;
    }

    @Override
    public String getTableName() {
        return mTableName;
    }

    public ASTNode getPredicate() {
        return mExpression;
    }

    @Override
    public boolean hasPredicate() {
        return mExpression != null;
    }

    public PlaceholderObject getAttributes() {
        return mPlaceholders;
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
