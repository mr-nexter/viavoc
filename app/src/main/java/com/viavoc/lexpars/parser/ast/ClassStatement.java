package com.viavoc.lexpars.parser.ast;

import com.viavoc.Utils;
import com.viavoc.lexpars.Console;
import com.viavoc.lexpars.lib.ClassName;
import com.viavoc.lexpars.lib.Tokens;
import com.viavoc.lexpars.parser.TokenType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ClassStatement extends InterruptableNode implements Statement {

    public String packageName;
    public List<String> imports;
    public List<TokenType> modifiers;
    public ClassName name;
    public ClassName extending;
    public List<ClassName> implementing;
    public LinkedList<Statement> fields;
    public LinkedList<Statement> construstos;
    public LinkedList<Statement> methods;

    public LinkedList<ClassStatement> innerClasses;

    public ClassStatement(List<TokenType> modifiers, ClassName name, ClassName extending, List<ClassName> implementing) {
        packageName = null;
        this.modifiers = modifiers;
        this.name = name;
        this.extending = extending;
        this.implementing = implementing;
        fields = new LinkedList<>();
        construstos = new LinkedList<>();
        methods = new LinkedList<>();
        innerClasses = new LinkedList<>();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (packageName != null) builder.append("package ").append(packageName).append(";\n\n");

        if (imports != null && !imports.isEmpty()) {
            for (String imp : imports) {
                builder.append("import ").append(imp).append(";\n");
            }
        }

        if (modifiers != null && !modifiers.isEmpty()) {
            for (TokenType token : modifiers) {
                if (token != null) {
                    builder.append(Tokens.toString(token)).append(" ");
                }
            }
        }

        builder.append("class ").append(name);

        if (extending != null) {
            builder.append("extends ").append(extending);
        }

        if (implementing != null && !implementing.isEmpty()){
            builder.append("implements ").append(Utils.toStringNames(implementing));
        }

        builder.append(" {").append(Console.newline());
        builder = BlockStatement.prepareToPrint(builder, fields);
        builder = BlockStatement.prepareToPrint(builder, construstos);
        builder = BlockStatement.prepareToPrint(builder, methods);

        return builder.append("\n}").toString();
    }

    @Override
    public void execute() {}

    @Override
    public int linesCount() {
        int lines = 1;
        for (Statement statement : fields) {
            if (statement.linesCount() == 0) {
                lines += 1;
            } else lines += statement.linesCount();
        }

        for (Statement statement : construstos) {
            if (statement.linesCount() == 0) {
                lines += 1;
            } else lines += statement.linesCount();
        }

        for (Statement statement : methods) {
            if (statement.linesCount() == 0) {
                lines += 1;
            } else lines += statement.linesCount();
        }

        return lines;
    }

    public void addClass(ClassStatement classStatement) {
        innerClasses.add(classStatement);
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
