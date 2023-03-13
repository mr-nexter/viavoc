package com.viavoc.lexpars.models;

public class Pair<T, V> {
    public T name;
    public V type;

    public Pair(T name, V type){
        this.name = name;
        this.type = type;
    }
}
