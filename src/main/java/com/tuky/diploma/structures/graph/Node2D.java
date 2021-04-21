package com.tuky.diploma.structures.graph;

import com.tuky.diploma.structures.area.Coord;
import com.tuky.diploma.structures.area.IntCoord;

public class Node2D<V extends Comparable<V>, T extends Number> extends Node<V>{
    protected final Coord<T> coord;

    public Node2D(Coord<T> coord) {
        super();
        this.coord = coord;
    }

    public Node2D(V value, Coord<T> coord) {
        super(value);
        this.coord = coord;
    }

    public boolean sameCoord(Node2D<V, T> another) {
        return sameX(another) && sameY(another);
    }

    public boolean sameX(Node2D<V, T> another) {
        return another.getCoord().X().equals(coord.X());
    }

    public boolean sameY(Node2D<V, T> another) {
        return another.getCoord().Y().equals(coord.Y());
    }


    public static <V extends Comparable<V>
            , T extends Number> Node2D<V, T> at (Coord<T> coord) {
        return new Node2D<>(coord);
    }

    public static <V extends Comparable<V>
            , T extends Number> Node2D<V, T> at (T x, T y) {
        return new Node2D<>(Coord.at(x, y));
    }

    public Coord<T> getCoord() {
        return coord;
    }
}

