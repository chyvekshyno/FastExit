package com.tuky.diploma.structures.area;

import java.util.List;

public class Area {
    private final double len;
    private final List<Zone> zones;
    private final List<Coord<Integer>> exits;

    public Area(List<Zone> zones, List<Coord<Integer>> exits, double len){
        this.zones = zones;
        this.len = len;
        this.exits = exits;
    }

    public List<Coord<Integer>> getExits() {
        return exits;
    }

    private void connect(List<Integer> connection){
        try {
            zones.get(connection.get(0))
                    .getExits().get(connection.get(1))
                    .connect(zones.get(connection.get(2))
                                .getExits().get(connection.get(3)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Zone> getZones() {
        return zones;
    }

    public void build() {}

    public double getLen() {
        return len;
    }
}
