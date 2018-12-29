package database.exception;

public class BadQueryException extends Exception {
    public BadQueryException(String message) {
        super(message);
    }

    public static BadQueryException badSyntax() {
        return new BadQueryException("Bad syntax");
    }

    public static BadQueryException tableExists() {
        return new BadQueryException("A table with this name already exists");
    }

    public static BadQueryException tableNotFound() {
        return new BadQueryException("A table with this name does not exist");
    }
}
