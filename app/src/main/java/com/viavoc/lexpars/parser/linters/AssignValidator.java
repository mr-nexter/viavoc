package com.viavoc.lexpars.parser.linters;

import com.viavoc.lexpars.Console;
import com.viavoc.lexpars.lib.Variables;
import com.viavoc.lexpars.parser.ast.*;

public final class AssignValidator extends LintVisitor {

    @Override
    public void visit(AssignmentExpression s) {
        super.visit(s);
        if (s.target instanceof VariableExpression) {
            final String variable = ((VariableExpression) s.target).name;
            if (Variables.isExists(variable)) {
                Console.error(String.format(
                    "Warning: variable \"%s\" overrides constant", variable));
            }
        }
    }

    @Override
    public void visit(IncludeStatement st) {
        super.visit(st);
        applyVisitor(st, this);
    }

}
