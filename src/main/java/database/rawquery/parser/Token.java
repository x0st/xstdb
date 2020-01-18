package database.rawquery.parser;

abstract public class Token {
    public static final byte WORD = 0;
    public static final byte NUMBER = 1;
    public static final byte LEFT_BRACKET = 2;
    public static final byte QUOTED_STRING = 3;
    public static final byte RIGHT_BRACKET = 4;
    public static final byte MATH_OPERATOR = 5;
    public static final byte LOGICAL_OPERATOR = 6;
    public static final byte PLACEHOLDER = 7;
}
