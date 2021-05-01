package com.tuky.diploma.structures.cellular;

import com.tuky.diploma.structures.area.regularnet.RegularNet2D;
import com.tuky.diploma.structures.graph.Node2D;

import java.util.HashMap;
import java.util.Map;

public abstract class CellularAutomata
        <N extends Node2D<Double, Integer> & CellularAutomaton> {

    private int generation;

    protected final RegularNet2D<N> grid;

    public CellularAutomata(RegularNet2D<N> grid) {
        this.generation = 0;
        this.grid = grid;
    }

    public RegularNet2D<N> getGrid() {
        return grid;
    }

    public final int getGeneration() {
        return generation;
    }

    public void nextState() {
        Map<N, Double> newStates = new HashMap<>();

        for (N cell: getGrid().getAdjTable().keySet())
            newStates.put(cell, nextCellState(cell));

        for (N cell: getGrid().getAdjTable().keySet()) {
            cell.setValue(newStates.get(cell));
            cell.next(cell.getValue());
        }

        nextGen();
    }

    protected abstract double nextCellState(N cell);

    protected final void nextGen() {
        generation++;
    }
}
