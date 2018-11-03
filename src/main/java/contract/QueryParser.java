package contract;

import org.json.JSONObject;

import exception.BadQueryException;

public interface QueryParser<T> {
    public T parseFromJSON(JSONObject jsonObject) throws BadQueryException;
}
