package database.contract;

import org.json.JSONObject;

public interface QueryTransformer<O, I> {
    public O buildQuery(I input);
    public boolean transforms(I input);
}
