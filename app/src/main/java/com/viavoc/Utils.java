package com.viavoc;

import com.viavoc.lexpars.lib.ClassName;
import com.viavoc.lexpars.lib.Value;
import com.viavoc.lexpars.parser.ast.Expression;
import com.viavoc.lexpars.parser.ast.Statement;

import java.util.List;

public class Utils {
    public static final String ERROR_QUEUE = "error_queue";
    public static final String LOCALE = "locale";
    public static final String THEME = "theme";

    public static String toString(Value[] values) {
        if (values != null && values.length > 0) {
            StringBuilder builder = new StringBuilder();
            int last = values.length - 1;
            for (int i = 0; i < values.length; i++) {
                builder.append(values[i].toString());
                if (i < last) {
                    builder.append(", ");
                }
            }

            return builder.toString();
        }
        return "";
    }

    public static String toStringStatements(List<Statement> statements) {
        if (statements != null && !statements.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            int last = statements.size()-1;
            for (int i = 0; i < statements.size(); i++) {
                if (statements.get(i) != null) {
                    builder.append(statements.get(i).toString());
                    if (i < last) {
                        builder.append(", ");
                    }
                }
            }

            return builder.toString();
        }
        return "";
    }

    public static String toStringStatements(List<Statement> statements, String delimeter) {
        if (statements != null && !statements.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            int last = statements.size()-1;
            for (int i = 0; i < statements.size(); i++) {
                if (statements.get(i) != null) {
                    builder.append(statements.get(i).toString());
                    if (i < last) {
                        builder.append(delimeter);
                    }
                }
            }

            return builder.toString();
        }
        return "";
    }

    public static String toStringValues(List<Value> expressions) {
        if (expressions != null && !expressions.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            int last = expressions.size()-1;
            for (int i = 0; i < expressions.size(); i++) {
                if (expressions.get(i) != null) {
                    builder.append(expressions.get(i).toString());
                    if (i < last) {
                        builder.append(", ");
                    }
                }
            }

            return builder.toString();
        }
        return "";
    }

    public static String toStringNames(List<ClassName> names) {
        if (names != null && !names.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            int last = names.size()-1;
            for (int i = 0; i < names.size(); i++) {
                if (names.get(i) != null) {
                    builder.append(names.get(i).toString());
                    if (i < last) {
                        builder.append(", ");
                    }
                }
            }

            return builder.toString();
        }
        return "";
    }

    public static String toStringExpression(List<Expression> expressions) {
        if (expressions != null && !expressions.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            int last = expressions.size()-1;
            for (int i = 0; i < expressions.size(); i++) {
                if (expressions.get(i) != null) {
                    builder.append(expressions.get(i).toString());
                    if (i < last) {
                        builder.append(", ");
                    }
                }
            }

            return builder.toString();
        }
        return "";
    }

    public static String toStringExpression(List<Expression> expressions, String delimeter) {
        if (expressions != null && !expressions.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            int last = expressions.size()-1;
            for (int i = 0; i < expressions.size(); i++) {
                if (expressions.get(i) != null) {
                    builder.append(expressions.get(i).toString());
                    if (i < last) {
                        builder.append(delimeter);
                    }
                }
            }

            return builder.toString();
        }
        return "";
    }

    public static String toString(List<Object> statements, String delimeter) {
        if (statements != null && !statements.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            int last = statements.size()-1;
            for (int i = 0; i < statements.size(); i++) {
                if (statements.get(i) != null) {
                    builder.append(statements.get(i).toString());
                    if (i < last) {
                        builder.append(delimeter);
                    }
                }
            }

            return builder.toString();
        }
        return "";
    }

    public static String toStringDelFree(List<String> words) {
        if (words != null && !words.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            int last = words.size()-1;
            for (int i = 0; i < words.size(); i++) {
                if (words.get(i) != null) {
                    builder.append(words.get(i).toString());
                    if (i < last) {
                        builder.append(" ");
                    }
                }
            }

            return builder.toString();
        }
        return "";
    }
}
