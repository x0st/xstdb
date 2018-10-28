package exception;

public class TableDoesNotExistException extends BadQueryException {
    public TableDoesNotExistException(String message) {
        super(message);
    }
}
