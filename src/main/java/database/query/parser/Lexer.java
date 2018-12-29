package database.query.parser;

import database.contract.LexerInterface;
import database.exception.TokenizationException;

public class Lexer implements LexerInterface {

    /**
     * Indicates whether the pointer in the input should be moved forward
     * on the next iteration in the next() method.
     */
    private boolean doScan = true;

    /**
     * ID of the token that is being parsed at the moment.
     * {@link Token}
     */
    private byte lookingFor = -1;

    /**
     * The input data.
     */
    private char[] input;

    /**
     * Just a place where the lexeme being processed at the moment is stored.
     */
    private char[] buffer;

    /**
     * The length of the lexeme being processed at the moment.
     */
    private short bufferSize = 0;

    /**
     * The found token.
     */
    private byte token = -1;

    /**
     * Points to a character in the input at the moment.
     */
    private short inputPointer = 0;

    /**
     * ASCII of the character followed by the current character.
     * In the following example 'b' is nextChar in case 'a' is the current character.
     * ['t', 'a', 'b']
     */
    private int nextChar = -1;

    /**
     * ASCII code of the character being processed at the moment.
     * In the following example 'a' is currChar whereas 't' is prevChar and 'b' is nextChar.
     * [..., 't', 'a', 'b', ....]
     */
    private int currChar = -1;

    /**
     * ASCII code of the previous character.
     *
     * In the following example 't' is the value of prevChar in case 'a' is the current character.
     * ['t', 'a', 'b']
     */
    private int prevChar = -1;

    /**
     * Indicates whether the number currently being processed is negative.
     */
    private boolean negativeIntLexeme = false;

    /**
     * The property contains an integer in case the token currently being processed is NUMBER.
     * For example, if the input is 'test hello 123 bla bla' and the lexeme currently being processed is '123'
     * then the set of values the property will contain is: 1, 12, 123
     */
    private int lexemeAsInt = 0;

    /**
     * Constructor.
     *
     * @param is input the lexer will analyze lexically.
     */
    public Lexer(char[] is) {
        input = is;
        buffer = new char[1024];
    }

    /**
     * Constructor.
     */
    public Lexer() {
        this(new char[0]);
    }

    /**
     * Set the given input as new and reset all the values of properties to default values.
     *
     * @param val input the lexer will analyze lexically.
     */
    public void setInput(char[] val) {
        input = val;

        nextChar = -1;
        prevChar = -1;
        currChar = -1;

        inputPointer = 0;

        bufferSize = 0;
    }

    /**
     * Update the values of the properties that contain the previous/current/next chars.
     */
    private void scan() {
        prevChar = currChar;
        currChar = readInput();
        nextChar = lookAhead();
    }

    /**
     * Move forward the pointer in the input by 1 position.
     *
     * @return new value for the currChar property
     */
    private int readInput() {
        if (input.length > inputPointer) {
            return input[inputPointer++];
        } else {
            return -1;
        }
    }

    /**
     * Take the character preceded by the current character.
     *
     * @return new value for the nextChar property
     */
    private int lookAhead() {
        if (input.length > inputPointer) {
            return input[inputPointer];
        } else {
            return -1;
        }
    }

    public int lexemeAsInt() {
        if (negativeIntLexeme) {
            return -lexemeAsInt;
        }

        return lexemeAsInt;
    }

    public char[] lexeme() {
        char[] _buff = new char[bufferSize];

        System.arraycopy(buffer, 0, _buff, 0, bufferSize);

        return _buff;
    }

    private void throwException() throws TokenizationException {
        throw new TokenizationException(
                String.format("Cannot tokenize the database expression: %s", String.valueOf(input))
        );
    }

    private boolean isBackslash(int chr) {
        return chr == 92;
    }

    private boolean isQuotationMark(int chr) {
        return chr == 34;
    }

    private boolean isMinus(int chr) {
        return chr == 45;
    }

    private boolean isNumber(int chr) {
        return chr >= 48 && chr <= 57;
    }

    private boolean isLeftBracket(int chr) {
        return chr == 91;
    }

    private boolean isRightBracket(int chr) {
        return chr == 93;
    }

    private boolean isLogicalOperator(int chr) {
        return chr == 124 || chr == 38;
    }

    private boolean isMathOperator(int chr) {
        return chr == 61 || chr == 62 || chr == 60;
    }

    private boolean isColon(int chr) {
        return chr == 58;
    }

