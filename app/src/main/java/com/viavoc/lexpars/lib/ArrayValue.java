package com.viavoc.lexpars.lib;

import com.viavoc.Utils;
import com.viavoc.lexpars.exceptions.TypeException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ArrayValue implements Value, Iterable<Value> {

    public static ArrayValue of(byte[] array) {
        final int size = array.length;
        final ArrayValue result = new ArrayValue(size);
        for (int i = 0; i < size; i++) {
            result.set(i, NumberValue.of(array[i]));
        }
        return result;
    }

    public static ArrayValue of(String[] array) {
        final int size = array.length;
        final ArrayValue result = new ArrayValue(size);
        for (int i = 0; i < size; i++) {
            result.set(i, new StringValue(array[i]));
        }
        return result;
    }

    public static ArrayValue add(ArrayValue array, Value value) {
        final int last = array.elements.length;
        final ArrayValue result = new ArrayValue(last + 1);
        System.arraycopy(array.elements, 0, result.elements, 0, last);
        result.elements[last] = value;
        return result;
    }

    public static ArrayValue merge(ArrayValue array1, ArrayValue array2) {
        final int length1 = array1.elements.length;
        final int length2 = array2.elements.length;
        final int length = length1 + length2;
        final ArrayValue result = new ArrayValue(length);
        System.arraycopy(array1.elements, 0, result.elements, 0, length1);
        System.arraycopy(array2.elements, 0, result.elements, length1, length2);
        return result;
    }

    private final Value[] elements;

    public ArrayValue() {
        elements = null;
    }

    public ArrayValue(int size) {
        this.elements = new Value[size];
    }

    public ArrayValue(Value[] elements) {
        this.elements = new Value[elements.length];
        System.arraycopy(elements, 0, this.elements, 0, elements.length);
    }

    public ArrayValue(List<Value> values) {
        final int size = values.size();
        this.elements = values.toArray(new Value[size]);
    }

    public ArrayValue(ArrayValue array) {
        this(array.elements);
    }

    public Value[] getCopyElements() {
        final Value[] result = new Value[elements.length];
        System.arraycopy(elements, 0, result, 0, elements.length);
        return result;
    }

    @Override
    public Type type() {
        return Type.ARRAY;
    }

    public int size() {
        return elements.length;
    }

    public Value get(int index) {
        return elements[index];
    }

    public void set(int index, Value value) {
        elements[index] = value;
    }

    @Override
    public Object raw() {
        return elements;
    }

    @Override
    public int asInt() {
        throw new TypeException("Cannot cast array to integer");
    }

    @Override
    public double asNumber() {
        throw new TypeException("Cannot cast array to number");
    }

    @Override
    public String asString() {
        return elements == null ? "{}" : "{ " + Utils.toString(elements) + " }";
    }

    @Override
    public Iterator<Value> iterator() {
        return Arrays.asList(elements).iterator();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Arrays.deepHashCode(this.elements);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass())
            return false;
        final ArrayValue other = (ArrayValue) obj;
        return Arrays.deepEquals(this.elements, other.elements);
    }

    @Override
    public int compareTo(Value o) {
        if (o.type() == Type.ARRAY) {
            final int lengthCompare = Integer.compare(size(), ((ArrayValue) o).size());
            if (lengthCompare != 0) return lengthCompare;
        }
        return asString().compareTo(o.asString());
    }

    @Override
    public String toString() {
        return asString();
    }
}
