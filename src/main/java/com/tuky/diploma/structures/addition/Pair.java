package com.tuky.diploma.structures.addition;

public class Pair<T> {
    private final T x;
    private final T y;

    public Pair(T a, T b) {
        this.x = a;
        this.y = b;
    }

    public T X() {
        return x;
    }

    public T Y() {
        return y;
    }
}
