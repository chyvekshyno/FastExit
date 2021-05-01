package com.tuky.diploma.structures.area;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class Zone {

    private List<Side> sides;
    private List<Exit> exits;

    private int MAX_X;
    private int MIN_X;
    private int MAX_Y;
    private int MIN_Y;

    private final double len;

    public Zone(double len) {
        this.len = len;
        this.sides = null;
        this.exits = null;
    }

    public Zone(List<Side> sides, double len) {
        this.len = len;
        setShape(sides);
    }

    public void setShape(List<Side> sides) {
        this.sides = sides;
        this.exits = sides.stream()
                .filter(o -> o instanceof Exit)
                .map(o -> (Exit) o)
                .collect(Collectors.toList());
        update();
    }

    public int MAX_X (){
        return MAX_X;
    }

    public int MIN_X () {
        return MIN_X;
    }
    public int MAX_Y () {
        return MAX_Y;
    }
    public int MIN_Y () {
        return MIN_Y;
    }

    public List<Side> getShape() {
        return sides;
    }

    public List<Exit> getExits() {
        return exits;
    }

    private void update() {
        MAX_X = updateMAX_X();
        MAX_Y = updateMAX_Y();
        MIN_X = updateMIN_X();
        MIN_Y = updateMIN_Y();
    }

    private int updateMAX_X () {
        return sides.stream()
                .mapToInt(Side::MAX_X).max()
                .orElseThrow(NoSuchElementException::new);
    }

    private int updateMIN_X () {
        return sides.stream()
                .mapToInt(Side::MIN_X).min()
                .orElseThrow(NoSuchElementException::new);
    }

    private int updateMAX_Y () {
        return sides.stream()
                .mapToInt(Side::MAX_Y).max()
                .orElseThrow(NoSuchElementException::new);
    }

    private int updateMIN_Y () {
        return sides.stream()
                .mapToInt(Side::MIN_Y).min()
                .orElseThrow(NoSuchElementException::new);
    }

    public double getLen() {
        return len;
    }
}
