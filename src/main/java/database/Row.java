package database;

import database.contract.ValueHolder;

public class Row {
    private ValueHolder[] mValueHolders;

    public Row(int capacity) {
        mValueHolders = new ValueHolder[capacity];
    }

    public void set(int columnIndex, ValueHolder holder) {
        mValueHolders[columnIndex] = holder;
    }

    public ValueHolder get(int columnIndex) {
        return mValueHolders[columnIndex];
    }
}
