package com.tuky.diploma.structures.area;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Zone {
    private List<Polygon> polygons;
    private List<Exit> exits;

    public Zone() {
        this.polygons = null;
        this.exits = null;
    }

    public Zone(List<Polygon> polygons) {
        setShape(polygons);
    }

    public void setShape(List<Polygon> polygons) {
        this.polygons = polygons;
        this.exits = polygons.stream()
                .filter(o -> o instanceof Exit)
                .map(o -> (Exit) o)
                .collect(Collectors.toList());
    }

    public List<Polygon> getShape() {
        return polygons;
    }

    public List<Exit> getExits() {
        return exits;
    }
}
