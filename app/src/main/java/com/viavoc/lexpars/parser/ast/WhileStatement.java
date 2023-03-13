package com.viavoc.lexpars.parser.ast;

public final class WhileStatement extends InterruptableNode implements Statement {

    public Expression condition;
    public Statement statement;

    public WhileStatement(Expression condition, Statement statement) {
        this.condition = condition;
        this.statement = statement;
    }
    
    @Override
    public void execute() {
        super.interruptionCheck();
        while (condition.eval().asInt() != 0) {
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
        return 1 + statement.linesCount();
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
        return "while (" + condition + ")  " + statement;
    }
}
