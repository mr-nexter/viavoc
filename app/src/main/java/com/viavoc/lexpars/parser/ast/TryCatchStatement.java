package com.viavoc.lexpars.parser.ast;

import com.viavoc.lexpars.lib.Tokens;
import com.viavoc.lexpars.models.Pair;
import com.viavoc.lexpars.parser.ParseError;
import com.viavoc.lexpars.parser.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TryCatchStatement extends InterruptableNode implements Statement {

    public Statement tryParams;
    public Statement tryBody;
    public List<Pair<Expression, Statement>> catchs;
    public Statement finallyBody;

    public TryCatchStatement(Statement tryParams, Statement tryBody, List<Pair<Expression, Statement>> catchPairs) {
        this.tryParams = tryParams;
        this.tryBody = tryBody;
        this.catchs = catchPairs;
    }

    public TryCatchStatement(Statement tryParams, Statement tryBody, List<Pair<Expression, Statement>> catchPairs, Statement finallyBody) {
        this.tryParams = tryParams;
        this.tryBody = tryBody;
        this.catchs = catchPairs;
        this.finallyBody = finallyBody;
    }

    public boolean containsCatchBody(BlockStatement body) {
        if (catchs.size() > 0) {
            for (Pair<Expression, Statement> catcher : catchs) {
                if (catcher.type.equals(body)) return true;
            }
            return false;
        } else return false;
    }

    public Statement getLastCatchBody() {
        if (!this.catchs.isEmpty()) {
            return this.catchs.get(this.catchs.size()-1).type;
        } else return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("try ");

        if (tryParams != null) {
            builder.append("(").append(tryParams).append(") ");
        }

        builder.append(tryBody);
        for (Pair<Expression, Statement> catcher : catchs) {
            builder.append("catch (").append(catcher.name).append(" ) ").append(catcher.type);
        }

        String f = finallyBody == null ? "" : "finally " + finallyBody.toString();
        builder.append(f);

        return builder.toString();
    }

    @Override
    public void execute() {}

    @Override
    public int linesCount() {
        int linesNumb = 1;
        if (tryBody != null) linesNumb += tryBody.linesCount();
        for (Pair<Expression, Statement> catcher : catchs) {
            linesNumb += (1 + catcher.type.linesCount());
        }
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
}
