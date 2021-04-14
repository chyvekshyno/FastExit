package com.tuky.diploma.structures.area.regularnet;

import com.tuky.diploma.structures.area.IntCoord;
import com.tuky.diploma.structures.graph.Node;

import java.util.*;

/**
 * class describes cell unit for regular net
 * neighbour indexes:
 *      [0  1   2]
 *      [3  x   4]
 *      [5  6   7]
 */
public class UnitCellNode extends Node {

    //region    VECTORS
    public static final int NEIGHBOURS_COUNT  = 7;

    public static final int VEC_TOP_LEFT       = 0;
    public static final int VEC_TOP            = 1;
    public static final int VEC_TOP_RIGHT      = 2;
    public static final int VEC_LEFT           = 3;
    public static final int VEC_RIGHT          = 4;
    public static final int VEC_BOTTOM_LEFT    = 5;
    public static final int VEC_BOTTOM         = 6;
    public static final int VEC_BOTTOM_RIGHT   = 7;

    private static final UnitCellNode NULL_CELL = UnitCellNode.at(IntCoord.NULL_COORD);
    //endregion

    //region    Fields
    private final IntCoord coord;
//    private List<UnitCellNode> neighbours;
    //endregion

    //region    Constructors

    public UnitCellNode(IntCoord coord) {
        this.coord = coord;
//        neighbours = new ArrayList<>(Collections.nCopies(7, NULL_CELL));
    }

    //endregion

    //region    Methods

    public static int vecReverse(int vec) {
        return NEIGHBOURS_COUNT - vec;
    }

    //region    Getter n Setter
//    public IntCoord getCoord() {
//        return coord;
//    }
//
//    public List<UnitCellNode> getNeighbours() {
//        return neighbours;
//    }
//    //endregion
//
//    public void meetNeighbour(UnitCellNode another, int vec) throws Exception {
//        meetNeighbour(another, vec, true);
//    }
//
//    public void leaveAlone () {
//        for (int i = 0; i < NEIGHBOURS_COUNT; i++) {
//            leaveNeighbour(i, true);
//        }
//    }
//
//    private void leaveNeighbour(int vec, boolean flag_first) {
//        if (neighbours.get(vec) != NULL_CELL) {
//            if (flag_first)
//                neighbours.get(vec).leaveNeighbour(NEIGHBOURS_COUNT - vec, false);
//            neighbours.set(vec, NULL_CELL);
//        }
//    }
//
//    private void meetNeighbour(UnitCellNode another, int vec, boolean flag_first) throws Exception {
//        if (neighbours.get(vec) != NULL_CELL)
//            throw new Exception("Cell already has neighbour");
//
//        neighbours.add(vec, another);
//        if (flag_first){
//            another.meetNeighbour(this, NEIGHBOURS_COUNT-vec, false);
//        }
//    }

    //region    STATIC
    public static UnitCellNode at (IntCoord coord) {
        return new UnitCellNode(coord);
    }

    public static UnitCellNode at (int x, int y) {
        return new UnitCellNode(IntCoord.at(x, y));
    }
    //endregion
    //endregion
}
