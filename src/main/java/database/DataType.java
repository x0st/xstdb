package database;

public enum DataType {
    INTEGER (1, 4),
    STRING  (2, 50);

    private final int mType;
    private final int mSize;

    DataType(int type, int size) {
        mType = type;
        mSize = size;
    }

    public int getType() {
        return mType;
    }

    public int getSize() {
        return mSize;
    }

    public static DataType valueOf(byte type) {
        for (DataType dataType : DataType.values()) {
            if (dataType.getType() == type) {
                return dataType;
            }
        }

        return null;
    }
}
