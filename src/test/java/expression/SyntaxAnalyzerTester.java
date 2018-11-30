package expression;

import org.junit.jupiter.api.Test;

import database.exception.ExpressionSyntaxException;
import database.exception.TokenizationException;
import database.expression.parser.ASTNode;
import database.expression.parser.Lexer;
import database.expression.parser.SyntaxAnalyzer;

import static database.expression.Token.Type.MATH_OPERATOR;
import static database.expression.Token.Type.LOGICAL_OPERATOR;
import static database.expression.Token.Type.COLUMN;
import static database.expression.Token.Type.PLACEHOLDER;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SyntaxAnalyzerTester {

    private String[] validExpressions = new String[] {
            "n > :n & id = :id | name < :name",
            "t = :test",
            "test = :test & b = :b"
    };

    private String[] badExpressions = new String[] {
            "n = :n = ",
            " :n | ",
            "name > :name & name ",
            "name > :name | :name ",
    };

    @Test
    public void throwsExceptionToBadExpression() {
        Lexer lexer = new Lexer();
        lexer.setInput(badExpressions[0].toCharArray());

        SyntaxAnalyzer sa = new SyntaxAnalyzer(lexer);

        assertThrows(ExpressionSyntaxException.class, sa::ast);
        lexer.setInput(badExpressions[1].toCharArray());
        assertThrows(ExpressionSyntaxException.class, sa::ast);
        lexer.setInput(badExpressions[2].toCharArray());
        assertThrows(ExpressionSyntaxException.class, sa::ast);
        lexer.setInput(badExpressions[3].toCharArray());
        assertThrows(ExpressionSyntaxException.class, sa::ast);
    }

    @Test
    public void buildsASTCorrectly0() throws TokenizationException, ExpressionSyntaxException {
        Lexer lexer = new Lexer();
        lexer.setInput(validExpressions[0].toCharArray());

        SyntaxAnalyzer sa = new SyntaxAnalyzer(lexer);

        ASTNode astnode = sa.ast();

        assertEquals(LOGICAL_OPERATOR, astnode.getToken());

        assertEquals(MATH_OPERATOR, astnode.getRight().getToken());
        assertEquals(PLACEHOLDER, astnode.getRight().getRight().getToken());
        assertEquals(COLUMN, astnode.getRight().getLeft().getToken());

        assertEquals(LOGICAL_OPERATOR, astnode.getLeft().getToken());

        assertEquals(MATH_OPERATOR, astnode.getLeft().getLeft().getToken());
        assertEquals(COLUMN, astnode.getLeft().getLeft().getLeft().getToken());
        assertEquals(PLACEHOLDER, astnode.getLeft().getLeft().getRight().getToken());

        assertEquals(MATH_OPERATOR, astnode.getLeft().getRight().getToken());
        assertEquals(COLUMN, astnode.getLeft().getRight().getLeft().getToken());
        assertEquals(PLACEHOLDER, astnode.getLeft().getRight().getRight().getToken());
    }

    @Test
    public void buildsASTCorrectly1() throws TokenizationException, ExpressionSyntaxException {
        Lexer lexer = new Lexer();
        lexer.setInput(validExpressions[1].toCharArray());

        SyntaxAnalyzer sa = new SyntaxAnalyzer(lexer);

        ASTNode astnode = sa.ast();

        assertEquals(MATH_OPERATOR, astnode.getToken());
        assertEquals(PLACEHOLDER, astnode.getRight().getToken());
        assertEquals(COLUMN, astnode.getLeft().getToken());
    }

    @Test
    public void buildsASTCorrectly2() throws TokenizationException, ExpressionSyntaxException {
        Lexer lexer = new Lexer();
        lexer.setInput(validExpressions[2].toCharArray());

        SyntaxAnalyzer sa = new SyntaxAnalyzer(lexer);

        ASTNode astnode = sa.ast();

        assertEquals(LOGICAL_OPERATOR, astnode.getToken());

        assertEquals(MATH_OPERATOR, astnode.getLeft().getToken());
        assertEquals(PLACEHOLDER, astnode.getLeft().getRight().getToken());
        assertEquals(COLUMN, astnode.getLeft().getLeft().getToken());

        assertEquals(MATH_OPERATOR, astnode.getRight().getToken());
        assertEquals(PLACEHOLDER, astnode.getRight().getRight().getToken());
        assertEquals(COLUMN, astnode.getRight().getLeft().getToken());
    }
}
