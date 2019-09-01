package database.contract;

import database.query.expression.parser.ASTNode;

public interface HasPredicate {
    public ASTNode getPredicate();
    public boolean hasPredicate();
}
