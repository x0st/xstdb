package database.query;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import database.contract.QueryParser;

public class Parser implements QueryParser<JSONObject> {
    Schema mSchema;

    public Parser() {
        mSchema = SchemaLoader.load(
                new JSONObject(
                        new JSONTokener(System.class.getResourceAsStream("/query/parser/query_schema.json"))
                )
        );
    }

    @Override
    public JSONObject fromString(String str) throws ValidationException {
        JSONObject query = new JSONObject(str);

        mSchema.validate(query);

        return query;
    }
}
