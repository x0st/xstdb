package database.query.transformer;

import org.json.JSONObject;

import database.contract.QueryTransformer;
import database.query.entity.DescribeTableQuery;

public class DescribeTableQueryTransformer implements QueryTransformer<DescribeTableQuery, JSONObject> {
    @Override
    public DescribeTableQuery buildQuery(JSONObject input) {
        return new DescribeTableQuery(input.getString("name"));
    }

    @Override
    public boolean transforms(JSONObject input) {
        return input.getString("type").equals("describe_table");
    }
}
