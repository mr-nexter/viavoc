package com.viavoc.lexpars.parser.visitors;

import com.viavoc.lexpars.lib.NumberValue;
import com.viavoc.lexpars.lib.Type;
import com.viavoc.lexpars.lib.Types;
import com.viavoc.lexpars.lib.Value;
import com.viavoc.lexpars.parser.ast.BinaryExpression;
import com.viavoc.lexpars.parser.ast.ConditionalExpression;
import com.viavoc.lexpars.parser.ast.IncludeStatement;
import com.viavoc.lexpars.parser.ast.Node;
import com.viavoc.lexpars.parser.ast.Statement;
import com.viavoc.lexpars.parser.ast.UnaryExpression;
import com.viavoc.lexpars.parser.ast.ValueExpression;
import com.viavoc.lexpars.parser.ast.VariableExpression;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public final class VisitorUtils {

    private VisitorUtils() { }

    public static boolean isValue(Node node) {
        return (node instanceof ValueExpression);
    }

    public static boolean isVariable(Node node) {
        return (node instanceof VariableExpression);
    }

    public static Statement includeProgram(IncludeStatement s) {
        if (!isValue(s)) return null;
        try {
            return s.loadProgram(s.expression.eval().asString());
        } catch (IOException ex) {
            return null;
        }
    }

    public static boolean isIntegerValue(Node node, int valueToCheck) {
        if (!isValue(node)) return false;

        final Value value = ((ValueExpression) node).value;
        if (value.type() != Type.NUMBER) return false;

        final Number number = ((NumberValue) value).raw();
        if ( (number instanceof Integer) || (number instanceof Short) || (number instanceof Byte)) {
            return number.intValue() == valueToCheck;
        }
        return false;
    }

    public static boolean isValueAsInt(Node node, int valueToCheck) {
        if (!isValue(node)) return false;

        final Value value = ((ValueExpression) node).value;
        if (value.type() != Type.NUMBER) return false;

        return value.asInt() == valueToCheck;
    }

    public static boolean isConstantValue(Node node) {
        if (!isValue(node)) return false;

        final Type type = ((ValueExpression) node).value.type();
        return ( (type == Type.NUMBER) || (type == Type.STRING) );
    }

    public static boolean isSameVariables(Node n1, Node n2) {
        if (isVariable(n1) && isVariable(n2)) {
            final VariableExpression v1 = (VariableExpression) n1;
            final VariableExpression v2 = (VariableExpression) n2;
            return v1.name.equals(v2.name);
        }
        return false;
    }

    public static Set<String> operators() {
        final Set<String> operators = new HashSet<>();
        for (BinaryExpression.Operator op : BinaryExpression.Operator.values()) {
            operators.add(op.toString());
        }
        for (UnaryExpression.Operator op : UnaryExpression.Operator.values()) {
            operators.add(op.toString());
        }
        for (ConditionalExpression.Operator op : ConditionalExpression.Operator.values()) {
            operators.add(op.getName());
        }
        return operators;
    }
}
