package database.query.assember;

import database.query.QueryType;
import database.contract.LexerInterface;
import database.contract.QueryAssembler;
import database.exception.BadQueryException;
import database.query.entity.DescribeTableQuery;

public class DescribeTableQueryAssembler extends BaseQueryAssembler implements QueryAssembler<DescribeTableQuery> {
    @Override
    public DescribeTableQuery assemble(LexerInterface lexer) throws BadQueryException {
        return new DescribeTableQuery(takeTableName(lexer));
    }

    @Override
    public boolean supports(QueryType queryType) {
        return queryType == QueryType.SHOW;
    }
}
