package query;

import org.json.JSONObject;

import exception.BadQueryException;

public class DescribeTableQuery {
    private String tableName;

    public DescribeTableQuery(JSONObject jo) throws BadQueryException {
        validateTableNameKey(jo);

        tableName = jo.getString("name");
    }

    private void validateTableNameKey(JSONObject jo) throws BadQueryException {
        if (!jo.has("name")) {
            throw new BadQueryException("No name specified.");
        } else if (!(jo.opt("name") instanceof String)) {
            throw new BadQueryException("The given name cannot be read.");
        }
    }

    public String getTableName() {
        return tableName;
    }
}
