package com.viavoc.lexpars.parser.ast;

import com.viavoc.lexpars.parser.Lexer;
import com.viavoc.lexpars.parser.Parser;
import com.viavoc.lexpars.parser.SourceLoader;
import com.viavoc.lexpars.parser.Token;
import com.viavoc.lexpars.parser.visitors.FunctionAdder;
import java.io.IOException;
import java.util.List;

public final class IncludeStatement extends InterruptableNode implements Statement {

    public final Expression expression;
    
    public IncludeStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void execute() {
        super.interruptionCheck();
        try {
            final Statement program = loadProgram(expression.eval().asString());
            if (program != null) {
                program.accept(new FunctionAdder());
                program.execute();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public int linesCount() {
        return 1;
    }

    public Statement loadProgram(String path) throws IOException {
        final String input = SourceLoader.readSource(path);
        final List<Token> tokens = Lexer.tokenize(input);
        final Parser parser = new Parser(tokens, null);
        final Statement program = parser.parse();
        if (parser.getParseErrors().hasErrors()) {
            return null;
        }
        return program;
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
        return "include " + expression;
    }
}
