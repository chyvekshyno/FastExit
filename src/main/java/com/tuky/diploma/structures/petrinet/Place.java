package com.tuky.diploma.structures.petrinet;

import java.util.List;

public class Place {

    private List<Integer> tokens;


    public Place() {
    }

    public boolean isEmpty() {
        return tokens.isEmpty();
    }

    public boolean contains (int color) {
        return tokens.contains(color);
    }

    public void remove(int color) {
        tokens.remove((Integer) color);
    }

    public void append(int color) {
        tokens.add(color);
    }

}
