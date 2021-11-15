package database.query.expression.parser;

import java.util.Arrays;

import database.contract.Record;
import database.contract.ValueHolder;
import database.query.expression.PlaceholderObject;
import database.rawquery.parser.Token;
import database.scheme.ColumnScheme;

public class Evaluator {
    private ASTNode mASTNode;
    private PlaceholderObject mPlaceholderObject;
    private ColumnScheme[] mColumnSchemes;

    public Evaluator(PlaceholderObject placeholderObject, ColumnScheme[] columnSchemes, ASTNode astNode) {
        mPlaceholderObject = placeholderObject;
        mColumnSchemes = columnSchemes;
        mASTNode = astNode;

        cache(astNode);
    }

    private void cache(ASTNode astNode) {
        if (astNode.getToken() == Token.MATH_OPERATOR || astNode.getToken() == Token.LOGICAL_OPERATOR) {
            cache(astNode.getLeft());
            cache(astNode.getRight());
        } else {
            if (astNode.getToken() == Token.WORD) {
                for (int columnIndex = 0; columnIndex < mColumnSchemes.length; columnIndex++) {
                    if (Arrays.hashCode(astNode.getCharArrayValue()) == mColumnSchemes[columnIndex].getNameHash()) {
                        astNode.mark(columnIndex); break;
                    }
                }
            } else if (astNode.getToken() == Token.PLACEHOLDER) {
                for (int placeholderIndex = 0; placeholderIndex < mPlaceholderObject.getSize(); placeholderIndex++) {
                    if (Arrays.hashCode(astNode.getCharArrayValue()) == Arrays.hashCode(mPlaceholderObject.getPlaceholder(placeholderIndex).toCharArray())) {
                        astNode.mark(placeholderIndex); break;
                    }
                }
            }
        }
    }

    public boolean evaluate(Record record) {
        return evaluate0(mASTNode, record);
    }

    private boolean evaluate0(ASTNode astNode, Record record) {
        ValueHolder left;
        ValueHolder right;

        if (astNode.getToken() == Token.MATH_OPERATOR) {
            left = record.get(astNode.getLeft().getMarker());
            right = mPlaceholderObject.getValue(astNode.getRight().getMarker());

            switch (astNode.getCharArrayValue()[0]) {
                case '=':
                    // because of collision we must compare the whole string
                    return left.hash() == right.hash() && Arrays.equals(left.getCharArrayValue(), right.getCharArrayValue());
                case '>':
                    return left.hash() > right.hash();
                case '<':
                    return left.hash() < right.hash();
            }
        } else if (astNode.getToken() == Token.LOGICAL_OPERATOR) {
            if (astNode.getCharArrayValue()[0] == '&') {
                return evaluate0(astNode.getLeft(), record) & evaluate0(astNode.getRight(), record);
            } else if (astNode.getCharArrayValue()[0] == '|') {
                return evaluate0(astNode.getLeft(), record) | evaluate0(astNode.getRight(), record);
            }
        }

        return false;
    }
}
