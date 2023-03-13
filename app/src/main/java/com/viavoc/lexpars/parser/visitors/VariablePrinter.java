package com.viavoc.lexpars.parser.visitors;

import com.viavoc.lexpars.Console;
import com.viavoc.lexpars.parser.ast.*;

public final class VariablePrinter extends AbstractVisitor {

    @Override
    public void visit(AssignmentExpression s) {
        super.visit(s);
        Console.println(s.target);
    }

    @Override
    public void visit(VariableExpression s) {
        super.visit(s);
        Console.println(s.name);
    }
}
