package com.viavoc.lexpars.parser.visitors;

import com.viavoc.lexpars.parser.ast.*;

public final class FunctionAdder extends AbstractVisitor {

    @Override
    public void visit(FunctionDefineStatement s) {
        super.visit(s);
        s.execute();
    }
}
