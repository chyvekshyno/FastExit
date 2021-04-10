package com.tuky.diploma.structures.area;

public class Polygon {
    protected final Coord coord1;
    protected final Coord coord2;

    public Polygon(Coord coord1, Coord coord2) {
        this.coord1 = coord1;
        this.coord2 = coord2;
    }

    public Coord getCoord1() {
        return coord1;
    }

    public Coord getCoord2() {
        return coord2;
    }
}