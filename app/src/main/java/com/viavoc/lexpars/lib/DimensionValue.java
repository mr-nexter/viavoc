package com.viavoc.lexpars.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class DimensionValue implements Value {

    public List<Integer> dimens;
    public static final Integer GENERIC = null;

    public DimensionValue() {
        dimens = new ArrayList<>();
    }

    public DimensionValue(final DimensionValue dim) {
        this.dimens = new ArrayList<>();
        if (dim != null) {
            if (dim.dimens != null) {
                this.dimens.addAll(dim.dimens);
            }
        }
    }

    public DimensionValue(List<Integer> dimens) {
        this.dimens = dimens;
    }

    public DimensionValue addDimen(Integer number) {
        dimens.add(number);
        return this;
    }

    public void clear(){
        this.dimens = new ArrayList<>();
    }

    public boolean isEmpty(){
        return dimens.isEmpty();
    }

    @Override
    public Object raw() {
        return null;
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
        final StringBuilder builder = new StringBuilder();
        if (!dimens.isEmpty()) {
            for (Integer dim : dimens) {
                builder.append("[");
                if (dim != null) builder.append(dim);
                builder.append("]");
            }
            return builder.toString();
        } else return "";
    }

    @Override
    public Type type() {
        return null;
    }

    @Override
    public int compareTo(Value o) {
        return 0;
    }

    @Override
    public String toString() {
        return asString();
    }
}
