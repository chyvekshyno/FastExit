package com.tuky;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * class describes cell unit for cellular automata
 */
public class Cell {

    //region    Fields
    private Map<Integer, Cell> neighbours;
    private boolean mark;

    private static final int[][] neighbour_ind = {
            {7,     0,      4},
            {3,    -1,      1},
            {6,     2,      5}
    };

    private static final int[] neighbour_left_ind = {
                4, 5, 6, 7, 1, 2, 3, 0
    };

    private static final int[] neighbour_right_ind = {
            7, 4, 5, 6, 0, 1, 2, 3
    };
    //endregion

    //region    Constructors
    public Cell() {
        neighbours = null;
        mark = false;
    }

    public Cell(Map<Integer, Cell> neighbours) {
        this.neighbours = neighbours;
    }

    //endregion

    //region    Methods
    //region    Getter n Setter
    public Map<Integer, Cell> getNeighbours() {
        return neighbours == null ? new HashMap<>() : neighbours;
    }
    //endregion

    public boolean hasMark() {
        return mark;
    }

    /**
     * create a new cell as neighbour for this
     * for vector (x, y)
     * @param x : int {-1, 0, 1}
     * @param y : int {-1, 0, 1}
     * @return Cell
     */
    public Cell cell(int x, int y) {
        //  check input
        if (x < -1 || x > 1 || y < -1 || y > 1 || (x == 0 && y == 0))
            throw new IllegalArgumentException("vector (x,y)=("
                    + String.join(String.valueOf(x), String.valueOf(y))
                    + ") is invalid;");

        //  need to define the vector of a new cell like pair (x,y) є {-1,0,1}
        int nvec = vecToNeighbour(x, y);
        int nvec_inv = vecToNeighbour(-x, -y);

        //  create a cell and sets int neighbours
        Map<Integer, Cell> cellNeighbours = new HashMap<>();
        cellNeighbours.put(nvec_inv, this);
        if (nvec % 2 == 1) {
            cellNeighbours.put(neighbour_right_ind[nvec_inv]
                    , neighbours.get(neighbour_left_ind[nvec]));
            cellNeighbours.put(neighbour_left_ind[nvec_inv]
                    , neighbours.get(neighbour_right_ind[nvec]));
        }

        Cell cell = new Cell(cellNeighbours);

        //  set created cell as neighbour
        this.setNeighbour(cell, x, y);


        return cell;
    }

    private int vecToNeighbour(int x, int y) {
        return neighbour_ind[x+1][y+1];
    }

    private void setNeighbour(Cell cell, int x, int y) {
        if (neighbours == null)
            neighbours = new HashMap<>();
        neighbours.putIfAbsent(vecToNeighbour(x, y), cell);
    }
    //endregion
}
