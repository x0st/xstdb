package database.expression.parser;

import database.exception.ExpressionSyntaxException;
import database.exception.TokenizationException;

import static database.expression.Token.Type.COLUMN;
import static database.expression.Token.Type.LOGICAL_OPERATOR;
import static database.expression.Token.Type.MATH_OPERATOR;
import static database.expression.Token.Type.PLACEHOLDER;

public class SyntaxAnalyzer {
    private final Lexer mLexer;

    public SyntaxAnalyzer(Lexer lexer) {
        mLexer = lexer;
    }

    private ASTNode token(final byte desiredToken) throws ExpressionSyntaxException {
        if (mLexer.token() != desiredToken) {
            throw new ExpressionSyntaxException(
                    String.format("Bad syntax of the expression: %s", String.valueOf(mLexer.getInput()))
            );
        }

        return new ASTNode(mLexer.token(), mLexer.lexeme());
    }

    private ASTNode expr() throws TokenizationException, ExpressionSyntaxException {
        ASTNode buffer = token(COLUMN);
        mLexer.next();
        buffer = token(MATH_OPERATOR).setLeft(buffer);
        mLexer.next();
        buffer.setRight(token(PLACEHOLDER));

        return buffer;
    }


    public ASTNode ast() throws TokenizationException, ExpressionSyntaxException {

        // 1 + 1 + 1
        //
        //        +
        //      /   \
        //     /     \
        //    +       +
        //   / \     / \
        //  1   1   1   1

        // id = :id & name = :name | f > :b

        //
        //            |
        //          /   \
        //         /     \
        //        &       >
        //       / \     / \
        //      /   \   f  :f
        //     /     \
        //    =       =
        //   / \     / \
        // id  :id  b  :b

        mLexer.next();
        ASTNode root = expr();

        while (mLexer.next()) {
            root = token(LOGICAL_OPERATOR).setLeft(root);
            mLexer.next();
            root.setRight(expr());
        }

        return root;
    }
}
