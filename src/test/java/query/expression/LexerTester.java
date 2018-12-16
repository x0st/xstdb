package query.expression;

import static database.query.expression.Token.Type.COLUMN;
import static database.query.expression.Token.Type.LOGICAL_OPERATOR;
import static database.query.expression.Token.Type.MATH_OPERATOR;
import static database.query.expression.Token.Type.PLACEHOLDER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import database.exception.TokenizationException;
import database.query.expression.parser.Lexer;

public class LexerTester {

    private String[] validExpressions = new String[] {
            "n > :n & id = :id | name < :name",
            "t = :test",
            "test = :test & b = :b",
            "id = :id & name = :name | f > :b"
    };

    private String[][] badExpressions = new String[][] {
            {"1", "n1 = :n & id = :id | name = :name"},
            {"4", "n = :n # id = :id | name = :name"},
            {"8", "n = :n | id = :id # name = :name"},
    };

    @Test
    public void returnsCorrectLexemes0() throws TokenizationException {
        Lexer lexer = new Lexer();
        lexer.setInput(validExpressions[0].toCharArray());

        lexer.next(); assertEquals("n", String.valueOf(lexer.lexeme()));
        lexer.next(); assertEquals(">", String.valueOf(lexer.lexeme()));
        lexer.next(); assertEquals(":n", String.valueOf(lexer.lexeme()));
        lexer.next(); assertEquals("&", String.valueOf(lexer.lexeme()));
        lexer.next(); assertEquals("id", String.valueOf(lexer.lexeme()));
        lexer.next(); assertEquals("=", String.valueOf(lexer.lexeme()));
        lexer.next(); assertEquals(":id", String.valueOf(lexer.lexeme()));
        lexer.next(); assertEquals("|", String.valueOf(lexer.lexeme()));
        lexer.next(); assertEquals("name", String.valueOf(lexer.lexeme()));
        lexer.next(); assertEquals("<", String.valueOf(lexer.lexeme()));
        lexer.next(); assertEquals(":name", String.valueOf(lexer.lexeme()));
    }

    @Test
    public void returnsCorrectLexemes3() throws TokenizationException {
        Lexer lexer = new Lexer();

        lexer.setInput(validExpressions[3].toCharArray());

        lexer.next(); assertEquals("id", String.valueOf(lexer.lexeme()));
        lexer.next(); assertEquals("=", String.valueOf(lexer.lexeme()));
        lexer.next(); assertEquals(":id", String.valueOf(lexer.lexeme()));
        lexer.next(); assertEquals("&", String.valueOf(lexer.lexeme()));
        lexer.next(); assertEquals("name", String.valueOf(lexer.lexeme()));
        lexer.next(); assertEquals("=", String.valueOf(lexer.lexeme()));
        lexer.next(); assertEquals(":name", String.valueOf(lexer.lexeme()));
        lexer.next(); assertEquals("|", String.valueOf(lexer.lexeme()));
        lexer.next(); assertEquals("f", String.valueOf(lexer.lexeme()));
        lexer.next(); assertEquals(">", String.valueOf(lexer.lexeme()));
        lexer.next(); assertEquals(":b", String.valueOf(lexer.lexeme()));

    }

    @Test
    public void returnsCorrectTokens0() throws TokenizationException {
        Lexer lexer = new Lexer();
        lexer.setInput(validExpressions[0].toCharArray());

        lexer.next(); assertEquals(COLUMN, lexer.token());
        lexer.next(); assertEquals(MATH_OPERATOR, lexer.token());
        lexer.next(); assertEquals(PLACEHOLDER, lexer.token());

        lexer.next(); assertEquals(LOGICAL_OPERATOR, lexer.token());

        lexer.next(); assertEquals(COLUMN, lexer.token());
        lexer.next(); assertEquals(MATH_OPERATOR, lexer.token());
        lexer.next(); assertEquals(PLACEHOLDER, lexer.token());

        lexer.next(); assertEquals(LOGICAL_OPERATOR, lexer.token());

        lexer.next(); assertEquals(COLUMN, lexer.token());
        lexer.next(); assertEquals(MATH_OPERATOR, lexer.token());
        lexer.next(); assertEquals(PLACEHOLDER, lexer.token());

        lexer.next(); assertEquals(-1, lexer.token());
        lexer.next(); assertEquals(-1, lexer.token());
        lexer.next(); assertEquals(-1, lexer.token());
    }

    @Test
    public void returnsCorrectTokens1() throws TokenizationException {
        Lexer lexer = new Lexer();
        lexer.setInput(validExpressions[1].toCharArray());

        lexer.next(); assertEquals(COLUMN, lexer.token());
        lexer.next(); assertEquals(MATH_OPERATOR, lexer.token());
        lexer.next(); assertEquals(PLACEHOLDER, lexer.token());

        lexer.next(); assertEquals(-1, lexer.token());
        lexer.next(); assertEquals(-1, lexer.token());
        lexer.next(); assertEquals(-1, lexer.token());
    }

    @Test
    public void returnsCorrectTokens2() throws TokenizationException {
        Lexer lexer = new Lexer();
        lexer.setInput(validExpressions[2].toCharArray());

        lexer.next(); assertEquals(COLUMN, lexer.token());
        lexer.next(); assertEquals(MATH_OPERATOR, lexer.token());
        lexer.next(); assertEquals(PLACEHOLDER, lexer.token());

        lexer.next(); assertEquals(LOGICAL_OPERATOR, lexer.token());

        lexer.next(); assertEquals(COLUMN, lexer.token());
        lexer.next(); assertEquals(MATH_OPERATOR, lexer.token());
        lexer.next(); assertEquals(PLACEHOLDER, lexer.token());

        lexer.next(); assertEquals(-1, lexer.token());
        lexer.next(); assertEquals(-1, lexer.token());
        lexer.next(); assertEquals(-1, lexer.token());
    }

    @Test
    public void returnsCorrectTokens3() throws TokenizationException {
        Lexer lexer = new Lexer();
        lexer.setInput(validExpressions[3].toCharArray());

        lexer.next(); assertEquals(COLUMN, lexer.token());
        lexer.next(); assertEquals(MATH_OPERATOR, lexer.token());
        lexer.next(); assertEquals(PLACEHOLDER, lexer.token());

        lexer.next(); assertEquals(LOGICAL_OPERATOR, lexer.token());

        lexer.next(); assertEquals(COLUMN, lexer.token());
        lexer.next(); assertEquals(MATH_OPERATOR, lexer.token());
        lexer.next(); assertEquals(PLACEHOLDER, lexer.token());

        lexer.next(); assertEquals(LOGICAL_OPERATOR, lexer.token());

        lexer.next(); assertEquals(COLUMN, lexer.token());
        lexer.next(); assertEquals(MATH_OPERATOR, lexer.token());
        lexer.next(); assertEquals(PLACEHOLDER, lexer.token());

        lexer.next(); assertEquals(-1, lexer.token());
        lexer.next(); assertEquals(-1, lexer.token());
        lexer.next(); assertEquals(-1, lexer.token());
    }

    @Test
    public void throwsExceptionToBadExpression() throws TokenizationException {
        Lexer lexer = new Lexer();

        for (String[] badExpression : badExpressions) {
            lexer.setInput(badExpression[1].toCharArray());

            for (int i = 0; i < Integer.valueOf(badExpression[0]); i++) {
                if (i == Integer.valueOf(badExpression[0]) - 1) {
                    assertThrows(TokenizationException.class, lexer::next);
                } else {
                    lexer.next();
                }
            }
        }
    }
}