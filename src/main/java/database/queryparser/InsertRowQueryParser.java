package database.queryparser;

import org.json.JSONObject;

import database.contract.QueryParser;
import database.exception.BadQueryException;
import database.query.InsertRowQuery;

public class InsertRowQueryParser implements QueryParser<InsertRowQuery> {

    @Override
    public InsertRowQuery parseFromJSON(JSONObject jsonObject) throws BadQueryException {
        validateTableNameKey(jsonObject);
        validateDataKey(jsonObject);

        return new InsertRowQuery(
                jsonObject.getString("name"),
                jsonObject.getJSONObject("data").toMap()
        );
    }

    private void validateDataKey(JSONObject jo) throws BadQueryException {
        if (!jo.has("data")) {
            throw new BadQueryException("No data provided.");
        } else if (!(jo.opt("data") instanceof JSONObject)) {
            throw new BadQueryException("The given data cannot be read.");
        }
    }

    private void validateTableNameKey(JSONObject jo) throws BadQueryException {
        if (!jo.has("name")) {
            throw new BadQueryException("No name specified.");
        } else if (!(jo.opt("name") instanceof String)) {
            throw new BadQueryException("The given name cannot be read.");
        }
    }
}
