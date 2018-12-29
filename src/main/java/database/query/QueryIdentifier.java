package database.query;

import database.QueryType;

public class QueryIdentifier {
    public QueryType identify(String query) {
        char[] buffer = new char[20];

        for (int i = 0; i < query.length(); i++) {
            if (query.charAt(i) != 32 || i < 20) {
                buffer[i] = (char)(((int)query.charAt(i)) - 32);
            } else {
                break;
            }
        }

        return QueryType.valueOf(String.valueOf(buffer));
    }
}
