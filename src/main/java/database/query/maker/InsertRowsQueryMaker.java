package database.query.maker;

import java.util.LinkedList;
import java.util.List;

import database.DataType;
import database.ExternalRow;
import database.Value;
import database.contract.LexerInterface;
import database.contract.QueryMaker;
import database.exception.BadQueryException;
import database.query.entity.InsertRowsQuery;
import database.query.parser.Lexer;
import database.query.parser.Token;

public class InsertRowsQueryMaker extends BaseQueryMaker implements QueryMaker<InsertRowsQuery> {
    @Override
    public InsertRowsQuery make(LexerInterface lexer) throws BadQueryException {
        InsertRowsQuery.Builder builder = InsertRowsQuery.builder();

        builder.table(takeTableName(lexer));

        List<String> columns = takeColumns(lexer);

        builder.columns(columns);

        while (lexer.next()) {
            builder.row(takeRecord(lexer, columns.size()));
        }

        return builder.build();
    }

    private ExternalRow takeRecord(LexerInterface lexer, int capacity) throws BadQueryException {
        ExternalRow externalRow = new ExternalRow(capacity);

        assertToken(Token.LEFT_BRACKET, lexer);

        for (int i = 0; i < capacity; i++) {
            lexer.next();

            if (lexer.token() != Token.WORD && lexer.token() != Token.NUMBER && lexer.token() != Token.QUOTED_STRING) {
                throw new BadQueryException("Bad syntax");
            }

            if (lexer.token() == Token.NUMBER) {
                externalRow.add(new Value(((Lexer)lexer).lexemeAsInt(), DataType.INTEGER));
            } else {
                externalRow.add(new Value(lexer.lexeme(), DataType.STRING));
            }
        }

        lexer.next();
        assertToken(Token.RIGHT_BRACKET, lexer);

        return externalRow;
    }

    private List<String> takeColumns(LexerInterface lexer) throws BadQueryException {
        List<String> columns = new LinkedList<>();

        lexer.next();
        assertToken(Token.LEFT_BRACKET, lexer);

        while (true) {
            lexer.next();

            if (lexer.token() == Token.WORD) {
                columns.add(String.valueOf(lexer.lexeme()));
            } else if (lexer.token() == Token.RIGHT_BRACKET) {
                break;
            } else {
                throw new BadQueryException("Bad syntax");
            }
        }

        return columns;
    }
}
