package queryparser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import contract.QueryParser;
import exception.BadQueryException;
import query.CreateTableQuery;
import scheme.ColumnScheme;

public class CreateTableQueryParser implements QueryParser<CreateTableQuery> {

    @Override
    public CreateTableQuery parseFromJSON(JSONObject jsonObject) throws BadQueryException {
        JSONArray columns;
        String tableName = null;
        List<ColumnScheme> columnSchemes = new ArrayList<>();

        validateTableNameKey(jsonObject);
        validateColumnsKey(jsonObject);

        tableName = jsonObject.getString("name");
        columns = jsonObject.getJSONArray("columns");

        for (Object col : columns) {
            JSONObject column = (JSONObject) col;
            Integer size;

            // integer
            if (column.getInt("type") == 1) {
                size = 4;
            } // string
            else if (column.getInt("type") == 2) {
                size = 1024;
            } // never gonna happen, but...
            else {
                size = 4;
            }

            columnSchemes.add(
                    new ColumnScheme(
                            column.getString("name"),
                            column.getInt("type"),
                            size
                    )
            );
        }

        return new CreateTableQuery(tableName, columnSchemes);
    }

    private void validateColumnsKey(JSONObject jo) throws BadQueryException {
        if (!jo.has("columns")) {
            throw new BadQueryException("No columns specified.");
        } else if (!(jo.opt("columns") instanceof JSONArray)) {
            throw new BadQueryException("The given columns cannot be read.");
        }

        JSONArray columns = jo.getJSONArray("columns");

        for (Object col : columns) {
            JSONObject column = (JSONObject) col;

            if (!column.has("name")) {
                throw new BadQueryException("All the given columns must have a name specified.");
            } else if (!(column.opt("name") instanceof String)) {
                throw new BadQueryException("A column name cannot be read.");
            } else if (!column.has("type")) {
                throw new BadQueryException("All the given columns must have a type specified.");
            } else if (!(column.opt("type") instanceof Integer)) {
                throw new BadQueryException("A column type cannot be read.");
            }

            if (column.getInt("type") != 1 && column.getInt("type") != 2) {
                throw new BadQueryException("An undefined column type.");
            }
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
