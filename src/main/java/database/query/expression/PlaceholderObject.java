package database.query.expression;

import java.util.ArrayList;
import java.util.List;

import database.Value;
import database.contract.ValueHolder;
import database.exception.BuilderException;

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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<String> placeholders = new ArrayList<>(10);
        private List<ValueHolder> values = new ArrayList<>(10);

        public Builder placeholder(String value) {
            placeholders.add(value);
            return this;
        }

        public Builder value(ValueHolder value) {
            values.add(value);
            return this;
        }

        public PlaceholderObject build() {
            if (placeholders.size() == 0 || placeholders.size() != values.size()) {
                throw new RuntimeException();
            }

            PlaceholderObject po = new PlaceholderObject(placeholders.size());

            for (int i = 0; i < placeholders.size(); i++) {
                po.set(placeholders.get(i), values.get(i));
            }

            return po;
        }
    }
}
