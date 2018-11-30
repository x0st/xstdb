package database.contract;

public interface Record {
    public void set(String columnName, ColumnValueDataHolder columnValueDataHolder);
    public ColumnValueDataHolder get(String columnName);
}
