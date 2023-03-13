package com.viavoc.lexpars.lib;

public interface Value extends Comparable<Value> {
    
    Object raw();
    
    int asInt();
    
    double asNumber();
    
    String asString();
    
    Type type();
}
