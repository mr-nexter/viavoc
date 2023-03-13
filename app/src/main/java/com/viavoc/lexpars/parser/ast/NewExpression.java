package com.viavoc.lexpars.parser.ast;

import com.viavoc.Utils;
import com.viavoc.lexpars.lib.Value;

import java.util.List;
import java.util.stream.Collectors;

public class NewExpression implements Expression {

    public Value name;
    public List<Expression> arguments;
    public Expression initialisedBy;
    private boolean objectCreation = true;

    public NewExpression(Value name, List<Expression> arguments, Expression initialisedBy) {
        this.name = name;
        this.arguments = arguments;
        this.initialisedBy = initialisedBy;
    }

    public NewExpression(Value name) {
        this.name = name;
        objectCreation = false;
    }

    public NewExpression setInitialisedBy(Expression initialisedBy) {
        this.initialisedBy = initialisedBy;
        return this;
    }

    @Override
    public Value eval() {
        return null;
    }

    @Override
    public int linesCount() {
        return 0;
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
        StringBuilder builder = new StringBuilder("new ");
        builder.append(name);
        if (objectCreation) {
            builder.append("(");
            builder.append(Utils.toStringExpression(arguments));
            builder.append(")");
        }
        if (initialisedBy != null) builder.append(initialisedBy);

        return builder.toString();
    }
}
