package com.tuky.diploma.structures.area;

import static java.lang.Math.max;
import static java.lang.Math.min;

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

    public int MAX_X () {
        return max(coord1.X(), coord2.X());
    }

    public int MIN_X () {
        return min(coord1.X(), coord2.X());
    }

    public int MAX_Y () {
        return max(coord1.Y(), coord2.Y());
    }

    public int MIN_Y () {
        return min(coord1.Y(), coord2.Y());
    }
}
