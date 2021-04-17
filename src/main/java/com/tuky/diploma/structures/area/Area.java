package com.tuky.diploma.structures.area;

import java.util.List;

public class Area {
    private List<Zone> zones;

    public Area(List<Zone> zones, List<List<Integer>> connections){
        this.zones = zones;
        connectAll(connections);
    }

    private void connectAll(List<List<Integer>> connections) {
        connections.forEach(this::connect);
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
}
