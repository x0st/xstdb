package database.contract;

import database.query.QueryType;
import database.exception.BadQueryException;

public interface QueryAssembler<T extends Query> {
    public T assemble(LexerInterface lexer) throws BadQueryException;
    public boolean supports(QueryType queryType);
}
