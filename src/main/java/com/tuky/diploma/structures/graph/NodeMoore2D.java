package com.tuky.diploma.structures.graph;

import com.tuky.diploma.structures.area.Coord;

/**
 * class describes cell unit for regular net
 * neighbour indexes:
 *      [0  1   2]
 *      [3  x   4]
 *      [5  6   7]
 */
public class NodeMoore2D
        <V extends Comparable<V>, T extends Number>
        extends Node2D<V, T>
        implements Moore2D  {

    public NodeMoore2D(Coord<T> coord) {
        super(coord);
    }
    //endregion

    public static <V extends Comparable<V>
            , T extends Number> NodeMoore2D<V, T> at (Coord<T> coord) {
        return new NodeMoore2D<>(coord);
    }

    public static <V extends Comparable<V>
            , T extends Number> NodeMoore2D<V, T> at (T x, T y) {
        return new NodeMoore2D<>(Coord.at(x, y));
    }

    @Override
    public int vecLeft(int vec) {
        return 0;
    }

    @Override
    public int vecRight(int vec) {
        return 0;
    }

}
