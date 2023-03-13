package com.viavoc.lexpars.parser.ast;

public class ForEachStatement implements Statement {

    public final VariableExpression variable;
    public final Expression container;
    public Statement body;

    public ForEachStatement(VariableExpression variable, Expression container, Statement body) {
        this.variable = variable;
        this.container = container;
        this.body = body;
    }

    @Override
    public String toString(){
        return String.format("for (%s : %s) %s", variable, container, body);
    }

    @Override
    public void execute() {

    }

    @Override
    public int linesCount() {
        int lines = 1;
        if (body != null) lines += body.linesCount();
        return lines;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <R, T> R accept(ResultVisitor<R, T> visitor, T input) {
        return visitor.visit(this, input);
    }
}
