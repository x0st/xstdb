package database.queryparser;

import org.json.JSONObject;

import database.contract.QueryParser;
import database.exception.BadQueryException;
import database.query.DescribeTableQuery;

public class DescribeTableQueryParser implements QueryParser<DescribeTableQuery> {

    @Override
    public DescribeTableQuery parseFromJSON(JSONObject jsonObject) throws BadQueryException {
        validateTableNameKey(jsonObject);

        return new DescribeTableQuery(
                jsonObject.getString("name")
        );
    }

    private void validateTableNameKey(JSONObject jo) throws BadQueryException {
        if (!jo.has("name")) {
            throw new BadQueryException("No name specified.");
        } else if (!(jo.opt("name") instanceof String)) {
            throw new BadQueryException("The given name cannot be read.");
        }
    }
}
