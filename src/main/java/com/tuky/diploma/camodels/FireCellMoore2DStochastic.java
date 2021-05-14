package com.tuky.diploma.camodels;

import com.tuky.diploma.structures.addition.Risk;
import com.tuky.diploma.structures.area.Coord;
import com.tuky.diploma.structures.cellular.CellularAutomaton;
import com.tuky.diploma.structures.graph.NodeMoore2D;

public class FireCellMoore2DStochastic
        extends NodeMoore2D<Double, Integer>
        implements CellularAutomaton, Risk {

    private FireCellState state;

    private final double KOEF_IGNITE;
    private final double TIME_BURN;


    @Override
    public double getMaxDamage() {
        return getState().damage();
    }

    public enum FireCellState{
        FUEL    {
            @Override
            public double damage() {  return 0;  }
        },
        NonFUEL {
            @Override
            public double damage() {  return 0;  }
        },
        BURNED {
            @Override
            public double damage() {  return 10;  }
        },
        FIRE {
            @Override
            public double damage() {  return 100;  }
        };

        abstract public double damage();

    }
    public FireCellState getState() {
        return state;
    }
    public FireCellMoore2DStochastic(Coord<Integer> coord) {
        this(coord, 0.2, 100000, FireCellState.FUEL);
    }

    public FireCellMoore2DStochastic(Coord<Integer> coord, double koef_ignite, double burnTime, FireCellState state) {
        super(coord);
        this.state = state;
        this.KOEF_IGNITE = koef_ignite;
        this.TIME_BURN = burnTime;
        setValue(0.);
    }

    public double KOEF_IGNITE() {
        return KOEF_IGNITE;
    }

    public boolean next(double value) {
        if (state == FireCellState.FUEL) {
            return fuel_fire(value);
        }
        else if (state == FireCellState.FIRE)
            fire_burn(value);
        return false;
    }

    private boolean fuel_fire(double value) {
        if (Math.random() < value)
            return setFire();
        return false;
    }

    public boolean setFire() {
        state = FireCellState.FIRE;
        setValue(1.);
        return true;
    }

    private void fire_burn(double time) {
        if (time > TIME_BURN) {
            state = FireCellState.BURNED;
            setValue(1.);
        } else setValue(time);
    }

    public double out(double len, int vec) {
        return KOEF_IGNITE * (getValue() / 1000 / len) * natureAction(vec);
    }

    public double natureAction(int vec) {
        return 1.;
    }

    public static FireCellMoore2DStochastic at
            (Coord<Integer> coord, double koef_ignite, double burnTime, FireCellState state) {
        return new FireCellMoore2DStochastic(coord, koef_ignite, burnTime, state);
    }

    public static FireCellMoore2DStochastic at (int x, int y) {
        return FireCellMoore2DStochastic.at(Coord.at(x, y), 0.2, 100000, FireCellState.FUEL);
    }
}
