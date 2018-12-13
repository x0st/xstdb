package database.query.expression.parser;

public class ASTNode {
    private ASTNode mLeft;
    private ASTNode mRight;
    private byte mToken;
    private char[] mCharArrayValue;
    private int mFastAccessMark;

    public ASTNode(byte token, char[] value) {
        mToken = token;
        mCharArrayValue = value;
        mFastAccessMark = -1;
    }

    public ASTNode getLeft() {
        return mLeft;
    }

    public ASTNode getRight() {
        return mRight;
    }

    public char[] getCharArrayValue() {
        return mCharArrayValue;
    }

    public byte getToken() {
        return mToken;
    }

    public ASTNode setLeft(ASTNode left) {
        mLeft = left;

        return this;
    }

    public ASTNode setRight(ASTNode right) {
        mRight = right;

        return this;
    }

    public void mark(int marker) {
        mFastAccessMark = marker;
    }

    public int getMarker() {
        return mFastAccessMark;
    }
}
