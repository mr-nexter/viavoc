package com.viavoc.lexpars.parser.linters;

import com.viavoc.lexpars.parser.ast.IncludeStatement;
import com.viavoc.lexpars.parser.ast.Statement;
import com.viavoc.lexpars.parser.ast.Visitor;
import com.viavoc.lexpars.parser.visitors.AbstractVisitor;
import com.viavoc.lexpars.parser.visitors.VisitorUtils;

public abstract class LintVisitor extends AbstractVisitor {

    protected void applyVisitor(IncludeStatement s, Visitor visitor) {
        final Statement program = VisitorUtils.includeProgram(s);
        if (program != null) {
            program.accept(visitor);
        }
    }
}
