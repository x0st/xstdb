package database.query.maker;

import database.contract.LexerInterface;
import database.exception.BadQueryException;
import database.query.parser.Token;

abstract public class BaseQueryMaker {
    protected char[] takeTableName(LexerInterface lexer) throws BadQueryException {
        lexer.next();
        assertToken(Token.WORD, lexer);

        return lexer.lexeme();
    }

    protected void assertToken(byte expected, LexerInterface lexer) throws BadQueryException {
        if (lexer.token() != expected) {
            throw BadQueryException.badSyntax();
        }
    }
}
