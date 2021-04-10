package com.tuky.diploma.structures.area.regularnet;

import com.tuky.diploma.structures.addition.HSLS;
import com.tuky.diploma.structures.area.Area;
import com.tuky.diploma.structures.area.IntCoord;
import com.tuky.diploma.structures.area.Side;
import com.tuky.diploma.structures.area.Zone;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

/**
 * Create cell map from JSON file.
 * JSON-file describes building structure
 * Each object describes one floor (can't be repeated, may be as key)
 * Object has attributes:
 *      1.  floor
 *      2.  name    [optional]
 *      3.  door    (as the start point of room as wall array)
 *          wall s also an object:
 *              1.  length  (in cm/inch)
 *              2.  door    (distance from start of wall in cm/inch, width)    [optional]
 *                      is a new  room so is an array
 *                      if door is an empty array, it's the exit of the floor
 *              3.  vector  (of rotation from previous)     {l, r}
 *              4.  degree  [90 grad as default]
 *
 * Start build floor from input door
 */
public class CellMap {

    //region    FIELDS
    private List<List<Cell>> map;

    //endregion

    //region    CONSTRUCTORS

    public CellMap(IntCoord coord1, IntCoord coord2) {
        map = rectangle(coord1, coord2);
    }

    //endregion


    //region    METHODS
    public List<List<Cell>> getMap() {
        return map;
    }

    public Cell at (IntCoord coord) {
        return at(coord.X(), coord.Y());
    }

    public Cell at (int x, int y) {
        return map.get(y).get(x);
    }


    private List<List<Cell>> rectangle(IntCoord coord1, IntCoord coord2) {
        final int height = coord1.X() - coord2.X();
        final int width  = coord1.Y() - coord2.Y();

        Cell cell;
        List<List<Cell>> map = new ArrayList<>();
        List<Cell> cellLine = new ArrayList<>();

        try {
            //  first line
            cell = Cell.at(0, 0);
            cellLine.add(cell);
            for (int w = 1; w < width; w++) {
                cell = Cell.at(w, 0);
                cell.makeNeighbour(cellLine.get(w-1), Cell.VEC_LEFT);
                cellLine.add(cell);
            }
            map.add(cellLine);

            //  center lines
            for (int h = 1; h < height; h++) {
                cell = Cell.at(0, h);
                cellLine = new ArrayList<>();
                cellLine.add(cell);
                for (int w = 1; w < width; w++) {
                    cell = Cell.at(w, h);
                    cell.makeNeighbour(cellLine.get(w-1), Cell.VEC_LEFT);
                    cell.makeNeighbour(map.get(h-1).get(w-1), Cell.VEC_TOP_LEFT);
                    cell.makeNeighbour(map.get(h-1).get(w), Cell.VEC_TOP_LEFT);
                    cellLine.add(cell);
                }
                map.add(cellLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    private List<List<Cell>> outline(Zone zone) {
        List<List<HSLS>> HArray = HSLSArray(shape(zone.getShape()));

        return null;
    }

    private List<List<Cell>> shape(List<Side> zoneShape) {
        return zoneShape.stream()
                .map(side -> bresenhamLine(side.getCoord1(), side.getCoord2()))
                .collect(Collectors.toList());
    }

    private List<List<HSLS>> HSLSArray(List<List<Cell>> shape) {
        return shape.stream()
                .map(this::toHSLS)
                .collect(Collectors.toList());
    }

    private List<HSLS> toHSLS (List<Cell> side) {
        List<HSLS> res = new ArrayList<>();
        HSLS hsls;



        return null;
    }

    //region    BRESENHAM
    private List<Cell> bresenhamLine(IntCoord coord1, IntCoord coord2) {
        int x0 = coord1.X();
        int y0 = coord1.Y();
        int x1 = coord2.X();
        int y1 = coord2.Y();

        int dx = abs(x0 - x1);
        int dy = abs(y0 - y1);

        if (dy < dx) {
            if (x0 > x1)    return bresenhamLineLow(x1, y1, x0, y0);
            else            return bresenhamLineLow(x0, y0, x1, y1);
        } else {
            if (y0 > y1)    return bresenhamLineHigh(x1, y1, x0, y0);
            else            return bresenhamLineHigh(x0, y0, x1, y1);
        }
    }

    private List<Cell> bresenhamLineLow(int x0, int y0, int x1, int y1) {
        int dx = x1 - x0;
        int dy = y1 - y0;
        int y_step = 1;

        if (dy < 0) {
            y_step = -1;
            dy = -dy;
        }
        int D = 2 * dy;
        int error = D - dx;

        List<Cell> bresenham = new ArrayList<>();
        for (int x = x0, y = y0; x < x1; x++) {
            bresenham.add(at(x, y));
            error += D;
            if (error >= 0){
                y += y_step;
                error -= 2 * dx;
            }
        }
        return bresenham;
    }

    private List<Cell> bresenhamLineHigh(int x0, int y0, int x1, int y1) {
        int dx = x1 - x0;
        int dy = y1 - y0;
        int x_step = 1;

        if (dx < 0) {
            x_step = -1;
            dx = -dx;
        }
        int D = 2 * dx;
        int error = D - dy;

        List<Cell> bresenham = new ArrayList<>();
        for (int x = x0, y = y0; y < y1; y++) {
            bresenham.add(at(x, y));
            error += D;
            if (error >= 0){
                x += x_step;
                error -= 2 * dy;
            }
        }
        return bresenham;
    }
    //endregion
    //endregion


    //region    STATIC METHODS
    public static CellMap of (IntCoord coord1, IntCoord coord2) {
        return new CellMap(coord1, coord2);
    }

    public static CellMap of (Zone zone) {


        return null;
    }

    public static CellMap of (Area area) {
        return null;
    }
    //endregion

}