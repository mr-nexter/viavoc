package com.viavoc.lexpars.parser.ast;

public final class ForStatement extends InterruptableNode implements Statement {
    
    public Statement initialization;
    public Expression termination;
    public Statement increment;
    public Statement statement;

    public ForStatement(Statement initialization, Expression termination, Statement increment, Statement block) {
        this.initialization = initialization;
        this.termination = termination;
        this.increment = increment;
        this.statement = block;
    }

    @Override
    public void execute() {
        super.interruptionCheck();
        for (initialization.execute(); termination.eval().asInt() != 0; increment.execute()) {
            try {
                statement.execute();
            } catch (BreakStatement bs) {
                break;
            } catch (ContinueStatement cs) {
                // continue;
            }
        }
    }

    @Override
    public int linesCount() {
        int lines = 1;
        if (statement != null) lines += statement.linesCount();
        return lines;
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
        return "for (" + initialization.toString() + " " + termination.toString() + "; " + increment.toString() + " ) " + statement.toString();
    }
}
