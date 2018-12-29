package database.contract;

import database.exception.TokenizationException;

public interface LexerInterface {
    boolean next() throws TokenizationException;
    char[] lexeme();
    byte token();
}
