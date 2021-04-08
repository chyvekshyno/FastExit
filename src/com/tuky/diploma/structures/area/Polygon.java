package com.tuky.diploma.structures.area;

import java.util.List;

public class Polygon extends AreaObject {
    private final Coord coord;
    private final List<Coord> exits;

    public Polygon(Coord coord) {
        this(coord, null);
    }

    public Polygon(Coord coord, List<Coord> exit) {
        this.coord = coord;
        this.exits = exit;
    }

    public Coord getCoord() {
        return coord;
    }

    public List<Coord> getExits() {
        return exits;
    }
}
