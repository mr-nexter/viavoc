package com.viavoc.lexpars.parser.ast;

import com.viavoc.lexpars.lib.Value;

public final class ExprStatement extends InterruptableNode implements Expression, Statement {
    
    public final Expression expr;
    
    public ExprStatement(Expression function) {
        this.expr = function;
    }

    @Override
    public void execute() {
        super.interruptionCheck();
        expr.eval();
    }
    
    @Override
    public Value eval() {
        return expr.eval();
    }

    @Override
    public int linesCount() {
        if (expr.linesCount() != 0) return expr.linesCount();
        else return 1;
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
        return expr.toString();
    }
}
