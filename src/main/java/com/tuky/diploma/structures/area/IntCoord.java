package com.tuky.diploma.structures.area;

public class IntCoord extends Coord<Integer> {

    public static IntCoord NULL_COORD = IntCoord.at(Integer.MIN_VALUE, Integer.MAX_VALUE);

    public IntCoord(int x, int y) {
        super(x, y);
    }

    public static IntCoord at(int x, int y) {
        return new IntCoord(x, y);
    }
}