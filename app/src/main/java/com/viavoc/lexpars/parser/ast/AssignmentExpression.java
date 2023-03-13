package com.viavoc.lexpars.parser.ast;

import com.viavoc.lexpars.lib.Value;

public final class AssignmentExpression extends InterruptableNode implements Expression, Statement {

    public final Accessible target;
    public final BinaryExpression.Operator operation;
    public Expression expression;
    public boolean hideDetails = false;
    
    public AssignmentExpression(BinaryExpression.Operator operation, Accessible target, Expression expr) {
        this.operation = operation;
        this.target = target;
        this.expression = expr;
    }

    public AssignmentExpression(AssignmentExpression assignmentExpression) {
        this.operation = assignmentExpression.operation;
        this.target = assignmentExpression.target;
        this.expression = assignmentExpression.expression;
        this.hideDetails = assignmentExpression.hideDetails;
    }

    public void setDetails(boolean f){
        this.hideDetails = f;
    }
    
    @Override
    public void execute() {
        eval();
    }

    @Override
    public Value eval() {
        super.interruptionCheck();
        if (operation == null) {
            // Simple assignment
            return target.set(expression.eval());
        }
        final Expression expr1 = new ValueExpression(target.get());
        final Expression expr2 = new ValueExpression(expression.eval());
        return target.set(new BinaryExpression(operation, expr1, expr2).eval());
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
        if (hideDetails) {
            return target.toString();
        } else {
            final String op = (operation == null) ? "" : operation.toString();
            return String.format("%s %s= %s;", target, op, expression);
        }
    }
}
