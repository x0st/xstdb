package database.expression;

abstract public class Token {
    abstract public class Type {
        public static final byte BOOLEAN = 4;
        public static final byte LOGICAL_OPERATOR = 3;

        public static final byte MATH_OPERATOR = 1;
        public static final byte PLACEHOLDER = 2;
        public static final byte COLUMN = 0;
    }
}
