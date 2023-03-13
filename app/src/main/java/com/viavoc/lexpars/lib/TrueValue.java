package com.viavoc.lexpars.lib;

import java.util.Objects;

public final class TrueValue implements Value {


    private final String value;

    public TrueValue() {
        this.value = "true";
    }

    @Override
    public Type type() {
        return Type.STRING;
    }

    @Override
    public Object raw() {
        return value;
    }
    
    @Override
    public int asInt() {
        return 0;
    }
    
    @Override
    public double asNumber() {
        return 0;
    }

    @Override
    public String asString() {
        return value;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.value);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass())
            return false;
        final TrueValue other = (TrueValue) obj;
        return Objects.equals(this.value, other.value);
    }
    
    @Override
    public int compareTo(Value o) {
        if (o.type() == Type.STRING) {
            return value.compareTo(((TrueValue) o).value);
        }
        return asString().compareTo(o.asString());
    }
    
    @Override
    public String toString() {
        return asString();
    }

}
