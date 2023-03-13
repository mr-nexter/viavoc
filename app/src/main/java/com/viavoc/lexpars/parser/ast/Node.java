package com.viavoc.lexpars.parser.ast;

public interface Node {
    
    void accept(Visitor visitor);

    <R, T> R accept(ResultVisitor<R, T> visitor, T input);
}
