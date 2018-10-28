package query;

import org.json.JSONArray;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;

import exception.BadQueryException;
import scheme.ColumnScheme;
import scheme.TableScheme;

public class CreateTableQuery {
    private TableScheme tableScheme;

    public CreateTableQuery(JSONObject jo) throws BadQueryException {
        String tableName;
        List<ColumnScheme> columnSchemes = new ArrayList<ColumnScheme>();

        validateTableNameKey(jo);
        validateColumnsKey(jo);

        tableName = jo.getString("name");

        JSONArray columns = jo.getJSONArray("columns");

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

        tableScheme = new TableScheme(tableName, 0, columnSchemes);
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

    public TableScheme getTableScheme() {
        return tableScheme;
    }
}
