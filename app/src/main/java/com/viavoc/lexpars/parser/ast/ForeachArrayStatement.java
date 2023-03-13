package com.viavoc.lexpars.parser.ast;

import com.viavoc.lexpars.lib.Value;
import com.viavoc.lexpars.lib.Variables;

public final class ForeachArrayStatement extends InterruptableNode implements Statement {
    
    public final String variable;
    public final Expression container;
    public final Statement body;

    public ForeachArrayStatement(String variable, Expression container, Statement body) {
        this.variable = variable;
        this.container = container;
        this.body = body;
    }

    @Override
    public void execute() {
        super.interruptionCheck();
        final Value previousVariableValue = Variables.isExists(variable) ? Variables.get(variable) : null;
        final Iterable<Value> iterator = (Iterable<Value>) container.eval();
        for (Value value : iterator) {
            Variables.set(variable, value);
            try {
                body.execute();
            } catch (BreakStatement bs) {
                break;
            } catch (ContinueStatement cs) {
                // continue;
            }
        }
        // Восстанавливаем переменную
        if (previousVariableValue != null) {
            Variables.set(variable, previousVariableValue);
        }
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
    public <R, T> R accept(ResultVisitor<R, T> visitor, T t) {
        return visitor.visit(this, t);
    }

    @Override
    public String toString() {
        return String.format("for (%s : %s) %s", variable, container, body);
    }
}
