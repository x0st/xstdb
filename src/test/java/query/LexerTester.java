package query;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import database.exception.TokenizationException;
import database.rawquery.parser.Lexer;
import database.rawquery.parser.Token;

public class LexerTester {

    @Test
    public void parsesCorrectly0() throws TokenizationException {
        Lexer lexer = new Lexer();
        lexer.setInput("add users [id 1] [login 2]".toCharArray());

        lexer.next();
        assertEquals(lexer.token(), Token.WORD);
        assertEquals("add",   String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.WORD);
        assertEquals("users", String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.LEFT_BRACKET);
        assertEquals("[",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.WORD);
        assertEquals("id",    String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.NUMBER);
        assertEquals("1",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.RIGHT_BRACKET);
        assertEquals("]",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.LEFT_BRACKET);
        assertEquals("[",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.WORD);
        assertEquals("login", String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.NUMBER);
        assertEquals("2",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.RIGHT_BRACKET);
        assertEquals("]",     String.valueOf(lexer.lexeme()));
    }

    @Test
    public void parsesCorrectly1() throws TokenizationException {
        Lexer lexer = new Lexer();

        lexer.setInput("put users [id name] [1 \"pavlo nbm t78#\"] [25 \"dmitro\"] [-100 \"haha\"]".toCharArray());

        lexer.next();
        assertEquals(lexer.token(), Token.WORD);
        assertEquals("put",   String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.WORD);
        assertEquals("users", String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.LEFT_BRACKET);
        assertEquals("[",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.WORD);
        assertEquals("id",    String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.WORD);
        assertEquals("name",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.RIGHT_BRACKET);
        assertEquals("]",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.LEFT_BRACKET);
        assertEquals("[",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.NUMBER);
        assertEquals("1", String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.QUOTED_STRING);
        assertEquals("pavlo nbm t78#",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.RIGHT_BRACKET);
        assertEquals("]",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.LEFT_BRACKET);
        assertEquals("[",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.NUMBER);
        assertEquals("25", String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.QUOTED_STRING);
        assertEquals("dmitro",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.RIGHT_BRACKET);
        assertEquals("]",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.LEFT_BRACKET);
        assertEquals("[",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.NUMBER);
        assertEquals("-100", String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.QUOTED_STRING);
        assertEquals("haha",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.RIGHT_BRACKET);
        assertEquals("]",     String.valueOf(lexer.lexeme()));

    }

    @Test
    public void parsesCorrectly2() throws TokenizationException {
        Lexer lexer = new Lexer();
        lexer.setInput("show users".toCharArray());

        lexer.next();
        assertEquals(lexer.token(), Token.WORD);
        assertEquals("show",   String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.WORD);
        assertEquals("users", String.valueOf(lexer.lexeme()));
    }

    @Test
    public void parsesCorrectly3() throws TokenizationException {
        Lexer lexer = new Lexer();

        lexer.setInput("get users [name <> :name & id > :id | name = :name] where [:name = \"#123 test\" & :id = -24]".toCharArray());

        lexer.next();
        assertEquals(lexer.token(), Token.WORD);
        assertEquals("get",   String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.WORD);
        assertEquals("users", String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.LEFT_BRACKET);
        assertEquals("[",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.WORD);
        assertEquals("name",    String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.MATH_OPERATOR);
        assertEquals("<>",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.PLACEHOLDER);
        assertEquals(":name",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.LOGICAL_OPERATOR);
        assertEquals("&",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.WORD);
        assertEquals("id", String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.MATH_OPERATOR);
        assertEquals(">",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.PLACEHOLDER);
        assertEquals(":id",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.LOGICAL_OPERATOR);
        assertEquals("|",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.WORD);
        assertEquals("name", String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.MATH_OPERATOR);
        assertEquals("=",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.PLACEHOLDER);
        assertEquals(":name",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.RIGHT_BRACKET);
        assertEquals("]",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.WORD);
        assertEquals("where", String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.LEFT_BRACKET);
        assertEquals("[",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.PLACEHOLDER);
        assertEquals(":name",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.MATH_OPERATOR);
        assertEquals("=",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.QUOTED_STRING);
        assertEquals("#123 test",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.LOGICAL_OPERATOR);
        assertEquals("&",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.PLACEHOLDER);
        assertEquals(":id",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.MATH_OPERATOR);
        assertEquals("=",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.NUMBER);
        assertEquals("-24",     String.valueOf(lexer.lexeme()));
        lexer.next();
        assertEquals(lexer.token(), Token.RIGHT_BRACKET);
        assertEquals("]",     String.valueOf(lexer.lexeme()));
    }
}