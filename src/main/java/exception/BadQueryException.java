package exception;

public class BadQueryException extends Exception {
    public BadQueryException(String message) {
        super(message);
    }
}
