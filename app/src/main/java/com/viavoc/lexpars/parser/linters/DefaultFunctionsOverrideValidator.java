package com.viavoc.lexpars.parser.linters;

import com.viavoc.lexpars.Console;
import com.viavoc.lexpars.lib.Functions;
import com.viavoc.lexpars.parser.ast.*;

public final class DefaultFunctionsOverrideValidator extends LintVisitor {

    @Override
    public void visit(FunctionDefineStatement s) {
        super.visit(s);
        if (Functions.isExists(s.name)) {
            Console.error(String.format(
                    "Warning: function \"%s\" overrides default module function", s.name));
        }
    }

    @Override
    public void visit(IncludeStatement st) {
        super.visit(st);
        applyVisitor(st, this);
    }

}
