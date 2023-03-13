package com.viavoc.lexpars.parser.ast;

import com.viavoc.lexpars.exceptions.VariableDoesNotExistsException;
import com.viavoc.lexpars.lib.*;
import com.viavoc.lexpars.parser.Token;
import com.viavoc.lexpars.parser.TokenType;

import java.util.List;
import java.util.stream.Collectors;

public final class VariableExpression extends InterruptableNode implements Expression, Accessible {

    public final String name;
    public boolean isDefinition;
    public boolean showModifiers = true;
    public List<TokenType> modifiers;

    public VariableExpression(String name) {
        this.name = name;
        this.isDefinition = false;
        this.modifiers = null;
    }

    public VariableExpression(VariableExpression var) {
        this.name = var.name;
        this.isDefinition = var.isDefinition;
        this.modifiers = var.modifiers;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VariableExpression) {
            return ((VariableExpression) obj).name.equals(this.name);
        }
        return false;
    }

    @Override
    public Value eval() {
        super.interruptionCheck();
        return null;
    }

    @Override
    public int linesCount() {
        return 0;
    }

    @Override
    public Value get() {
        if (!Variables.isExists(name)) throw new VariableDoesNotExistsException(name);
        return Variables.get(name);
    }

    public Value getType() {
        return this.get();
    }

    @Override
    public Value set(Value value) {
        Variables.set(name, value);
        this.isDefinition = true;
        return value;
    }

    public VariableExpression set(Value value, Value type){
        Variables.set(name, type);
        this.isDefinition = true;
        return this;
    }

    public VariableExpression setModifiers(List<TokenType> modifiers){
        this.modifiers = modifiers;
        return this;
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
        if (isDefinition) {
            StringBuilder builder = new StringBuilder();

            if (modifiers != null && !modifiers.isEmpty() && showModifiers) {
                for (TokenType modifier : modifiers) {
                    builder.append(Tokens.toString(modifier)).append(" ");
                }
            }
            builder.append(this.get()).append(" ").append(this.name);

            return builder.toString();
        } else return name;
    }
}
