package database.expression.parser;

import database.exception.TokenizationException;

import static database.expression.Token.Type.*;

public class Lexer {
    private char[] input;

    private static final short BUFFER_SIZE = 21;

    private char[] buffer = new char[BUFFER_SIZE];

    private short bufferSize = 0;

    private byte token = -1;

    private short inputPointer = 0;

    private int nextChar = -1;
    private int prevChar = -1;
    private int currChar = -1;

    public Lexer(char[] is) {
        input = is;
    }

    public Lexer() {
        input = new char[0];
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

    private void scan() {
        prevChar = currChar;
        currChar = readInput();
        nextChar = lookAhead();
    }

    private int readInput() {
        if (input.length > inputPointer) {
            return input[inputPointer++];
        } else {
            return -1;
        }
    }

    private int lookAhead() {
        if (input.length > inputPointer) {
            return input[inputPointer];
        } else {
            return -1;
        }
    }

    private boolean isLogicalOperator(int chr) {
        return chr == '|' || chr == '&';
    }

    private boolean isMathOperator(int chr) {
        return chr == '=' || chr == '>' || chr == '<';
    }

    private boolean isColon(int chr) {
        return chr == ':';
    }

    private boolean isLetter(int chr) {
        return chr >= 'a' && chr <= 'z';
    }

    private boolean isSpace(int chr) {
        return chr == ' ';
    }

    public char[] lexeme() {
        char[] _buff = new char[bufferSize];

        System.arraycopy(buffer, 0, _buff, 0, bufferSize);

        return _buff;
    }

    public byte token() {
        return token;
    }

    public boolean next() throws TokenizationException {
        bufferSize = 0;

        do {
            scan();

            if (isSpace(prevChar) && isLogicalOperator(currChar) && isSpace(nextChar)) {
                buffer[bufferSize++] = (char)currChar;
                token = LOGICAL_OPERATOR;

                return true;
            }

            if (isSpace(prevChar) && isMathOperator(currChar) && isSpace(nextChar)) {
                buffer[bufferSize++] = (char)currChar;
                token = MATH_OPERATOR;

                return true;
            }

            if ( (isLetter(prevChar) && isSpace(currChar) && !isColon(buffer[0])) ||
                    (isLetter(prevChar) && currChar == -1 && !isColon(buffer[0]))) {
                token = COLUMN;

                return true;
            }

            if (isLetter(prevChar) && isSpace(currChar) && isColon(buffer[0]) ||
                    (isLetter(prevChar) && currChar == -1 && isColon(buffer[0]))) {
                token = PLACEHOLDER;

                return true;
            }

            if ((prevChar == -1 && isLetter(currChar) && isLetter(nextChar)) ||
                    (isLetter(prevChar) && isLetter(currChar) && isLetter(nextChar)) ||
                    (isLetter(currChar) && isSpace(nextChar)) ||
                    (isLetter(currChar) && nextChar == -1) ||
                    (isSpace(prevChar) && isLetter(currChar)) ||
                    (isSpace(prevChar) && isColon(currChar) && isLetter(nextChar)) ||
                    (isColon(prevChar) && isLetter(currChar))) {
                buffer[bufferSize++] = (char) currChar;
            } else if (!isSpace(currChar) && currChar != -1) {
                throw new TokenizationException(
                        String.format("Cannot tokenize the database expression: %s", String.valueOf(input))
                );
            }

        } while (currChar != -1);

        token = -1;
        bufferSize = 0;

        return false;
    }
}
