package com.viavoc.lexpars.parser.optimization;

import com.viavoc.lexpars.parser.ast.Node;

import java.io.Serializable;

public interface Optimizable extends Serializable {

    Node optimize(Node node);

    int optimizationsCount();

    String summaryInfo();
}
