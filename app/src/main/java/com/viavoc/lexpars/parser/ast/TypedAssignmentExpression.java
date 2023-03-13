package com.viavoc.lexpars.parser.ast;

import com.viavoc.lexpars.lib.Value;

public final class TypedAssignmentExpression extends InterruptableNode implements Expression, Statement {

    public final Value targetType;
    public final Accessible target;
    public final BinaryExpression.Operator operation;
    public final Expression expression;

    public TypedAssignmentExpression(BinaryExpression.Operator operation, Value targetType, Accessible target, Expression expr) {
        this.targetType = targetType;
        this.operation = operation;
        this.target = target;
        this.expression = expr;
    }
    
    @Override
    public void execute() {
        eval();
    }

    @Override
    public Value eval() {
        return null;
    }

    @Override
    public int linesCount() {
        return 0;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <R, T> R accept(ResultVisitor<R, T> visitor, T t) {
        return visitor.visit(this, t);
    }

    @Override
    public String toString() {
        final String op = (operation == null) ? "" : operation.toString();
        return String.format("%s %s= %s", target, op, expression);
    }
}
