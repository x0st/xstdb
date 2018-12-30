package database.query;

import database.QueryType;
import database.contract.LexerInterface;
import database.exception.BadQueryException;
import database.exception.TokenizationException;
import database.query.parser.Token;

public class QueryIdentifier {
    public QueryType identify(LexerInterface lexer) throws BadQueryException {
        try {
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
        } catch (TokenizationException e) {
            throw BadQueryException.badSyntax();
        }
    }
}
