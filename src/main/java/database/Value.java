package database;

import java.util.Arrays;

import database.contract.ValueHolder;

public class Value implements ValueHolder {
    private DataType mDataType;
    private char[] mCharArrayValue;
    private int mIntegerValue;
    private int mHash;

    public Value(char[] charArrayValue, DataType dataType) {
        mCharArrayValue = charArrayValue;
        mDataType = dataType;
        mHash = Arrays.hashCode(charArrayValue);
    }

    public Value(int hash, char[] charArrayValue, DataType dataType) {
        mCharArrayValue = charArrayValue;
        mDataType = dataType;
        mHash = hash;
    }

    public Value(int integerValue, DataType dataType) {
        mDataType = dataType;
        mIntegerValue = integerValue;
        mHash = integerValue;
    }

    @Override
    public char[] getCharArrayValue() {
        return mCharArrayValue;
    }

    @Override
    public int getIntegerValue() {
        return mIntegerValue;
    }

    @Override
    public DataType getDataType() {
        return mDataType;
    }

    @Override
    public int hash() {
        return mHash;
    }
}
