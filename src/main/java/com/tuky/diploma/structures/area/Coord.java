package com.tuky.diploma.structures.area;

import com.tuky.diploma.structures.addition.Pair;

public class Coord extends Pair<Double> {

    public Coord(double x, double y) {
        super(x, y);
    }

    public static Coord getInstance(double x, double y) {
        return new Coord(x, y);
    }
}
