package com.viavoc.lexpars.parser.ast;

import com.viavoc.lexpars.lib.*;
import com.viavoc.lexpars.parser.TokenType;

import java.util.List;

public final class FunctionDefineStatement implements Statement {

    public List<TokenType> modifiers;
    public Value returningType;
    public String name;
    public Arguments arguments;
    public Statement body;
    public boolean isConstructor = false;

    public FunctionDefineStatement(String name, Arguments arguments, Statement body) {
        this.name = name;
        this.arguments = arguments;
        this.body = body;
    }

    public FunctionDefineStatement(List<TokenType> modifiers, Value returningType,
                                   String name, Arguments arguments, Statement body) {
        this.modifiers = modifiers;
        this.returningType = returningType;
        this.name = name;
        this.arguments = arguments;
        this.body = body;
    }

    @Override
    public void execute() {
        Functions.set(name, new UserDefinedFunction(arguments, body));
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
        StringBuilder builder = new StringBuilder();

        if (modifiers != null && !modifiers.isEmpty()) {
            for (TokenType modifier : modifiers) {
                builder.append(Tokens.toString(modifier)).append(" ");
            }
        }

        if (!isConstructor) builder.append(returningType == null ? " void " :  " " + returningType + " ");
        else builder.append(" ");
        builder.append(name).append(arguments);

        if (body instanceof ReturnStatement) {
            builder.append(" = ").append(((ReturnStatement) body).expression);
        } else {
            builder.append(" ").append(body);
        }

        return builder.toString();
    }
}
