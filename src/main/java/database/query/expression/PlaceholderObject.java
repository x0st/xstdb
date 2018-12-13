package database.query.expression;

import java.util.Arrays;

import database.DataType;
import database.Value;
import database.contract.ValueHolder;

public class PlaceholderObject {
    private final ValueHolder[] mValues;
    private final String[] mPlaceholders;
    private final int mSize;
    private int mCounter;

    public PlaceholderObject(int size) {
        mValues = new Value[size];
        mPlaceholders = new String[size];
        mSize = size;
        mCounter = 0;
    }

    public int getSize() {
        return mSize;
    }

    public void set(String placeholderName, ValueHolder value) {
        mPlaceholders[mCounter] = placeholderName;
        mValues[mCounter] = value;

        mCounter++;
    }

    public String getPlaceholder(int i) {
        return mPlaceholders[i];
    }

    public ValueHolder getValue(int i) {
        return mValues[i];
    }
}
