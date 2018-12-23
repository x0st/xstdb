package database.contract;

import org.json.JSONObject;

public interface QueryParser<T> {
    public T fromString(String str);
}
