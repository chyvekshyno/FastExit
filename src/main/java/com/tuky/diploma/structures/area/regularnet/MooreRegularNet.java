package com.tuky.diploma.structures.area.regularnet;

import com.tuky.diploma.structures.area.IntCoord;
import com.tuky.diploma.structures.area.Zone;
import com.tuky.diploma.structures.graph.Node2D;
import com.tuky.diploma.structures.graph.NodeMoore2D;

import java.util.ArrayList;
import java.util.List;

public class MooreRegularNet<V extends Comparable<V>> extends RegularNet<NodeMoore2D<V, Integer>>{

    public MooreRegularNet() {
    }

    public MooreRegularNet(int x0, int y0, int x1, int y1) {
        super(x0, y0, x1, y1);
    }

    public MooreRegularNet(IntCoord coord1, IntCoord coord2) {
        super(coord1, coord2);
    }

    public MooreRegularNet(Zone zone) {
        super(zone);
    }

    @Override
    protected List<NodeMoore2D<V, Integer>> cellLineFirst
            (int y, int x0, int x1, List<List<NodeMoore2D<V, Integer>>> map) {
        NodeMoore2D<V, Integer> cell = NodeMoore2D.at(x0, y);
        List<NodeMoore2D<V, Integer>> cellLine = new ArrayList<>();
        cellLine.add(cell);
        try {
            for (int w = x0 + 1; w < x1 + 1; w++) {
                cell = NodeMoore2D.at(w, y);
                cellLine.add(cell);
                addTransition(cell, cellLine.get(w - x0 - 1), NodeMoore2D.VEC_LEFT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cellLine;
    }

    @Override
    protected List<NodeMoore2D<V, Integer>> cellLine
            (int y, int x0, int x1, List<List<NodeMoore2D<V, Integer>>> map) {
        NodeMoore2D<V, Integer> cell = NodeMoore2D.at(x0, y);
        List<NodeMoore2D<V, Integer>> cellLine = new ArrayList<>();
        cellLine.add(cell);
        try {
            for (int w = x0 + 1; w < x1; w++) {
                cell = NodeMoore2D.at(w, y);
                cellLine.add(cell);
                addTransition(cell, cellLine.get(w-x0-1)      , NodeMoore2D.VEC_LEFT);
                addTransition(cell, getAtRect(w-1, y-1, map)  , NodeMoore2D.VEC_TOP_LEFT);
                addTransition(cell, getAtRect(w  , y-1, map)  , NodeMoore2D.VEC_TOP);
                addTransition(cell, getAtRect(w+1, y-1, map)  , NodeMoore2D.VEC_TOP_RIGHT);
            }
            cell = NodeMoore2D.at(x1, y);
            cellLine.add(cell);
            addTransition(cell, cellLine.get(x1-x0-1)      , NodeMoore2D.VEC_LEFT);
            addTransition(cell, getAtRect(x1-1, y-1, map)  , NodeMoore2D.VEC_TOP_LEFT);
            addTransition(cell, getAtRect(x1  , y-1, map)  , NodeMoore2D.VEC_TOP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cellLine;
    }
}
