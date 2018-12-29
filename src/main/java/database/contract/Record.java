package database.contract;

public interface Record {
    public void set(int index, ValueHolder columnValueDataHolder);
    public ValueHolder get(int index);
    public int size();
}
