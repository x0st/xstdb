package database.query.expression.parser;

import database.exception.TokenizationException;

import static database.query.expression.parser.Token.Type.*;

public class Lexer extends database.Lexer {

    private static final short BUFFER_SIZE = 21;

    public Lexer(char[] is) {
        input = is;
        buffer = new char[bufferCapacity()];
    }

    public Lexer() {
        this(new char[0]);
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

    @Override
    protected int bufferCapacity() {
        return BUFFER_SIZE;
    }
}
