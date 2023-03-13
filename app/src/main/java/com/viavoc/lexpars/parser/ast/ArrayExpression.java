package com.viavoc.lexpars.parser.ast;

import com.viavoc.Utils;
import com.viavoc.lexpars.lib.ArrayValue;
import com.viavoc.lexpars.lib.Value;
import java.util.List;

public final class ArrayExpression implements Expression {
    
    public final List<Expression> elements;

    public ArrayExpression(List<Expression> arguments) {
        this.elements = arguments;
    }
    
    @Override
    public Value eval() {
        final int size = elements.size();
        final ArrayValue array = new ArrayValue(size);
        for (int i = 0; i < size; i++) {
            array.set(i, elements.get(i).eval());
        }
        return array;
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
        return "{ " + Utils.toStringExpression(elements) + " } ";
    }
}
