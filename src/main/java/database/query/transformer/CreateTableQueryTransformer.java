package database.query.transformer;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import database.DataType;
import database.contract.QueryTransformer;
import database.query.entity.CreateTableQuery;
import database.scheme.ColumnScheme;

public class CreateTableQueryTransformer implements QueryTransformer<CreateTableQuery, JSONObject> {
    private final Schema mJsonSchema;

    public CreateTableQueryTransformer() {
        mJsonSchema = SchemaLoader.load(
                new JSONObject(
                        new JSONTokener(System.class.getResourceAsStream("/query/parser/create_table_query_schema.json"))
                )
        );
    }

    @Override
    public CreateTableQuery buildQuery(JSONObject input) {
        CreateTableQuery query;

        mJsonSchema.validate(input);

        query = new CreateTableQuery(
                input.getString("name"),
                gatherColumns(input.getJSONArray("columns"))
        );

        return query;
    }

    @Override
    public boolean transforms(JSONObject input) {
        return input.getString("type").equals("create_table");
    }

    private ColumnScheme[] gatherColumns(JSONArray columns) {
        ColumnScheme[] columnSchemes = new ColumnScheme[columns.length()];

        for (int i = 0; i < columns.length(); i++) {
            columnSchemes[i] = new ColumnScheme(
                    columns.getJSONObject(i).getString("name").toCharArray(),
                    DataType.valueOf((byte)columns.getJSONObject(i).getInt("type"))
            );
        }

        return columnSchemes;
    }
}
