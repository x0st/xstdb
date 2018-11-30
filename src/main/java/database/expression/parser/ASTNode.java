package database.expression.parser;

public class ASTNode {
    private ASTNode mLeft;
    private ASTNode mRight;
    private byte mToken;
    private char[] mCharArrayValue;
    private boolean mBooleanValue;

    public ASTNode(byte token, char[] value) {
        mToken = token;
        mCharArrayValue = value;
    }

    public ASTNode(byte token, boolean value) {
        mToken = token;
        mBooleanValue = value;
    }

    public ASTNode getLeft() {
        return mLeft;
    }

    public ASTNode getRight() {
        return mRight;
    }

    public boolean getBooleanValue() {
        return mBooleanValue;
    }

    public char[] getCharArrayValue() {
        return mCharArrayValue;
    }

    public byte getToken() {
        return mToken;
    }

    public void setToken(byte token) {
        mToken = token;
    }

    public void setBooleanValue(boolean value) {
        mBooleanValue = value;
    }

    public void setCharArrayValue(char[] value) {
        mCharArrayValue = value;
    }

    public ASTNode setLeft(ASTNode left) {
        mLeft = left;

        return this;
    }

    public ASTNode setRight(ASTNode right) {
        mRight = right;

        return this;
    }
}
