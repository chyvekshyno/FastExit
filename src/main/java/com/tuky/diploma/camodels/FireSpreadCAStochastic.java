package com.tuky.diploma.camodels;

import com.tuky.diploma.structures.area.regularnet.RegularNet2D;
import com.tuky.diploma.structures.cellular.CellularAutomata;
import com.tuky.diploma.structures.graph.Moore2D;
import com.tuky.diploma.structures.graph.Transition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FireSpreadCAStochastic
        extends CellularAutomata<FireCellMoore2DStochastic> {

    private final double unitTime;

    public double getUnitTime() {
        return unitTime;
    }

    public FireSpreadCAStochastic(RegularNet2D<FireCellMoore2DStochastic> grid, double unitTime) {
        super(grid);
        this.unitTime = unitTime;
    }


    @Override
    public void nextState() {
        Map<FireCellMoore2DStochastic, Double> newStates = new HashMap<>();

        for (var cell: getGrid().getAdjTable().keySet())
            newStates.put(cell, nextCellState(cell));

        for (var cell: getGrid().getAdjTable().keySet()) {
            cell.next(newStates.get(cell));
        }

        nextGen();
    }

    @Override
    protected final double nextCellState(FireCellMoore2DStochastic cell) {

        switch (cell.getState()) {
            case FUEL -> {
                List<Double> probs = new ArrayList<>();

                Transition<FireCellMoore2DStochastic> tr;
                double value;
                for (int vec = 0; vec < Moore2D.NEIGHBOURS_COUNT; vec++) {
                    tr = getGrid().getAdjTable().get(cell).get(vec);
                    if (tr == null)
                        continue;

                    value = tr.getEnd().out(grid.getLen(), 0);
                    if (vec == 0 || vec == 2 || vec == 5 || vec ==7)
                        value /= 1.41;

                    probs.add(value);
                }

                return igniteProb(probs);
            }
            case FIRE -> {
                return cell.getValue() + getUnitTime();
            }
        }
        return 0.;
    }

    private double igniteProb(List<Double> probes) {
        return probes.stream().mapToDouble(Double::doubleValue).sum();
    }
}