    private boolean isUnderscore(int chr) {
        return chr == 95;
    }

    private boolean isLetter(int chr) {
        return (chr >= 97 && chr <= 122) || (chr >= 65 && chr <= 90);
    }

    private boolean isSpace(int chr) {
        return chr == 32;
    }

    private int asciiToInt(int value) {
        if (value >= 48 && value <= 57) {
            return value - 48;
        }

        throw new RuntimeException();
    }

    public byte token() {
        return token;
    }

    public boolean next() throws TokenizationException {
        bufferSize = 0;

        do {
            if (doScan) scan();
            doScan = true;

            if (lookingFor == -1) {
                lexemeAsInt = 0;
                bufferSize = 0;
                buffer[bufferSize++] = (char) currChar;

                if (isLetter(currChar)) {
                    lookingFor = Token.WORD;
                } else if (isLeftBracket(currChar)) {
                    token = Token.LEFT_BRACKET;
                    return true;
                } else if (isRightBracket(currChar)) {
                    token = Token.RIGHT_BRACKET;
                    return true;
                } else if (isNumber(currChar)) {
                    lexemeAsInt = lexemeAsInt * 10 + asciiToInt(currChar);
                    lookingFor = Token.NUMBER;
                } else if (isMinus(currChar) && isNumber(nextChar)) {
                    negativeIntLexeme = true;
                    lookingFor = Token.NUMBER;
                } else if (isQuotationMark(currChar)) {
                    lookingFor = Token.QUOTED_STRING;
                    bufferSize = 0;
                } else if (isLogicalOperator(currChar)) {
                    token = Token.LOGICAL_OPERATOR;
                    return true;
                } else if (isMathOperator(currChar)) {
                    lookingFor = Token.MATH_OPERATOR;
                } else if (isColon(currChar)) {
                    lookingFor = Token.PLACEHOLDER;
                } else {
                    bufferSize = 0;

                    if (currChar != -1 && !isSpace(currChar)) {
                        throwException();
                    }
                }
            } else {
                if (lookingFor == Token.PLACEHOLDER) {
                    if (isLetter(currChar) || isUnderscore(currChar)) {
                        buffer[bufferSize++] = (char) currChar;
                    } else if (isLeftBracket(currChar) || isRightBracket(currChar)) {
                        doScan = false;
                        token = Token.PLACEHOLDER;
                        lookingFor = -1;
                        return true;
                    } else if (isSpace(currChar)) {
                        token = Token.PLACEHOLDER;
                        lookingFor = -1;
                        return true;
                    }
                } else if (lookingFor == Token.WORD) {
                    if (isLetter(currChar) || isUnderscore(currChar)) {
                        buffer[bufferSize++] = (char) currChar;
                    } else if (isRightBracket(currChar)) {
                        doScan = false;
                        token = Token.WORD;
                        lookingFor = -1;
                        return true;
                    } else if (isSpace(currChar)) {
                        token = Token.WORD;
                        lookingFor = -1;
                        return true;
                    } else {
                        throwException();
                    }
                } else if (lookingFor == Token.MATH_OPERATOR) {
                    if (isMathOperator(currChar)) {
                        buffer[bufferSize++] = (char) currChar;
                    } else if (isSpace(currChar)) {
                        token = Token.MATH_OPERATOR;
                        lookingFor = -1;
                        return true;
                    } else {
                        throwException();
                    }
                } else if (lookingFor == Token.NUMBER) {
                    if (isNumber(currChar)) {
                        lexemeAsInt = lexemeAsInt * 10 + asciiToInt(currChar);
                        buffer[bufferSize++] = (char) currChar;
                    } else if (isSpace(currChar)) {
                        negativeIntLexeme = false;
                        token = Token.NUMBER;
                        lookingFor = -1;
                        return true;
                    } else if (isRightBracket(currChar) || isLeftBracket(currChar)) {
                        negativeIntLexeme = false;
                        doScan = false;
                        token = Token.NUMBER;
                        lookingFor = -1;
                        return true;
                    } else {
                        throwException();
                    }
                } else if (lookingFor == Token.QUOTED_STRING) {
                    if (isQuotationMark(currChar) && !isBackslash(prevChar)) {
                        token = Token.QUOTED_STRING;
                        lookingFor = -1;
                        return true;
                    } else {
                        buffer[bufferSize++] = (char) currChar;
                    }
                }
            }

        } while (currChar != -1);

        token = -1;
        bufferSize = 0;

        return false;
    }
}
