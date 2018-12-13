package database.scheme;

import java.util.Arrays;

import database.DataType;

public class ColumnScheme {
    private char[] mName;
    private int mSize;
    private DataType mType;
    private int mNameHash;

    public ColumnScheme(char[] name, DataType type, int size) {
        mName = name;
        mType = type;
        mSize = size;
        mNameHash = Arrays.hashCode(name);
    }

    public ColumnScheme(char[] name, DataType type) {
        mName = name;
        mType = type;
        mSize = type.getSize();
        mNameHash = Arrays.hashCode(name);
    }

    public char[] getName() {
        return mName;
    }

    public DataType getType() {
        return mType;
    }

    public int getSize() {
        return mSize;
    }

    public int getNameHash() {
        return mNameHash;
    }
}
