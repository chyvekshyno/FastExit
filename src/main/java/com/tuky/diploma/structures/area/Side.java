package com.tuky.diploma.structures.area;

public class Side {
    protected final IntCoord coord1;
    protected final IntCoord coord2;

    public Side(IntCoord coord1, IntCoord coord2) {
        this.coord1 = coord1;
        this.coord2 = coord2;
    }

    public IntCoord getCoord1() {
        return coord1;
    }

    public IntCoord getCoord2() {
        return coord2;
    }
}
