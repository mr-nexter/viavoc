package com.viavoc.lexpars.lib;

import java.util.Objects;

public final class StringValue implements Value {
    
    public static final StringValue EMPTY = new StringValue("");
    
    private final String value;

    public StringValue(String value) {
        this.value = value;
    }
    
    public int length() {
        return value.length();
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
        try {
            int i = Integer.parseInt(value);
            return i;
        } catch (NumberFormatException e) {
            if (value.length() == 3) {
                return value.charAt(1);
            } else return 0;
        }
    }
    
    @Override
    public double asNumber() {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0;
        }
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
        final StringValue other = (StringValue) obj;
        return Objects.equals(this.value, other.value);
    }
    
    @Override
    public int compareTo(Value o) {
        if (o.type() == Type.STRING) {
            return value.compareTo(((StringValue) o).value);
        }
        return asString().compareTo(o.asString());
    }
    
    @Override
    public String toString() {
        return asString();
    }

}
