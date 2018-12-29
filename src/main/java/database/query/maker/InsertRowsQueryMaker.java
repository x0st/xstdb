package database.query.maker;

import java.util.LinkedList;
import java.util.List;

import database.DataType;
import database.QueryType;
import database.Row;
import database.Value;
import database.contract.LexerInterface;
import database.contract.QueryMaker;
import database.contract.Record;
import database.exception.BadQueryException;
import database.exception.BuilderException;
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

        try {
            return builder.build();
        } catch (BuilderException e) {
            throw BadQueryException.badSyntax();
        }
    }

    @Override
    public boolean supports(QueryType queryType) {
        return queryType == QueryType.PUT;
    }

    private Record takeRecord(LexerInterface lexer, int capacity) throws BadQueryException {
        Record externalRow = new Row(capacity);

        assertToken(Token.LEFT_BRACKET, lexer);

        for (int i = 0; i < capacity; i++) {
            lexer.next();

            if (lexer.token() != Token.WORD && lexer.token() != Token.NUMBER && lexer.token() != Token.QUOTED_STRING) {
                throw BadQueryException.badSyntax();
            }

            if (lexer.token() == Token.NUMBER) {
                externalRow.set(i, new Value(((Lexer)lexer).lexemeAsInt(), DataType.INTEGER));
            } else {
                externalRow.set(i, new Value(lexer.lexeme(), DataType.STRING));
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
                throw BadQueryException.badSyntax();
            }
        }

        return columns;
    }
}
