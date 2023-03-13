package com.viavoc.lexpars.parser.ast;

public final class DoWhileStatement extends InterruptableNode implements Statement {
    
    public Expression condition;
    public Statement statement;

    public DoWhileStatement(Expression condition, Statement statement) {
        this.condition = condition;
        this.statement = statement;
    }
    
    @Override
    public void execute() {
        super.interruptionCheck();
        do {
            try {
                statement.execute();
            } catch (BreakStatement bs) {
                break;
            } catch (ContinueStatement cs) {
                // continue;
            }
        }
        while (condition.eval().asInt() != 0);
    }

    @Override
    public int linesCount() {
        int lines = 2;
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
        if (statement instanceof BlockStatement) {
            ((BlockStatement) statement).addLastLineBreakNecessary = false;
        }
        return "do " + statement + " while (" + condition + ");\n";
    }
}
