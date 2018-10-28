package exception;

public class UnexpectedErrorException extends Exception {
    public UnexpectedErrorException(String message) {
        super(message);
    }

    public UnexpectedErrorException(String message, Throwable previous) {
        super(message, previous);
    }
}
