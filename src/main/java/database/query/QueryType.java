package database.query;

public enum QueryType {
    ADD(1),
    PUT(2),
    SHOW(3),
    GET(4),
    DELETE(5);

    private final int mType;

    QueryType(int type) {
        mType = type;
    }

    public int getType() {
        return mType;
    }
}
