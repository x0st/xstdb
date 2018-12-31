package database.query;

import database.contract.LexerInterface;
import database.query.parser.Lexer;

public class LexerFactory {
    public LexerInterface make(char[] input) {
        return new Lexer(input);
    }
}
