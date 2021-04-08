package com.tuky.diploma.structures.area;

import java.util.ArrayList;
import java.util.List;

public class Zone extends AreaObject{
    private List<Polygon> polygons;
    private List<Exit> exits;

    public Zone() {
        polygons = null;
        exits = null;
    }

    public void setShape(List<Polygon> polygons) {
        this.polygons = polygons;
    }

    public void setExits(List<Exit> exits) {
        this.exits = exits;
    }

    public List<Polygon> getShape() {
        return polygons;
    }

    public List<Exit> getExits() {
        return exits;
    }
}
