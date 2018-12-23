package database.query.transformer;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.LinkedList;
import java.util.List;

import database.DataType;
import database.ExternalRow;
import database.Value;
import database.contract.QueryTransformer;
import database.query.entity.InsertRowsQuery;

public class InsertRowsQueryTransformer implements QueryTransformer<InsertRowsQuery, JSONObject> {
    private final Schema mJsonSchema;

    public InsertRowsQueryTransformer() {
        mJsonSchema = SchemaLoader.load(
                new JSONObject(
                        new JSONTokener(System.class.getResourceAsStream("/query/parser/insert_rows_query_schema.json"))
                )
        );
    }

    @Override
    public InsertRowsQuery buildQuery(JSONObject input) {
        InsertRowsQuery query;

        mJsonSchema.validate(input);

        List<String> columns = gatherColumns(input.getJSONArray("columns"));
        List<ExternalRow> externalRows = gatherData(input.getJSONArray("data"), columns.size());

        query = new InsertRowsQuery(
                input.getString("name"),
                columns,
                externalRows
        );

        return query;
    }

    @Override
    public boolean transforms(JSONObject input) {
        return input.getString("type").equals("insert_rows");
    }

    private List<ExternalRow> gatherData(JSONArray array, int capacity) {
        Object value;
        JSONArray row;
        ExternalRow buffer;
        List<ExternalRow> rows = new LinkedList<>();

        for (Object o : array) {
            buffer = new ExternalRow(capacity);

            row = (JSONArray)o;

            for (int i = 0; i < row.length(); i++) {
                value = row.get(i);

                if (value instanceof Integer) {
                    buffer.add(new Value((Integer) value, DataType.INTEGER));
                } else if (value instanceof String) {
                    buffer.add(new Value(((String) value).toCharArray(), DataType.STRING));
                }
            }

            rows.add(buffer);
        }

        return rows;
    }

    private List<String> gatherColumns(JSONArray array) {
        List<String> columns = new LinkedList<>();

        for (Object o : array) {
            columns.add((String) o);
        }

        return columns;
    }
}
