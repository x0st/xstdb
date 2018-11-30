package database.expression.parser;

import java.util.Arrays;

import database.contract.ColumnValueDataHolder;
import database.contract.PlaceholderMap;
import database.contract.PlaceholderValueDataHolder;
import database.contract.Record;
import database.expression.Token;

import static database.DataType.INTEGER;
import static database.DataType.STRING;
import static database.expression.Token.Type.*;

public class Evaluator {

    public boolean evaluateExpression(ASTNode astNode) {
        if (astNode.getToken() == LOGICAL_OPERATOR) {
            if (astNode.getCharArrayValue()[0] == '&') {
                return evaluateExpression(astNode.getLeft()) & evaluateExpression(astNode.getRight());
            } else if (astNode.getCharArrayValue()[0] == '|') {
                return evaluateExpression(astNode.getLeft()) | evaluateExpression(astNode.getRight());
            }
        }

        return astNode.getBooleanValue();
    }

    public ASTNode evaluateConditions(ASTNode astNode, Record record, PlaceholderMap placeholderMap) {
        ColumnValueDataHolder left;
        PlaceholderValueDataHolder right;

        if (astNode.getToken() == Token.Type.MATH_OPERATOR) {
            // column
            left = record.get(String.valueOf(astNode.getLeft().getCharArrayValue()));
            // placeholder
            right = placeholderMap.get(String.valueOf(astNode.getRight().getCharArrayValue()));

            switch (astNode.getCharArrayValue()[0]) {
                case '=':
                    if (left.getDataType() == INTEGER && right.getDataType() == INTEGER) {
                        astNode.setBooleanValue(
                                left.getIntegerValue() == right.getIntegerValue()
                        );
                    } else if (left.getDataType() == STRING && right.getDataType() == STRING) {
                        astNode.setBooleanValue(
                                Arrays.equals(left.getCharArrayValue(), right.getCharArrayValue())
                        );
                    }
                    break;
                case '>':

                    if (left.getDataType() == INTEGER && right.getDataType() == INTEGER) {
                        astNode.setBooleanValue(
                                left.getIntegerValue() > right.getIntegerValue()
                        );
                    } else if (left.getDataType() == STRING && right.getDataType() == STRING) {
                        astNode.setBooleanValue(
                                left.getCharArrayValue().length > right.getCharArrayValue().length
                        );
                    }

                    break;
                case '<':

                    if (left.getDataType() == INTEGER && right.getDataType() == INTEGER) {
                        astNode.setBooleanValue(
                                left.getIntegerValue() < right.getIntegerValue()
                        );
                    } else if (left.getDataType() == STRING && right.getDataType() == STRING) {
                        astNode.setBooleanValue(
                                left.getCharArrayValue().length < right.getCharArrayValue().length
                        );
                    }

                    break;
            }

            astNode.setCharArrayValue(null);
            astNode.setToken(BOOLEAN);

            astNode.setRight(null);
            astNode.setLeft(null);
        } else {
            evaluateConditions(astNode.getLeft(), record, placeholderMap);
            evaluateConditions(astNode.getRight(), record, placeholderMap);
        }

        return astNode;
    }
}
