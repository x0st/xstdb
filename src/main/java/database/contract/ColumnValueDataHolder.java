package database.contract;

public interface ColumnValueDataHolder {
    public char[] getColumnName();
    public char[] getCharArrayValue();
    public byte getDataType();
    public int getIntegerValue();
}
