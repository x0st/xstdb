package database;

import database.contract.Record;
import database.contract.ValueHolder;

public class Row implements Record {
    private ValueHolder[] mValueHolders;

    public Row(int capacity) {
        mValueHolders = new ValueHolder[capacity];
    }

    @Override
    public void set(int columnIndex, ValueHolder holder) {
        mValueHolders[columnIndex] = holder;
    }

    @Override
    public ValueHolder get(int columnIndex) {
        return mValueHolders[columnIndex];
    }

    @Override
    public int size() {
        return mValueHolders.length;
    }
}
