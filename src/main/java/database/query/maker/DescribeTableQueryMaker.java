package database.query.maker;

import database.contract.LexerInterface;
import database.contract.QueryMaker;
import database.exception.BadQueryException;
import database.query.entity.DescribeTableQuery;
import database.query.parser.Token;

public class DescribeTableQueryMaker extends BaseQueryMaker implements QueryMaker<DescribeTableQuery> {
    @Override
    public DescribeTableQuery make(LexerInterface lexer) throws BadQueryException {
        return new DescribeTableQuery(takeTableName(lexer));
    }
}
