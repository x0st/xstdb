package database;

import database.exception.TokenizationException;

abstract public class Lexer {
    protected char[] input;

    protected char[] buffer;

    protected short bufferSize = 0;

    protected byte token = -1;

    protected short inputPointer = 0;

    protected int nextChar = -1;
    protected int prevChar = -1;
    protected int currChar = -1;

    public Lexer(char[] is) {
        input = is;
        buffer = new char[bufferCapacity()];
    }

    public Lexer() {
        this(new char[0]);
    }

    public char[] getInput() {
        return input;
    }

    public void setInput(char[] val) {
        input = val;

        nextChar = -1;
        prevChar = -1;
        currChar = -1;

        inputPointer = 0;

        bufferSize = 0;
    }

    protected void scan() {
        prevChar = currChar;
        currChar = readInput();
        nextChar = lookAhead();
    }

    protected int readInput() {
        if (input.length > inputPointer) {
            return input[inputPointer++];
        } else {
            return -1;
        }
    }

    protected int lookAhead() {
        if (input.length > inputPointer) {
            return input[inputPointer];
        } else {
            return -1;
        }
    }

    public char[] lexeme() {
        char[] _buff = new char[bufferSize];

        System.arraycopy(buffer, 0, _buff, 0, bufferSize);

        return _buff;
    }

    protected void throwException() throws TokenizationException {
        throw new TokenizationException(
                String.format("Cannot tokenize the database expression: %s", String.valueOf(input))
        );
    }

    protected boolean isBackslash(int chr) {
        return chr == 92;
    }

    protected boolean isQuote(int chr) {
        return chr == 34;
    }

    protected boolean isMinus(int chr) {
        return chr == 45;
    }

    protected boolean isNumber(int chr) {
        return chr >= 48 && chr <= 57;
    }

    protected boolean isLeftBracket(int chr) {
        return chr == 91;
    }

    protected boolean isRightBracket(int chr) {
        return chr == 93;
    }

    protected boolean isLogicalOperator(int chr) {
        return chr == 124 || chr == 38;
    }

    protected boolean isMathOperator(int chr) {
        return chr == 61 || chr == 62 || chr == 60;
    }

    protected boolean isColon(int chr) {
        return chr == 58;
    }

    protected boolean isUnderscore(int chr) {
        return chr == 95;
    }

    protected boolean isLetter(int chr) {
        return chr >= 97 && chr <= 122;
    }

    protected boolean isSpace(int chr) {
        return chr == ' ';
    }

    public byte token() {
        return token;
    }

    abstract public boolean next() throws TokenizationException;

    abstract protected int bufferCapacity();
}
