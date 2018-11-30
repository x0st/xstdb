package database.exception;

public class TableAlreadyExistsException extends BadQueryException {
    public TableAlreadyExistsException(String message) {
        super(message);
    }
}
