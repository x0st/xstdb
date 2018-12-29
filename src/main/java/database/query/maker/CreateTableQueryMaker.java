package database.query.maker;

import database.DataType;
import database.contract.LexerInterface;
import database.contract.QueryMaker;
import database.exception.BadQueryException;
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

        return builder.build();
    }

    private ColumnScheme takeColumnScheme(LexerInterface lexer) throws BadQueryException {
        ColumnScheme.Builder builder = ColumnScheme.builder();

        assertToken(Token.LEFT_BRACKET, lexer);

        lexer.next();
        assertToken(Token.WORD, lexer);

        builder.name(lexer.lexeme());

        lexer.next();
        assertToken(Token.WORD, lexer);

        builder.dataType(DataType.valueOf(String.valueOf(lexer.lexeme())));

        lexer.next();
        assertToken(Token.RIGHT_BRACKET, lexer);

        return builder.build();
    }
}
