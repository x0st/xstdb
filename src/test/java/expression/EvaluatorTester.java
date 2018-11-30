package expression;

import org.junit.jupiter.api.Test;

import database.ColumnValuePair;
import database.DataType;
import database.Row;
import database.contract.Record;
import database.contract.PlaceholderMap;
import database.expression.PlaceholderObject;
import database.expression.PlaceholderValuePair;

import database.expression.parser.ASTNode;
import database.expression.parser.Evaluator;

import static database.expression.Token.Type.COLUMN;
import static database.expression.Token.Type.PLACEHOLDER;
import static database.expression.Token.Type.MATH_OPERATOR;
import static database.expression.Token.Type.LOGICAL_OPERATOR;
import static database.expression.Token.Type.BOOLEAN;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EvaluatorTester {
    Evaluator ev = new Evaluator();
    ASTNode astNode;

    Record record = new Row();
    PlaceholderMap placeholderMap = new PlaceholderObject();

    private ASTNode ast(byte token, String string) {
        return new ASTNode(token, string.toCharArray());
    }

    private ASTNode ast(byte token, boolean v) {
        return new ASTNode(token, v);
    }

    @Test
    public void evaluatesConditionsCorrectly00() {
        record.set("n", new ColumnValuePair("n", 5, DataType.INTEGER));
        placeholderMap.set(":n", new PlaceholderValuePair("n", 5, DataType.INTEGER));

        astNode = ast(MATH_OPERATOR, "=")
                .setLeft(ast(COLUMN, "n"))
                .setRight(ast(PLACEHOLDER, ":n"));

        astNode = ev.evaluateConditions(astNode, record, placeholderMap);

        assertEquals(BOOLEAN, astNode.getToken());
        assertEquals(true, astNode.getBooleanValue());
    }

    @Test
    public void evaluatesConditionsCorrectly01() {
        record.set("n", new ColumnValuePair("n", 9, DataType.INTEGER));
        placeholderMap.set(":n", new PlaceholderValuePair("n", 5, DataType.INTEGER));

        astNode = ast(MATH_OPERATOR, "=")
                .setLeft(ast(COLUMN, "n"))
                .setRight(ast(PLACEHOLDER, ":n"));

        astNode = ev.evaluateConditions(astNode, record, placeholderMap);

        assertEquals(BOOLEAN, astNode.getToken());
        assertEquals(false, astNode.getBooleanValue());
    }

    @Test
    public void evaluatesConditionsCorrectly02() {
        record.set("n", new ColumnValuePair("n", 9, DataType.INTEGER));
        placeholderMap.set(":n", new PlaceholderValuePair("n", 5, DataType.INTEGER));

        astNode = ast(MATH_OPERATOR, ">")
                .setLeft(ast(COLUMN, "n"))
                .setRight(ast(PLACEHOLDER, ":n"));

        astNode = ev.evaluateConditions(astNode, record, placeholderMap);

        assertEquals(BOOLEAN, astNode.getToken());
        assertEquals(true, astNode.getBooleanValue());
    }

    @Test
    public void evaluatesConditionsCorrectly03() {
        record.set("n", new ColumnValuePair("n", 9, DataType.INTEGER));
        placeholderMap.set(":n", new PlaceholderValuePair("n", 5, DataType.INTEGER));

        astNode = ast(MATH_OPERATOR, "<")
                .setLeft(ast(COLUMN, "n"))
                .setRight(ast(PLACEHOLDER, ":n"));

        astNode = ev.evaluateConditions(astNode, record, placeholderMap);

        assertEquals(BOOLEAN, astNode.getToken());
        assertEquals(false, astNode.getBooleanValue());
    }

    @Test
    public void evaluatesConditionsCorrectly10() {
        record.set("n", new ColumnValuePair("n", 9, DataType.INTEGER));
        record.set("id", new ColumnValuePair("id", 123, DataType.INTEGER));

        placeholderMap.set(":n", new PlaceholderValuePair("n", 5, DataType.INTEGER));
        placeholderMap.set(":id", new PlaceholderValuePair("id", 123, DataType.INTEGER));

        astNode = ast(LOGICAL_OPERATOR, "&")
                .setLeft(
                        ast(MATH_OPERATOR, "<")
                        .setLeft(ast(COLUMN, "n"))
                        .setRight(ast(PLACEHOLDER, ":n"))
                )
                .setRight(
                        ast(MATH_OPERATOR, "=")
                        .setLeft(ast(COLUMN, "id"))
                        .setRight(ast(PLACEHOLDER, ":id"))
                );

        astNode = ev.evaluateConditions(astNode, record, placeholderMap);

        assertEquals(LOGICAL_OPERATOR, astNode.getToken());

        assertEquals(BOOLEAN, astNode.getLeft().getToken());
        assertEquals(BOOLEAN, astNode.getRight().getToken());

        assertEquals(false, astNode.getLeft().getBooleanValue());
        assertEquals(true, astNode.getRight().getBooleanValue());
    }

    @Test
    public void evaluatesConditionsCorrectly11() {
        record.set("n", new ColumnValuePair("n", 9, DataType.INTEGER));
        record.set("id", new ColumnValuePair("id", 123, DataType.INTEGER));

        placeholderMap.set(":n", new PlaceholderValuePair("n", 5, DataType.INTEGER));
        placeholderMap.set(":id", new PlaceholderValuePair("id", 123, DataType.INTEGER));

        astNode = ast(LOGICAL_OPERATOR, "|")
                .setLeft(
                        ast(MATH_OPERATOR, "<")
                        .setLeft(ast(COLUMN, "n"))
                        .setRight(ast(PLACEHOLDER, ":n"))
                )
                .setRight(
                        ast(MATH_OPERATOR, "=")
                        .setLeft(ast(COLUMN, "id"))
                        .setRight(ast(PLACEHOLDER, ":id"))
                );

        astNode = ev.evaluateConditions(astNode, record, placeholderMap);

        assertEquals(LOGICAL_OPERATOR, astNode.getToken());

        assertEquals(BOOLEAN, astNode.getLeft().getToken());
        assertEquals(BOOLEAN, astNode.getRight().getToken());

        assertEquals(false, astNode.getLeft().getBooleanValue());
        assertEquals(true, astNode.getRight().getBooleanValue());
    }

    @Test
    public void evaluatesConditionsCorrectly12() {
        record.set("n", new ColumnValuePair("n", 5, DataType.INTEGER));
        record.set("name", new ColumnValuePair("name", "pavel".toCharArray(), DataType.STRING));

        placeholderMap.set(":n", new PlaceholderValuePair("n", 5, DataType.INTEGER));
        placeholderMap.set(":name", new PlaceholderValuePair("name", "pavel".toCharArray(), DataType.STRING));

        astNode = ast(LOGICAL_OPERATOR, "|")
                .setLeft(
                        ast(MATH_OPERATOR, "=")
                        .setLeft(ast(COLUMN, "n"))
                        .setRight(ast(PLACEHOLDER, ":n"))
                )
                .setRight(
                        ast(MATH_OPERATOR, "=")
                        .setLeft(ast(COLUMN, "name"))
                        .setRight(ast(PLACEHOLDER, ":name"))
                );

        astNode = ev.evaluateConditions(astNode, record, placeholderMap);

        assertEquals(LOGICAL_OPERATOR, astNode.getToken());

        assertEquals(BOOLEAN, astNode.getLeft().getToken());
        assertEquals(BOOLEAN, astNode.getRight().getToken());

        assertEquals(true, astNode.getLeft().getBooleanValue());
        assertEquals(true, astNode.getRight().getBooleanValue());
    }

    @Test
    public void evaluatesConditionsCorrectly13() {
        record.set("n", new ColumnValuePair("n", 1, DataType.INTEGER));
        record.set("name", new ColumnValuePair("name", "pavel".toCharArray(), DataType.STRING));

        placeholderMap.set(":n", new PlaceholderValuePair("n", 5, DataType.INTEGER));
        placeholderMap.set(":name", new PlaceholderValuePair("name", "pl".toCharArray(), DataType.STRING));

        astNode = ast(LOGICAL_OPERATOR, "|")
                .setLeft(
                        ast(MATH_OPERATOR, "<")
                        .setLeft(ast(COLUMN, "n"))
                        .setRight(ast(PLACEHOLDER, ":n"))
                )
                .setRight(
                        ast(MATH_OPERATOR, "=")
                        .setLeft(ast(COLUMN, "name"))
                        .setRight(ast(PLACEHOLDER, ":name"))
                );

        astNode = ev.evaluateConditions(astNode, record, placeholderMap);

        assertEquals(LOGICAL_OPERATOR, astNode.getToken());

        assertEquals(BOOLEAN, astNode.getLeft().getToken());
        assertEquals(BOOLEAN, astNode.getRight().getToken());

        assertEquals(true, astNode.getLeft().getBooleanValue());
        assertEquals(false, astNode.getRight().getBooleanValue());
    }

    @Test
    public void evaluatesConditionsCorrectly20() {
        record.set("n", new ColumnValuePair("n", 1, DataType.INTEGER));
        record.set("name", new ColumnValuePair("name", "pavel".toCharArray(), DataType.STRING));
        record.set("age", new ColumnValuePair("age", 19, DataType.INTEGER));

        placeholderMap.set(":n", new PlaceholderValuePair("n", 5, DataType.INTEGER));
        placeholderMap.set(":name", new PlaceholderValuePair("name", "pl".toCharArray(), DataType.STRING));
        placeholderMap.set(":age", new PlaceholderValuePair("age", 20, DataType.INTEGER));

        astNode = ast(LOGICAL_OPERATOR, "&")
                    .setLeft(
                        ast(LOGICAL_OPERATOR, "|")
                        .setLeft(
                                ast(MATH_OPERATOR, "<")
                                .setLeft(ast(COLUMN, "n"))
                                .setRight(ast(PLACEHOLDER, ":n"))
                        )
                        .setRight(
                                ast(MATH_OPERATOR, "=")
                                .setLeft(ast(COLUMN, "name"))
                                .setRight(ast(PLACEHOLDER, ":name"))
                        )
                    )
                    .setRight(
                            ast(MATH_OPERATOR, ">")
                            .setLeft(ast(COLUMN, "age"))
                            .setRight(ast(PLACEHOLDER, ":age"))
                    );

        astNode = ev.evaluateConditions(astNode, record, placeholderMap);

        assertEquals(LOGICAL_OPERATOR, astNode.getToken());

        assertEquals(BOOLEAN, astNode.getLeft().getLeft().getToken());
        assertEquals(BOOLEAN, astNode.getLeft().getRight().getToken());
        assertEquals(BOOLEAN, astNode.getRight().getToken());

        assertEquals(true, astNode.getLeft().getLeft().getBooleanValue());
        assertEquals(false, astNode.getLeft().getRight().getBooleanValue());
        assertEquals(false, astNode.getRight().getBooleanValue());
    }

    @Test
    public void evaluatesExpressionsCorrectly00() {
        astNode = ast(BOOLEAN, true);

        assertEquals(true, ev.evaluateExpression(astNode));
    }

    @Test
    public void evaluatesExpressionsCorrectly01() {
        astNode = ast(BOOLEAN, false);

        assertEquals(false, ev.evaluateExpression(astNode));
    }

    @Test
    public void evaluatesExpressionsCorrectly10() {
        astNode = ast(LOGICAL_OPERATOR, "&")
                .setRight(ast(BOOLEAN, true))
                .setLeft(ast(BOOLEAN, false));

        assertEquals(false, ev.evaluateExpression(astNode));
    }

    @Test
    public void evaluatesExpressionsCorrectly11() {
        astNode = ast(LOGICAL_OPERATOR, "&")
                .setRight(ast(BOOLEAN, true))
                .setLeft(ast(BOOLEAN, true));

        assertEquals(true, ev.evaluateExpression(astNode));
    }

    @Test
    public void evaluatesExpressionsCorrectly12() {
        astNode = ast(LOGICAL_OPERATOR, "|")
                .setRight(ast(BOOLEAN, true))
                .setLeft(ast(BOOLEAN, false));

        assertEquals(true, ev.evaluateExpression(astNode));
    }

    @Test
    public void evaluatesExpressionsCorrectly13() {
        astNode = ast(LOGICAL_OPERATOR, "|")
                .setRight(ast(BOOLEAN, false))
                .setLeft(ast(BOOLEAN, true));

        assertEquals(true, ev.evaluateExpression(astNode));
    }

    @Test
    public void evaluatesExpressionsCorrectly20() {
        astNode = ast(LOGICAL_OPERATOR, "&")
                .setLeft(
                        ast(LOGICAL_OPERATOR, "|")
                        .setRight(ast(BOOLEAN, false))
                        .setLeft(ast(BOOLEAN, true))
                )
                .setRight(
                        ast(BOOLEAN, true)
                );

        assertEquals(true, ev.evaluateExpression(astNode));
    }

    @Test
    public void evaluatesExpressionsCorrectly21() {
        astNode = ast(LOGICAL_OPERATOR, "&")
                .setLeft(
                        ast(LOGICAL_OPERATOR, "|")
                        .setRight(ast(BOOLEAN, false))
                        .setLeft(ast(BOOLEAN, false))
                )
                .setRight(
                        ast(BOOLEAN, true)
                );

        assertEquals(false, ev.evaluateExpression(astNode));
    }

    @Test
    public void evaluatesExpressionsCorrectly22() {
        astNode = ast(LOGICAL_OPERATOR, "|")
                .setLeft(
                        ast(LOGICAL_OPERATOR, "|")
                        .setRight(ast(BOOLEAN, false))
                        .setLeft(ast(BOOLEAN, true))
                )
                .setRight(
                        ast(BOOLEAN, false)
                );

        assertEquals(true, ev.evaluateExpression(astNode));
    }
}
