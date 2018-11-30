package database.contract;

import org.json.JSONObject;

import database.exception.BadQueryException;

public interface QueryParser<T> {
    public T parseFromJSON(JSONObject jsonObject) throws BadQueryException;
}
