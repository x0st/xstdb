package database.query.maker;

import database.DataType;
import database.QueryType;
import database.Value;
import database.contract.LexerInterface;
import database.contract.QueryMaker;
import database.exception.BadQueryException;
import database.query.entity.DeleteRowsQuery;
import database.query.expression.PlaceholderObject;
import database.query.expression.parser.ASTNode;
import database.query.parser.Lexer;
import database.query.parser.Token;

public class DeleteRowsQueryMaker extends BaseQueryMaker implements QueryMaker<DeleteRowsQuery> {
    @Override
    public DeleteRowsQuery make(LexerInterface lexer) throws BadQueryException {
        String tableName;
        ASTNode astNode = null;
        PlaceholderObject predicateValues = null;

        // parse '%table_name%' out of 'delete %table_name% %predicate% where %placeholders%'
        tableName = String.valueOf(takeTableName(lexer));

        // if there is predicate
        if (lexer.next()) {
            // take the predicate
            astNode = takePredicate(lexer);

            // between predicate and placeholders there must be a word 'where'
            lexer.next();
            assertToken(Token.WORD, lexer);
            assertLexeme("where", lexer);

            // take values for predicate
            lexer.next();
            predicateValues = takePredicateValues(lexer);
        }

        return new DeleteRowsQuery(tableName, predicateValues, astNode);
    }

    /**
     * Parse '%placeholders%' out of 'delete %table_name% %predicate% where %placeholders%'.
     */
    private PlaceholderObject takePredicateValues(LexerInterface lexer) throws BadQueryException {
        assertToken(Token.LEFT_BRACKET, lexer);

        PlaceholderObject.Builder builder = PlaceholderObject.builder();

        lexer.next();
        putPlaceholderIntoBuilder(lexer, builder);

        while (true) {
            lexer.next();

            if (lexer.token() == Token.LOGICAL_OPERATOR) {
                assertLexeme("&", lexer);

                lexer.next();
                putPlaceholderIntoBuilder(lexer, builder);
            } else if (lexer.token() == Token.RIGHT_BRACKET) {
                break;
            } else {
                throw BadQueryException.expectedFound("one of: &, ]", String.valueOf(lexer.lexeme()));
            }
        }

        return builder.build();
    }

    /**
     * Parse next placeholder and put it into the given builder.
     * Example: ':name = alex'. The ':name' and 'alex' are put into the given builder.
     */
    private void putPlaceholderIntoBuilder(LexerInterface lexer, PlaceholderObject.Builder builder) throws BadQueryException {
        assertToken(Token.PLACEHOLDER, lexer);

        builder.placeholder(String.valueOf(lexer.lexeme()));

        lexer.next();
        assertToken(Token.MATH_OPERATOR, lexer);
        assertLexeme("=", lexer);

        lexer.next();

        if (lexer.token() == Token.WORD || lexer.token() == Token.QUOTED_STRING) {
            builder.value(new Value(lexer.lexeme(), DataType.STRING));
        } else if (lexer.token() == Token.NUMBER) {
            builder.value(new Value(((Lexer)lexer).lexemeAsInt(), DataType.INTEGER));
        } else {
            throw BadQueryException.expectedFound("a value", String.valueOf(lexer.lexeme()));
        }
    }

    /**
     * Parse '%predicate%' out of 'delete %table_name% %predicate% where %placeholders%'.
     */
    private ASTNode takePredicate(LexerInterface lexer) throws BadQueryException {
        assertToken(Token.LEFT_BRACKET, lexer);

        lexer.next();
        ASTNode root = takeCondition(lexer);

        while (true) {
            lexer.next();

            if (lexer.token() == Token.LOGICAL_OPERATOR) {
                root = astNode(Token.LOGICAL_OPERATOR, lexer).setLeft(root);
                lexer.next();
                root.setRight(takeCondition(lexer));
            } else if (lexer.token() == Token.RIGHT_BRACKET) {
                break;
            } else {
                throw BadQueryException.expectedFound("one of: |, &, ]", String.valueOf(lexer.lexeme()));
            }
        }

        return root;
    }

    /**
     * Parse next condition.
     * Example: 'name = :name'.
     */
    private ASTNode takeCondition(LexerInterface lexer) throws BadQueryException {
        ASTNode buffer = astNode(Token.WORD, lexer);
        lexer.next();
        buffer = astNode(Token.MATH_OPERATOR, lexer).setLeft(buffer);
        lexer.next();
        buffer.setRight(astNode(Token.PLACEHOLDER, lexer));

        return buffer;
    }

    /**
     * Just a convenient way to instantiate {@link ASTNode}.
     */
    private ASTNode astNode(byte token, LexerInterface lexer) throws BadQueryException {
        assertToken(token, lexer);

        return new ASTNode(lexer.token(), lexer.lexeme());
    }

    @Override
    public boolean supports(QueryType queryType) {
        return queryType == QueryType.DELETE;
    }
}
