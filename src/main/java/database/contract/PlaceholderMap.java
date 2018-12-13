package database.contract;

public interface PlaceholderMap {
    public void set(String columnName, ValueHolder columnValueSet);
    public ValueHolder get(String columnName);
}
