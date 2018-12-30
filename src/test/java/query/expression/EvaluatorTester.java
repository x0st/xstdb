package query.expression;

import org.junit.jupiter.api.Test;

import database.DataType;
import database.Row;
import database.Value;
import database.query.expression.PlaceholderObject;

import database.query.expression.parser.ASTNode;
import database.query.expression.parser.Evaluator;
import database.scheme.ColumnScheme;

import static database.query.parser.Token.WORD;
import static database.query.parser.Token.PLACEHOLDER;
import static database.query.parser.Token.MATH_OPERATOR;
import static database.query.parser.Token.LOGICAL_OPERATOR;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EvaluatorTester {
    ASTNode astNode;
    Row record = new Row(20);

    private ASTNode ast(byte token, String string) {
        return new ASTNode(token, string.toCharArray());
    }

    private void ev(boolean expected, PlaceholderObject po, ColumnScheme[] cs) {
        Evaluator ev = new Evaluator(po, cs, astNode);
        assertEquals(expected, ev.evaluate(record));
    }

    @Test
    public void evaluatesCorrectly00() {
        PlaceholderObject placeholderMap = new PlaceholderObject(1);
        ColumnScheme[] columnSchemes = new ColumnScheme[1];

        record.set(0, new Value(5, DataType.INTEGER));
        placeholderMap.set(":n", new Value(5, DataType.INTEGER));

        astNode = ast(MATH_OPERATOR, "=")
                .setLeft(ast(WORD, "n"))
                .setRight(ast(PLACEHOLDER, ":n"));

        columnSchemes[0] = new ColumnScheme("n".toCharArray(), DataType.INTEGER);

        ev(true, placeholderMap, columnSchemes);
    }

    @Test
    public void evaluatesCorrectly01() {
        PlaceholderObject placeholderMap = new PlaceholderObject(1);
        ColumnScheme[] columnSchemes = new ColumnScheme[1];

        record.set(0, new Value(9, DataType.INTEGER));
        placeholderMap.set(":n", new Value(5, DataType.INTEGER));

        astNode = ast(MATH_OPERATOR, "=")
                .setLeft(ast(WORD, "n"))
                .setRight(ast(PLACEHOLDER, ":n"));

        columnSchemes[0] = new ColumnScheme("n".toCharArray(), DataType.INTEGER);

        ev(false, placeholderMap, columnSchemes);
    }

    @Test
    public void evaluatesCorrectly02() {
        PlaceholderObject placeholderMap = new PlaceholderObject(1);
        ColumnScheme[] columnSchemes = new ColumnScheme[1];

        record.set(0, new Value(9, DataType.INTEGER));
        placeholderMap.set(":n", new Value(5, DataType.INTEGER));

        astNode = ast(MATH_OPERATOR, ">")
                .setLeft(ast(WORD, "n"))
                .setRight(ast(PLACEHOLDER, ":n"));

        columnSchemes[0] = new ColumnScheme("n".toCharArray(), DataType.INTEGER);

        ev(true, placeholderMap, columnSchemes);
    }


    @Test
    public void evaluatesCorrectly03() {
        PlaceholderObject placeholderMap = new PlaceholderObject(1);
        ColumnScheme[] columnSchemes = new ColumnScheme[1];

        record.set(0, new Value(9, DataType.INTEGER));
        placeholderMap.set(":n", new Value(5, DataType.INTEGER));

        astNode = ast(MATH_OPERATOR, "<")
                .setLeft(ast(WORD, "n"))
                .setRight(ast(PLACEHOLDER, ":n"));

        columnSchemes[0] = new ColumnScheme("n".toCharArray(), DataType.INTEGER);

        ev(false, placeholderMap, columnSchemes);
    }

    @Test
    public void evaluatesCorrectly10() {
        PlaceholderObject placeholderMap = new PlaceholderObject(2);
        ColumnScheme[] columnSchemes = new ColumnScheme[2];

        record.set(0, new Value(9, DataType.INTEGER));
        record.set(1, new Value(123, DataType.INTEGER));

        placeholderMap.set(":n", new Value(5, DataType.INTEGER));
        placeholderMap.set(":id", new Value(123, DataType.INTEGER));

        astNode = ast(LOGICAL_OPERATOR, "&")
                .setLeft(
                        ast(MATH_OPERATOR, "<")
                        .setLeft(ast(WORD, "n"))
                        .setRight(ast(PLACEHOLDER, ":n"))
                )
                .setRight(
                        ast(MATH_OPERATOR, "=")
                        .setLeft(ast(WORD, "id"))
                        .setRight(ast(PLACEHOLDER, ":id"))
                );

        columnSchemes[0] = new ColumnScheme("n".toCharArray(), DataType.INTEGER);
        columnSchemes[1] = new ColumnScheme("id".toCharArray(), DataType.INTEGER);

        ev(false, placeholderMap, columnSchemes);
    }


    @Test
    public void evaluatesCorrectly11() {
        PlaceholderObject placeholderMap = new PlaceholderObject(2);
        ColumnScheme[] columnSchemes = new ColumnScheme[2];

        record.set(0, new Value(9, DataType.INTEGER));
        record.set(1, new Value(123, DataType.INTEGER));

        placeholderMap.set(":n", new Value(5, DataType.INTEGER));
        placeholderMap.set(":id", new Value(123, DataType.INTEGER));

        astNode = ast(LOGICAL_OPERATOR, "|")
                .setLeft(
                        ast(MATH_OPERATOR, "<")
                                .setLeft(ast(WORD, "n"))
                                .setRight(ast(PLACEHOLDER, ":n"))
                )
                .setRight(
                        ast(MATH_OPERATOR, "=")
                                .setLeft(ast(WORD, "id"))
                                .setRight(ast(PLACEHOLDER, ":id"))
                );

        columnSchemes[0] = new ColumnScheme("n".toCharArray(), DataType.INTEGER);
        columnSchemes[1] = new ColumnScheme("id".toCharArray(), DataType.INTEGER);

        ev(true, placeholderMap, columnSchemes);
    }

    @Test
    public void evaluatesCorrectly12() {
        PlaceholderObject placeholderMap = new PlaceholderObject(2);
        ColumnScheme[] columnSchemes = new ColumnScheme[2];

        record.set(0, new Value(5, DataType.INTEGER));
        record.set(1, new Value("pavel".toCharArray(), DataType.STRING));

        placeholderMap.set(":n", new Value(5, DataType.INTEGER));
        placeholderMap.set(":name", new Value("pavel".toCharArray(), DataType.STRING));

        astNode = ast(LOGICAL_OPERATOR, "|")
                .setLeft(
                        ast(MATH_OPERATOR, "=")
                        .setLeft(ast(WORD, "n"))
                        .setRight(ast(PLACEHOLDER, ":n"))
                )
                .setRight(
                        ast(MATH_OPERATOR, "=")
                        .setLeft(ast(WORD, "name"))
                        .setRight(ast(PLACEHOLDER, ":name"))
                );

        columnSchemes[0] = new ColumnScheme("n".toCharArray(), DataType.INTEGER);
        columnSchemes[1] = new ColumnScheme("name".toCharArray(), DataType.STRING);

        ev(true, placeholderMap, columnSchemes);
    }

    @Test
    public void evaluatesCorrectly13() {
        PlaceholderObject placeholderMap = new PlaceholderObject(2);
        ColumnScheme[] columnSchemes = new ColumnScheme[2];

        record.set(0, new Value(1, DataType.INTEGER));
        record.set(1, new Value("pavel".toCharArray(), DataType.STRING));

        placeholderMap.set(":n", new Value(5, DataType.INTEGER));
        placeholderMap.set(":name", new Value("pl".toCharArray(), DataType.STRING));

        astNode = ast(LOGICAL_OPERATOR, "&")
                .setLeft(
                        ast(MATH_OPERATOR, "<")
                        .setLeft(ast(WORD, "n"))
                        .setRight(ast(PLACEHOLDER, ":n"))
                )
                .setRight(
                        ast(MATH_OPERATOR, "=")
                        .setLeft(ast(WORD, "name"))
                        .setRight(ast(PLACEHOLDER, ":name"))
                );

        columnSchemes[0] = new ColumnScheme("n".toCharArray(), DataType.INTEGER);
        columnSchemes[1] = new ColumnScheme("name".toCharArray(), DataType.STRING);

        ev(false, placeholderMap, columnSchemes);
    }

    @Test
    public void evaluatesCorrectly20() {
        PlaceholderObject placeholderMap = new PlaceholderObject(3);
        ColumnScheme[] columnSchemes = new ColumnScheme[3];

        record.set(0, new Value(1, DataType.INTEGER));
        record.set(1, new Value("pavel".toCharArray(), DataType.STRING));
        record.set(2, new Value(19, DataType.INTEGER));

        placeholderMap.set(":n", new Value(5, DataType.INTEGER));
        placeholderMap.set(":name", new Value("pl".toCharArray(), DataType.STRING));
        placeholderMap.set(":age", new Value(20, DataType.INTEGER));

        astNode = ast(LOGICAL_OPERATOR, "&")
                    .setLeft(
                        ast(LOGICAL_OPERATOR, "|")
                        .setLeft(
                                ast(MATH_OPERATOR, "<")
                                .setLeft(ast(WORD, "n"))
                                .setRight(ast(PLACEHOLDER, ":n"))
                        )
                        .setRight(
                                ast(MATH_OPERATOR, "=")
                                .setLeft(ast(WORD, "name"))
                                .setRight(ast(PLACEHOLDER, ":name"))
                        )
                    )
                    .setRight(
                            ast(MATH_OPERATOR, ">")
                            .setLeft(ast(WORD, "age"))
                            .setRight(ast(PLACEHOLDER, ":age"))
                    );

        columnSchemes[0] = new ColumnScheme("n".toCharArray(), DataType.INTEGER);
        columnSchemes[1] = new ColumnScheme("name".toCharArray(), DataType.STRING);
        columnSchemes[2] = new ColumnScheme("age".toCharArray(), DataType.STRING);

        ev(false, placeholderMap, columnSchemes);
    }
}
