package com.tuky.diploma.camodels;

import com.tuky.diploma.structures.area.Coord;
import com.tuky.diploma.structures.cellular.CellularAutomaton;
import com.tuky.diploma.structures.graph.NodeMoore2D;

public class FireCellMoore2DHeat
        extends NodeMoore2D<Double, Integer>
        implements CellularAutomaton {

    private FireCellState state;

    private final double KOEF_HEAT;
    private final double TEMP_IGNITE;
    private final double TIME_BURN;


    public enum FireCellState{
        FUEL    ,
        NonFUEL ,
        BURNED  ,
        FIRE;
    }
    public FireCellState getState() {
        return state;
    }

    public FireCellMoore2DHeat(Coord<Integer> coord) {
        this(coord, 250, 20, 0.42, FireCellState.FUEL);
    }

    public FireCellMoore2DHeat(Coord<Integer> coord, double igniteKoef, double burnTime, double koef_heat, FireCellState state) {
        super(coord);
        this.TEMP_IGNITE = igniteKoef;
        this.state = state;
        this.TIME_BURN = burnTime;
        KOEF_HEAT = koef_heat;
        setValue(0.);
    }

    public double getKOEF_HEAT() {
        return KOEF_HEAT;
    }

    public double getTEMP_IGNITE() {
        return TEMP_IGNITE;
    }

    public double out() {
        switch (state) {
            case FUEL -> {
            }
            case NonFUEL -> {
            }
            case BURNED -> {
            }
            case FIRE -> {
                return TEMP_IGNITE;
            }
        }

        return 0.;
    }

    public void next(double value) {
        if (state == FireCellState.FUEL) {
            fuel_fire(value);
        }
        else if (state == FireCellState.FIRE)
            fire_burn(value);
    }

    private void fuel_fire(double value) {
        if (value > TEMP_IGNITE)
            setFire();
    }

    private void fire_burn(double time) {
        if (time > TIME_BURN) {
            state = FireCellState.BURNED;
            setValue(0.);
        } else setValue(time);
    }


    public static FireCellMoore2DHeat at
            (Coord<Integer> coord, double ignTemp, double burnTime, double heatKoef, FireCellState state) {
        return new FireCellMoore2DHeat(coord, ignTemp, burnTime, heatKoef, state);
    }

    public static FireCellMoore2DHeat at (int x, int y) {
        return FireCellMoore2DHeat.at(Coord.at(x, y), 250, 3, 0.12, FireCellState.FUEL);
    }

    public void setFire() {
        state = FireCellState.FIRE;
        setValue(0.);
    }
}
