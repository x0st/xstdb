package database.query.transformer;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Iterator;

import database.DataType;
import database.Value;
import database.contract.QueryTransformer;
import database.query.entity.SelectRowsQuery;
import database.query.expression.PlaceholderObject;

public class SelectRowsQueryTransformer implements QueryTransformer<SelectRowsQuery, JSONObject> {
    private final Schema mJsonSchema;

    public SelectRowsQueryTransformer() {
        mJsonSchema = SchemaLoader.load(
                new JSONObject(
                        new JSONTokener(System.class.getResourceAsStream("/query/parser/select_rows_query_schema.json"))
                )
        );
    }

    @Override
    public SelectRowsQuery buildQuery(JSONObject input) {
        SelectRowsQuery query;

        mJsonSchema.validate(input);

        query = new SelectRowsQuery(
                input.getString("name"),
                buildPlaceholderObject(input.getJSONObject("attributes")),
                input.getString("expression")
        );

        return query;
    }

    @Override
    public boolean transforms(JSONObject input) {
        return input.getString("type").equals("select_rows");
    }

    private PlaceholderObject buildPlaceholderObject(JSONObject phs) {
        if (phs == null) {
            return null;
        }

        PlaceholderObject placeholderObject = new PlaceholderObject(phs.length());
        Iterator<String> keys = phs.keys();
        Object value;
        String key;

        while (keys.hasNext()) {
            key = keys.next();
            value = phs.get(key);

            if (value instanceof String) {
                placeholderObject.set(key, new Value(((String) value).toCharArray(), DataType.STRING));
            } else if (value instanceof Integer) {
                placeholderObject.set(key, new Value((Integer) value, DataType.INTEGER));
            }
        }

        return placeholderObject;
    }
}
