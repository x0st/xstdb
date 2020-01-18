package database.contract;

import database.QueryType;
import database.exception.BadQueryException;

public interface QueryAssembler<T extends Query> {
    public T assemble(LexerInterface lexer) throws BadQueryException;
    public boolean supports(QueryType queryType);
}
