package com.viavoc.lexpars.lib.core;

public class SearchResult {
    public String name;
    public String packageName;
    public ResultType type;
    public Class instance = null;

    public SearchResult(String name, String packageName, ResultType type) {
        this.name = name;
        this.packageName = packageName;
        this.type = type;
    }

    public SearchResult setClass(Class instance){
        this.instance = instance;
        return this;
    }
}
