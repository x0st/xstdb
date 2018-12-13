package database.contract;

import database.DataType;

public interface ValueHolder {
    public char[] getCharArrayValue();
    public DataType getDataType();
    public int getIntegerValue();
    public int hash();
}
