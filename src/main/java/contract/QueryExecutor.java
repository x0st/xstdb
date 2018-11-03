package contract;

import exception.BadQueryException;
import exception.UnexpectedErrorException;

public interface QueryExecutor<T, A> {
    public T execute(A query) throws BadQueryException, UnexpectedErrorException;
}
