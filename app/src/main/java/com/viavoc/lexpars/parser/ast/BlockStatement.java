package com.viavoc.lexpars.parser.ast;

import com.viavoc.lexpars.Console;
import java.util.LinkedList;
import java.util.List;

public final class BlockStatement extends InterruptableNode implements Statement {

    public final LinkedList<Statement> statements;
    public boolean addLastLineBreakNecessary = true;

    public BlockStatement() {
        statements = new LinkedList<>();
    }
    
    public BlockStatement add(Statement statement) {
        statements.add(statement);
        return this;
    }

    public Statement getLast() {
        return !statements.isEmpty() ? statements.getLast() : null;
    }

    public void addPrelast(Statement statement) {
        if (statements.size() >= 2) {
            statements.add(statements.size()-2, statement);
        } else statements.add(0, statement);
    }

    @Override
    public void execute() {
        super.interruptionCheck();
        for (Statement statement : statements) {
            statement.execute();
        }
    }

    @Override
    public int linesCount() {
        int lines = 1;
        for (Statement statement : statements) {
            if (statement != null) {
                if (statement.linesCount() == 0) {
                    lines += 1;
                } else lines += statement.linesCount();
            }
        }
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

    public static StringBuilder prepareToPrint(StringBuilder builder, List<Statement> statementsList) {
        for (Statement statement : statementsList) {
            if (statement != null) {
                // preventing it from printing extra and unnecessary ';' after blocks
                boolean inst = (statement instanceof BlockStatement) | (statement instanceof ForStatement)
                        | (statement instanceof TryCatchStatement) | (statement instanceof ClassStatement)
                        | (statement instanceof WhileStatement) | (statement instanceof ForEachStatement)
                        | (statement instanceof ForeachArrayStatement) | (statement instanceof ForeachMapStatement)
                        | (statement instanceof FunctionDefineStatement) | (statement instanceof MatchExpression)
                        | (statement instanceof IfStatement) | (statement instanceof DoWhileStatement);
                //| (statement instanceof AssignmentExpression)
                String statm = statement.toString();
                builder.append(statm);
                if (!inst) {
                    if (statm.charAt(statm.length()-1) != ';') {
                        builder.append(";");
                    }
                    builder.append(Console.newline());
                }
            }
        }
        return builder;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("{").append(Console.newline());
        result = prepareToPrint(result, statements);
        result.append("}");
        if (addLastLineBreakNecessary) {
            result.append(Console.newline());
        }
        return result.toString();
    }
}
