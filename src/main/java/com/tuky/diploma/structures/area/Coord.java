package com.tuky.diploma.structures.area;

import com.tuky.diploma.structures.addition.Pair;

public class Coord <T extends Number> extends Pair<T> {

    public Coord(T a, T b) {
        super(a, b);
    }

    public static <T extends Number> Coord<T> at(T x, T y) {
        return new Coord<>(x, y);
    }
}
