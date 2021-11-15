package database.rawquery;

import java.io.InputStream;

import database.contract.LexerInterface;
import database.rawquery.parser.Lexer;

public class LexerFactory {
    public LexerInterface make(InputStream input) {
        return new Lexer(input);
    }
}
