package database.contract;

import database.QueryType;
import database.exception.BadQueryException;

public interface QueryMaker<T extends Query> {
    public T make(LexerInterface lexer) throws BadQueryException;
    public boolean supports(QueryType queryType);
}
