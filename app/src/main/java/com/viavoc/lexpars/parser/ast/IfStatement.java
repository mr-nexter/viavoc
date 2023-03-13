package com.viavoc.lexpars.parser.ast;

public final class IfStatement extends InterruptableNode implements Statement {

    public Expression expression;
    public Statement ifStatement, elseStatement;

    public IfStatement(Expression expression, Statement ifStatement, Statement elseStatement) {
        this.expression = expression;
        this.ifStatement = ifStatement;
        this.elseStatement = elseStatement;
    }
    
    @Override
    public void execute() {
        super.interruptionCheck();
        final int result = expression.eval().asInt();
        if (result != 0) {
            ifStatement.execute();
        } else if (elseStatement != null) {
            elseStatement.execute();
        }
    }

    @Override
    public int linesCount() {
        int linesNumb = 1;
        if (ifStatement != null) linesNumb += ifStatement.linesCount();
        if (elseStatement != null) linesNumb += elseStatement.linesCount();
        return linesNumb;
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
        final StringBuilder result = new StringBuilder();
        result.append("if (").append(expression).append(") ").append(ifStatement);
        if (!(ifStatement instanceof BlockStatement)) result.append("\n");
        if (elseStatement != null) {
            result.append("else ").append(elseStatement);
        }
        return result.toString();
    }
}
