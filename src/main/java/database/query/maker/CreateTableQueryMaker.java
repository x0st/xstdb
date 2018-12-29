package database.query.maker;

import database.DataType;
import database.QueryType;
import database.contract.LexerInterface;
import database.contract.QueryMaker;
import database.exception.BadQueryException;
import database.exception.BuilderException;
import database.query.entity.CreateTableQuery;
import database.query.parser.Token;
import database.scheme.ColumnScheme;

public class CreateTableQueryMaker extends BaseQueryMaker implements QueryMaker<CreateTableQuery> {
    @Override
    public CreateTableQuery make(LexerInterface lexer) throws BadQueryException {
        CreateTableQuery.Builder builder = CreateTableQuery.builder();

        builder.table(takeTableName(lexer));

        while (lexer.next()) {
            builder.column(takeColumnScheme(lexer));
        }

        try {
            return builder.build();
        } catch (BuilderException e) {
            throw BadQueryException.badSyntax();
        }
    }

    @Override
    public boolean supports(QueryType queryType) {
        return queryType == QueryType.ADD;
    }

    private ColumnScheme takeColumnScheme(LexerInterface lexer) throws BadQueryException {
        ColumnScheme.Builder builder = ColumnScheme.builder();

        assertToken(Token.LEFT_BRACKET, lexer);

        lexer.next();
        assertToken(Token.WORD, lexer);

        builder.name(lexer.lexeme());

        lexer.next();
        assertToken(Token.WORD, lexer);

        try {
            builder.dataType(DataType.valueOf(String.valueOf(lexer.lexeme())));
        } catch (IllegalArgumentException e) {
            throw BadQueryException.badSyntax();
        }

        lexer.next();
        assertToken(Token.RIGHT_BRACKET, lexer);

        try {
            return builder.build();
        } catch (BuilderException e) {
            throw BadQueryException.badSyntax();
        }
    }
}
