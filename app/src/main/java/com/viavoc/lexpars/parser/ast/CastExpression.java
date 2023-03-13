package com.viavoc.lexpars.parser.ast;

import com.viavoc.lexpars.lib.ClassName;
import com.viavoc.lexpars.lib.Value;

public class CastExpression implements Expression {

    public ValueExpression castTo;
    public Expression toCast;

    public CastExpression(ValueExpression castTo, Expression toCast) {
        this.castTo = castTo;
        this.toCast = toCast;
    }

    public CastExpression(ClassName castTo, Expression toCast) {
        this.castTo = new ValueExpression(castTo);
        this.toCast = toCast;
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

    }

    @Override
    public <R, T> R accept(ResultVisitor<R, T> visitor, T input) {
        return null;
    }

    @Override
    public String toString() {
        return "(" + castTo + ") " + toCast;
    }
}
