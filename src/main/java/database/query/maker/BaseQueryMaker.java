package database.query.maker;

import java.util.Arrays;

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
            String expectedMessage = null;

            if (expected == Token.MATH_OPERATOR) {
                expectedMessage = "one of: <>, >, <, =";
            } else if (expected == Token.PLACEHOLDER) {
                expectedMessage = "a placeholder";
            } else if (expected == Token.WORD || expected == Token.QUOTED_STRING) {
                expectedMessage = "a string";
            } else if (expected == Token.LEFT_BRACKET) {
                expectedMessage = "a [";
            } else if (expected == Token.RIGHT_BRACKET) {
                expectedMessage = "a ]";
            } else if (expected == Token.NUMBER) {
                expectedMessage = "a number";
            } else if (expected == Token.LOGICAL_OPERATOR) {
                expectedMessage = "one of: |, &";
            } else {
                expectedMessage = "a token of type " + expected;
            }

            throw BadQueryException.expectedFound(expectedMessage, String.valueOf(lexer.lexeme()));
        }
    }

    protected void assertLexeme(String expected, LexerInterface lexer) throws BadQueryException {
        if (Arrays.hashCode(lexer.lexeme()) != Arrays.hashCode(expected.toCharArray())) {
            throw BadQueryException.expectedFound(expected, String.valueOf(lexer.lexeme()));
        }
    }
}
