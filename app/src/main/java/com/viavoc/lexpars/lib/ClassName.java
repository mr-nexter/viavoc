package com.viavoc.lexpars.lib;

import com.viavoc.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClassName implements Value {

    public String name;
    public DimensionValue dimension;
    public List<Value> templatingTypes;
    public boolean suppressTemplatingTypes = false;

    public ClassName(String name) {
        this.name = name;
        this.dimension = null;
        this.templatingTypes = null;
    }

    public ClassName(String name, List<Value> templatingTypes) {
        this.name = name;
        this.templatingTypes = new ArrayList<>();
        if (templatingTypes != null) {
            this.templatingTypes.addAll(templatingTypes);
        } else this.templatingTypes = null;
    }

    public ClassName(ClassName className) {
        this.name = className.name;
        this.dimension = new DimensionValue(className.dimension);
        if (className.templatingTypes != null) {
            this.templatingTypes = new ArrayList<>();
            this.templatingTypes.addAll(className.templatingTypes);
        } else this.templatingTypes = null;
    }

    public ClassName(String name, DimensionValue dimension, List<Value> templatingTypes) {
        this.name = name;
        this.dimension = dimension;
        this.templatingTypes = new ArrayList<>();
        if (templatingTypes != null) {
            this.templatingTypes.addAll(templatingTypes);
        } else this.templatingTypes = null;
    }

    public Value getDimension() {
        return dimension;
    }

    public void setDimension(final DimensionValue dim) {
        if (dim != null) {
            this.dimension = new DimensionValue(dim);
        } else this.dimension = null;
    }

    public ClassName setDimensionDeclarative(DimensionValue dim) {
        this.dimension.clear();
        for (int i = 0; i < dim.dimens.size(); i++) {
            this.dimension.addDimen(DimensionValue.GENERIC);
        }
        return this;
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
        String dimension = (this.dimension == null) ? "" : this.dimension.toString();
        String templ = "";
        if (!suppressTemplatingTypes) {
             templ = (this.templatingTypes == null) ? ""
                    : "<" + Utils.toStringValues(this.templatingTypes) + ">";
        }
        return name + templ + dimension;
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
