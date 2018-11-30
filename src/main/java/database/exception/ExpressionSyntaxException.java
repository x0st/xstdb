package database.exception;

public class ExpressionSyntaxException extends BadQueryException {
    public ExpressionSyntaxException(String message) {
        super(message);
    }
}
