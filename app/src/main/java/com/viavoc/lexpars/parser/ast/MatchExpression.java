package com.viavoc.lexpars.parser.ast;

import com.viavoc.lexpars.Console;
import com.viavoc.lexpars.lib.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class MatchExpression extends InterruptableNode implements Expression, Statement {

    public Expression expression;
    public final List<Pattern> patterns;

    public MatchExpression(Expression expression, List<Pattern> patterns) {
        this.expression = expression;
        this.patterns = patterns;
    }

    @Override
    public void execute() {
        eval();
    }

    @Override
    public Value eval() {
        return null;
    }

    @Override
    public int linesCount() {
        return 0;
    }

    private boolean matchListPatternEqualsSize(ListPattern p, List<String> parts, int partsSize, ArrayValue array) {
        // Set variables
        for (int i = 0; i < partsSize; i++) {
            Variables.define(parts.get(i), array.get(i));
        }
        if (optMatches(p)) {
            // Clean up will be provided after evaluate result
            return true;
        }
        // Clean up variables if no match
        for (String var : parts) {
            Variables.remove(var);
        }
        return false;
    }

    private boolean matchListPatternWithTail(ListPattern p, List<String> parts, int partsSize, ArrayValue array, int arraySize) {
        // Set element variables
        final int lastPart = partsSize - 1;
        for (int i = 0; i < lastPart; i++) {
            Variables.define(parts.get(i), array.get(i));
        }
        // Set tail variable
        final ArrayValue tail = new ArrayValue(arraySize - partsSize + 1);
        for (int i = lastPart; i < arraySize; i++) {
            tail.set(i - lastPart, array.get(i));
        }
        Variables.define(parts.get(lastPart), tail);
        // Check optional condition
        if (optMatches(p)) {
            // Clean up will be provided after evaluate result
            return true;
        }
        // Clean up variables
        for (String var : parts) {
            Variables.remove(var);
        }
        return false;
    }

    public void addCase(Pattern pattern) {
        if (patterns.size() > 0) {
            Pattern last = patterns.get(patterns.size()-1);
            if (last instanceof DefaultPattern) {
                if (patterns.size() >= 2) {
                    patterns.add(patterns.size()-1, pattern);
                } else patterns.add(0, pattern);
            } else patterns.add(pattern);
        } else patterns.add(pattern);
    }

    public boolean containsDefault() {
        if (patterns.get(patterns.size()-1) instanceof DefaultPattern) return true;
        else return false;
    }

    public DefaultPattern getDefault() {
        if (containsDefault()) {
            return (DefaultPattern) patterns.get(patterns.size()-1);
        } else return null;
    }

    public Pattern getPattern(Pattern pattern) {
        for (Pattern p : patterns) {
            if (p.equals(pattern)) return p;
        }
        return null;
    }

    public void addDefault(Pattern pattern) {
        if (containsDefault()) {
            patterns.remove(patterns.size()-1);
            patterns.add(pattern);
        } else patterns.add(pattern);
    }

    private boolean optMatches(Pattern pattern) {
        if (pattern.optCondition == null) return true;
        return pattern.optCondition.eval() != NumberValue.ZERO;
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
        final StringBuilder sb = new StringBuilder();
        sb.append("switch (").append(expression).append(") {");
        for (Pattern p : patterns) {
            if (p instanceof DefaultPattern) sb.append(Console.newline());
            else sb.append(Console.newline()).append("case ");
            if (p.result instanceof BlockStatement) sb.append(p);
            else sb.append(p + ";");
        }
        sb.append(Console.newline()).append("}");
        return sb.toString();

    }

    public abstract static class Pattern {
        public Statement result;
        public Expression optCondition;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            if (optCondition != null) {
                sb.append(" if ").append(optCondition);
            }
            sb.append(":").append(result);
            return sb.toString();
        }
    }

    public static class ConstantPattern extends Pattern {
        Value constant;

        public ConstantPattern(Value pattern) {
            this.constant = pattern;
        }

        @Override
        public String toString() {
            return constant.toString().concat(super.toString());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ConstantPattern) {
                return this.constant.equals(((ConstantPattern) obj).constant);
            }
            return super.equals(obj);
        }
    }

    public static class VariablePattern extends Pattern {
        public Expression variable;

        public VariablePattern(Expression pattern) {
            this.variable = pattern;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof VariablePattern) {
                return this.variable.equals(((VariablePattern) obj).variable);
            }
            return super.equals(obj);
        }

        @Override
        public String toString() {
            return variable.toString().concat(super.toString());
        }
    }

    public static class DefaultPattern extends Pattern {
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof DefaultPattern) {
                return true;
            }
            return super.equals(obj);
        }

        @Override
        public String toString() {
            return "default".concat(super.toString());
        }
    }

    public static class ListPattern extends Pattern {
        List<String> parts;

        public ListPattern() {
            this(new ArrayList<String>());
        }

        ListPattern(List<String> parts) {
            this.parts = parts;
        }

        public void add(String part) {
            parts.add(part);
        }

        @Override
        public String toString() {
            final Iterator<String> it = parts.iterator();
            if (it.hasNext()) {
                final StringBuilder sb = new StringBuilder();
                sb.append("[").append(it.next());
                while (it.hasNext()) {
                    sb.append(" :: ").append(it.next());
                }
                sb.append("]").append(super.toString());
                return sb.toString();
            }
            return "[]".concat(super.toString());
        }
    }

    public static class TuplePattern extends Pattern {
        public List<Expression> values;

        public TuplePattern() {
            this(new ArrayList<Expression>());
        }

        public TuplePattern(List<Expression> parts) {
            this.values = parts;
        }

        public void addAny() {
            values.add(ANY);
        }

        public void add(Expression value) {
            values.add(value);
        }

        @Override
        public String toString() {
            final Iterator<Expression> it = values.iterator();
            if (it.hasNext()) {
                final StringBuilder sb = new StringBuilder();
                sb.append('(').append(it.next());
                while (it.hasNext()) {
                    sb.append(", ").append(it.next());
                }
                sb.append(')').append(super.toString());
                return sb.toString();
            }
            return "()".concat(super.toString());
        }

        private static final Expression ANY = new Expression() {
            @Override
            public Value eval() {
                return NumberValue.ONE;
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
                return "_".concat(super.toString());
            }
        };
    }
}
