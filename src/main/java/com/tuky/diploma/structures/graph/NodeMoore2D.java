package com.tuky.diploma.structures.graph;

import com.tuky.diploma.structures.area.Coord;
import com.tuky.diploma.structures.area.IntCoord;

/**
 * class describes cell unit for regular net
 * neighbour indexes:
 *      [0  1   2]
 *      [3  x   4]
 *      [5  6   7]
 */
public class NodeMoore2D<V extends Comparable<V>, T extends Number> extends Node2D<V, T> {

    //region    VECTORS
    public static final int NEIGHBOURS_COUNT  = 8;

    public static final int VEC_TOP_LEFT       = 0;
    public static final int VEC_TOP            = 1;
    public static final int VEC_TOP_RIGHT      = 2;
    public static final int VEC_LEFT           = 3;
    public static final int VEC_RIGHT          = 4;
    public static final int VEC_BOTTOM_LEFT    = 5;
    public static final int VEC_BOTTOM         = 6;
    public static final int VEC_BOTTOM_RIGHT   = 7;

    public NodeMoore2D(Coord<T> coord) {
        super(coord);
    }
    //endregion

    public static int vecReverse(int vec) {
        return NEIGHBOURS_COUNT - vec - 1;
    }

    public static <V extends Comparable<V>
            , T extends Number> NodeMoore2D<V, T> at (Coord<T> coord) {
        return new NodeMoore2D<>(coord);
    }

    public static <V extends Comparable<V>
            , T extends Number> NodeMoore2D<V, T> at (T x, T y) {
        return new NodeMoore2D<>(Coord.at(x, y));
    }
}
