package com.viavoc.lexpars.parser.ast;

import com.viavoc.lexpars.exceptions.TypeException;
import com.viavoc.lexpars.lib.*;
import java.util.List;

public class ContainerAccessExpression implements Expression, Accessible {

    public final Expression root;
    public final List<Expression> indices;
    public boolean rootIsVariable;
    public boolean isStaticClassName = false;
    public boolean isDelimiterNecessary = true;

    public ContainerAccessExpression(String variable, List<Expression> indices) {
        this(new VariableExpression(variable), indices);
    }

    public ContainerAccessExpression(Expression root, List<Expression> indices) {
        rootIsVariable = root instanceof VariableExpression;
        if (root instanceof ValueExpression) {
            if (((ValueExpression) root).value instanceof ClassName) {
                isStaticClassName = true;
            }
        }
        this.root = root;
        this.indices = indices;
    }

    public ContainerAccessExpression(Expression root, List<Expression> indices, boolean isDelimitersNecessary) {
        // int[][] i = new int[2][2]
        // <> [][] <> = <> <> [][]
        this.root = root;
        this.indices = indices;
        this.isDelimiterNecessary = isDelimitersNecessary;
    }

    public boolean rootIsVariable() {
        return rootIsVariable;
    }

    public Expression getRoot() {
        return root;
    }

    @Override
    public Value eval() {
        return get();
    }

    @Override
    public int linesCount() {
        return 0;
    }

    @Override
    public Value get() {
        final Value container = getContainer();
        final Value lastIndex = lastIndex();
        switch (container.type()) {
            case ARRAY:
                final int arrayIndex = lastIndex.asInt();
                return ((ArrayValue) container).get(arrayIndex);

            case MAP:
                return ((MapValue) container).get(lastIndex);
                
            default:
                throw new TypeException("Array or map expected. Got " + Types.typeToString(container.type()));
        }
    }

    @Override
    public Value set(Value value) {
        final Value container = getContainer();
        final Value lastIndex = lastIndex();
        switch (container.type()) {
            case ARRAY:
                final int arrayIndex = lastIndex.asInt();
                ((ArrayValue) container).set(arrayIndex, value);
                return value;

            case MAP:
                ((MapValue) container).set(lastIndex, value);
                return value;
                
            default:
                throw new TypeException("Array or map expected. Got " + container.type());
        }
    }
    
    public Value getContainer() {
        Value container = root.eval();
        final int last = indices.size() - 1;
        for (int i = 0; i < last; i++) {
            final Value index = index(i);
            switch (container.type()) {
                case ARRAY:
                    final int arrayIndex = index.asInt();
                    container = ((ArrayValue) container).get(arrayIndex);
                    break;
                    
                case MAP:
                    container = ((MapValue) container).get(index);
                    break;
                    
                default:
                    throw new TypeException("Array or map expected");
            }
        }
        return container;
    }
    
    public Value lastIndex() {
        return index(indices.size() - 1);
    }
    
    private Value index(int index) {
        return indices.get(index).eval();
    }
    
    public MapValue consumeMap(Value value) {
        if (value.type() != Type.MAP) {
            throw new TypeException("Map expected");
        }
        return (MapValue) value;
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
        final StringBuilder builder = new StringBuilder();
        if (isDelimiterNecessary) {
            if (rootIsVariable || isStaticClassName) {
                builder.append(root);
            } else {
                builder.append(root).append("()");
            }

            for (Expression e : indices) {
                if (!(e instanceof ElementAccessExpression)) {
                    builder.append('.');
                }
                builder.append(e);
            }
        } else {
            builder.append(root);
            for (Expression ind : indices) {
                if (ind != null) {
                    builder.append(ind);
                }
            }
        }

        return  builder.toString();
    }
}
