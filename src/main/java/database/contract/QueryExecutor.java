package database.contract;

import java.io.IOException;

import database.exception.BadQueryException;

public interface QueryExecutor<T, A> {
    public T execute(A query) throws BadQueryException, IOException;
}
