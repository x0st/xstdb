package database.contract;

public interface PlaceholderMap {
    public void set(String columnName, PlaceholderValueDataHolder columnValueSet);
    public PlaceholderValueDataHolder get(String columnName);
}
