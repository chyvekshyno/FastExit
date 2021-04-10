package com.tuky.diploma.structures.area;

import com.tuky.diploma.structures.addition.Pair;

public class DCoord extends Coord<Double> {

    public DCoord(double x, double y) {
        super(x, y);
    }

    public static DCoord at(double x, double y) {
        return new DCoord(x, y);
    }
}
