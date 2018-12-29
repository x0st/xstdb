package database.contract;

import database.exception.BadQueryException;

public interface QueryMaker<T> {
    public T make(LexerInterface lexer) throws BadQueryException;
}
