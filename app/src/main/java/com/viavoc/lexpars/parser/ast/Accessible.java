package com.viavoc.lexpars.parser.ast;

import com.viavoc.lexpars.lib.Value;

public interface Accessible extends Node {

    Value get();
    
    Value set(Value value);
}
