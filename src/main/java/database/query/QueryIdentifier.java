package database.query;

import database.QueryType;
import database.contract.LexerInterface;
import database.exception.BadQueryException;
import database.exception.TokenizationException;
import database.rawquery.parser.Token;

public class QueryIdentifier {
    public QueryType identify(LexerInterface lexer) throws BadQueryException {
        lexer.next();

        if (lexer.token() != Token.WORD) {
            throw BadQueryException.badSyntax();
        }

        for (QueryType queryType : QueryType.values()) {
            if (String.valueOf(lexer.lexeme()).toUpperCase().equals(queryType.name())) {
                return queryType;
            }
        }

        throw BadQueryException.badSyntax();
    }
}
