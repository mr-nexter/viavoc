package com.viavoc.lexpars.parser.ast;

import java.io.Serializable;

public interface Visitor extends Serializable {

    void visit(ArrayExpression s);
    void visit(AssignmentExpression s);
    void visit(BinaryExpression s);
    void visit(ClassStatement s);
    void visit(BlockStatement s);
    void visit(BreakStatement s);
    void visit(ConditionalExpression s);
    void visit(ContainerAccessExpression s);
    void visit(ContinueStatement s);
    void visit(DoWhileStatement s);
    void visit(DestructuringAssignmentStatement s);
    void visit(ForStatement s);
    void visit(ForEachStatement s);
    void visit(ForeachArrayStatement s);
    void visit(ForeachMapStatement s);
    void visit(FunctionDefineStatement s);
    void visit(FunctionReferenceExpression e);
    void visit(ExprStatement s);
    void visit(FunctionalExpression s);
    void visit(IfStatement s);
    void visit(IncludeStatement s);
    void visit(MapExpression s);
    void visit(MatchExpression s);
    void visit(NewExpression s);
    void visit(PrintStatement s);
    void visit(PrintlnStatement s);
    void visit(ReturnStatement s);
    void visit(TernaryExpression s);
    void visit(TryCatchStatement s);
    void visit(TypedAssignmentExpression s);
    void visit(UnaryExpression s);
    void visit(ValueExpression s);
    void visit(VariableExpression s);
    void visit(WhileStatement st);
}
