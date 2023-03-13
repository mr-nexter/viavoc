package com.viavoc.lexpars.parser.ast;

import com.viavoc.lexpars.lib.Value;

public final class Argument {

    private final Value name;
    private final Expression valueExpr;

    public Argument(Value name) {
        this(name, null);
    }

    public Argument(Value name, Expression valueExpr) {
        this.name = name;
        this.valueExpr = valueExpr;
    }

    public String getName() {
        return name.asString();
    }

    public Expression getValueExpr() {
        return valueExpr;
    }

    @Override
    public String toString() {
        if (name == null && valueExpr != null) {
            return valueExpr.toString();
        } else return name + (valueExpr == null ? "" : " = " + valueExpr);
    }
}
