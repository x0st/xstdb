package database.contract;

import java.io.IOException;

import database.exception.BadQueryException;

public interface QueryExecutor<T, A extends Query> {
    public T execute(A query) throws BadQueryException, IOException;
    public boolean executes(Query query);
}
