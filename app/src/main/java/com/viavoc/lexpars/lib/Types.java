package com.viavoc.lexpars.lib;

import java.util.HashMap;
import java.util.Map;

public final class Types {
    
    private static final Type FIRST = Type.OBJECT;
    private static final Type LAST = Type.FUNCTION;
    private static final Map<Type, String> NAMES;

    static {
        NAMES = new HashMap<>();
        NAMES.put(Type.OBJECT, "object");
        NAMES.put(Type.SHORT, "short");
        NAMES.put(Type.BYTE, "byte");
        NAMES.put(Type.INT, "int");
        NAMES.put(Type.FLOAT, "float");
        NAMES.put(Type.DOUBLE, "double");
        NAMES.put(Type.ARRAY, "array");
        NAMES.put(Type.STRING, "string");
        NAMES.put(Type.MAP, "map");
        NAMES.put(Type.FUNCTION, "function");
    }

    public static String typeToString(Type type) {
        if (NAMES.containsKey(type)) return NAMES.get(type);
        return "unknown (" + type + ")";
    }

    private Types() { }
}
