package com.viavoc.lexpars.parser.ast;

import java.io.Serializable;

public interface Statement extends Node, Serializable {
    
    void execute();
    int linesCount();
}
