package database;

public class ExternalRow {
    private final Value[] mValues;
    private int mCounter;

    public ExternalRow(int capacity) {
        mValues = new Value[capacity];
        mCounter = 0;
    }

    public void add(Value value) {
        mValues[mCounter++] = value;
    }

    public Value get(int i) {
        return mValues[i];
    }
}
