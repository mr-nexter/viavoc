package com.viavoc.lexpars.parser.ast;

import com.viavoc.lexpars.lib.Value;

public class ElementAccessExpression implements Expression {

    Expression expression;

    public ElementAccessExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "[" + expression + "]";
    }

    @Override
    public Value eval() { return null; }

    @Override
    public int linesCount() { return 0; }

    @Override
    public void accept(Visitor visitor) { }

    @Override
    public <R, T> R accept(ResultVisitor<R, T> visitor, T input) { return null; }
}
