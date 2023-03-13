package com.viavoc.lexpars.parser.ast;

import com.viavoc.lexpars.lib.Value;

import java.io.Serializable;

public interface Expression extends Node, Serializable {

    Value eval();
    int linesCount();
}
