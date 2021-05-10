package com.tuky.diploma.camodels;

import com.tuky.diploma.structures.area.regularnet.RegularNet2D;
import com.tuky.diploma.structures.cellular.CellularAutomata;
import com.tuky.diploma.structures.graph.Moore2D;
import com.tuky.diploma.structures.graph.Transition;

import java.util.*;

public class FireSpreadCAHeat
        extends CellularAutomata<FireCellMoore2DHeat> {

    private final double unitTime;

    public double getUnitTime() {
        return unitTime;
    }

    public FireSpreadCAHeat(RegularNet2D<FireCellMoore2DHeat> grid, double unitTime) {
        super(grid);
        this.unitTime = unitTime;
    }


    private double fuelTransition(List<Double> probes) {
        return probes.stream().mapToDouble(Double::doubleValue).sum();
    }

    @Override
    protected final double nextCellState(FireCellMoore2DHeat cell) {

        switch (cell.getState()) {
            case FUEL -> {
                List<Double> heat = new ArrayList<>();

                Transition<FireCellMoore2DHeat> tr;
                double value;
                for (int vec = 0; vec < Moore2D.NEIGHBOURS_COUNT; vec++) {
                    tr = getGrid().getAdjTable().get(cell).get(vec);
                    if (tr == null)
                        continue;

                    value = heat(cell, tr.getEnd());
                    if (vec == 0 || vec == 2 || vec == 5 || vec ==7)
                        value /= 1.41;

                    heat.add(value);
                }

                return cell.getValue() + fuelTransition(heat);
            }
            case FIRE -> {
                return cell.getValue() + getUnitTime();
            }
        }

        return 0.;
    }

    private double heat(FireCellMoore2DHeat cell, FireCellMoore2DHeat cellFire) {
        return  cell.getValue()
                + (getUnitTime() * cell.getKOEF_HEAT() * ((cellFire.out() - cell.getValue())
                / (getGrid().getLen() * getGrid().getLen())) ) ;
    }
}
