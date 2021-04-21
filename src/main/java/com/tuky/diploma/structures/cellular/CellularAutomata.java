package com.tuky.diploma.structures.cellular;

import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.structures.graph.Node;
import com.tuky.diploma.structures.graph.Node2D;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class CellularAutomata
        <V extends Number & Comparable<V>,  N extends Node2D<V, Integer>> {

    private int generation;

    public CellularAutomata() {
        this.generation = 0;
    }

    public int getGeneration() {
        return generation;
    }

    public void nextState() {
        Map<N, V> newStates = new HashMap<>();

        for (N cell: getCells())
            newStates.put(cell, nextCellState(cell));

        for (N cell: getCells())
            cell.setValue(newStates.get(cell));

        generation++;
    }

    protected abstract Collection<N> getCells();

    protected abstract V nextCellState(N cell);
}
