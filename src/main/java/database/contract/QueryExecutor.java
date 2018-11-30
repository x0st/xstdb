package database.contract;

import database.exception.BadQueryException;
import database.exception.UnexpectedErrorException;

public interface QueryExecutor<T, A> {
    public T execute(A query) throws BadQueryException, UnexpectedErrorException;
}
