package database.scheme;

import java.util.Arrays;

import database.DataType;
import database.exception.BuilderException;

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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private char[] name = null;
        private DataType dataType = null;

        public Builder name(char[] value) {
            name = value;
            return this;
        }

        public Builder dataType(DataType value) {
            dataType = value;
            return this;
        }

        public ColumnScheme build() throws BuilderException {
            if (name == null || dataType == null) {
                throw new BuilderException();
            }

            return new ColumnScheme(name, dataType);
        }
    }
}
