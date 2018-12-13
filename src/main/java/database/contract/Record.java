package database.contract;

public interface Record {
    public void set(String columnName, ValueHolder columnValueDataHolder);
    public ValueHolder get(String columnName);
    public byte size();
}
