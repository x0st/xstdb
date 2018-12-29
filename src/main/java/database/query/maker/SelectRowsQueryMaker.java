package database.query.maker;

import database.DataType;
import database.Value;
import database.contract.LexerInterface;
import database.contract.QueryMaker;
import database.exception.BadQueryException;
import database.query.entity.SelectRowsQuery;
import database.query.expression.PlaceholderObject;
import database.query.expression.parser.ASTNode;
import database.query.parser.Lexer;
import database.query.parser.Token;

public class SelectRowsQueryMaker extends BaseQueryMaker implements QueryMaker<SelectRowsQuery> {
    @Override
    public SelectRowsQuery make(LexerInterface lexer) throws BadQueryException {
        String tableName;
        ASTNode astNode = null;
        PlaceholderObject object = null;

        tableName = String.valueOf(takeTableName(lexer));

        if (lexer.next()) {
            astNode = takeExpression(lexer);
            object = takePlaceholders(lexer);
        }

        return new SelectRowsQuery(tableName, object, astNode);
    }

    private PlaceholderObject takePlaceholders(LexerInterface lexer) throws BadQueryException {
        PlaceholderObject.Builder builder = PlaceholderObject.builder();

        lexer.next();
        assertToken(Token.LEFT_BRACKET, lexer);

        lexer.next();

        while (true) {
            assertToken(Token.PLACEHOLDER, lexer);

            builder.placeholder(String.valueOf(lexer.lexeme()));

            lexer.next();

            switch (lexer.token()) {
                case Token.NUMBER:
                    builder.value(new Value(((Lexer)lexer).lexemeAsInt(), DataType.INTEGER));
                    break;
                case Token.WORD:
                case Token.QUOTED_STRING:
                    builder.value(new Value(lexer.lexeme(), DataType.STRING));
                    break;
                default:
                    throw new BadQueryException("Bad syntax");
            }

            lexer.next();

            if (lexer.token() == Token.RIGHT_BRACKET) {
                break;
            }
        }

        return builder.build();
    }

    private ASTNode takeExpression(LexerInterface lexer) throws BadQueryException {
        assertToken(Token.LEFT_BRACKET, lexer);

        lexer.next();
        ASTNode root = expr(lexer);

        while (true) {
            lexer.next();

            if (lexer.token() == Token.LOGICAL_OPERATOR) {
                root = astNode(Token.LOGICAL_OPERATOR, lexer).setLeft(root);
                lexer.next();
                root.setRight(expr(lexer));
            } else if (lexer.token() == Token.RIGHT_BRACKET) {
                break;
            } else {
                throw new BadQueryException("Bad syntax");
            }
        }

        return root;
    }

    private ASTNode expr(LexerInterface lexer) throws BadQueryException {
        ASTNode buffer = astNode(Token.WORD, lexer);
        lexer.next();
        buffer = astNode(Token.MATH_OPERATOR, lexer).setLeft(buffer);
        lexer.next();
        buffer.setRight(astNode(Token.PLACEHOLDER, lexer));

        return buffer;
    }

    private ASTNode astNode(byte token, LexerInterface lexer) throws BadQueryException {
        assertToken(token, lexer);

        return new ASTNode(lexer.token(), lexer.lexeme());
    }
}
