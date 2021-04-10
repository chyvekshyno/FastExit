package com.tuky.diploma.structures.area;

import java.util.List;
import java.util.stream.Collectors;

public class Zone {
    private List<Side> sides;
    private List<Exit> exits;

    public Zone() {
        this.sides = null;
        this.exits = null;
    }

    public Zone(List<Side> sides) {
        setShape(sides);
    }

    public void setShape(List<Side> sides) {
        this.sides = sides;
        this.exits = sides.stream()
                .filter(o -> o instanceof Exit)
                .map(o -> (Exit) o)
                .collect(Collectors.toList());
    }

    public List<Side> getShape() {
        return sides;
    }

    public List<Exit> getExits() {
        return exits;
    }
}
