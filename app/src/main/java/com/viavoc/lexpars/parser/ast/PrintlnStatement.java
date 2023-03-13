package com.viavoc.lexpars.parser.ast;

import com.viavoc.lexpars.Console;

public final class PrintlnStatement extends InterruptableNode implements Statement {
    
    public final Expression expression;

    public PrintlnStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void execute() { }

    @Override
    public int linesCount() {
        return 1;
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
        String out = expression == null ? "" : expression.toString();
        return "System.out.println(" + out + ")";
    }
}
