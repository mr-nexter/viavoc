package com.viavoc.lexpars.lib;

import java.io.Serializable;
public interface Function extends Serializable {

    Value execute(Value... args);
}
