package com.tuky.diploma.structures.area.regularnet;

import com.tuky.diploma.structures.addition.HSLS;
import com.tuky.diploma.structures.addition.Pair;
import com.tuky.diploma.structures.area.Area;
import com.tuky.diploma.structures.area.IntCoord;
import com.tuky.diploma.structures.area.Side;
import com.tuky.diploma.structures.area.Zone;

import java.util.*;
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
    private final int MIN_X = 0;
    private final int MIN_Y = 0;
    private List<List<Cell>> map;

    //endregion

    //region    CONSTRUCTORS

    public CellMap(IntCoord coord1, IntCoord coord2) {
        map = cellRectangle(coord1, coord2);
    }

    //endregion


    //region    METHODS
    //region    THIS MAP GETTERS
    public List<List<Cell>> getMap() {
        return map;
    }

    public Cell get(IntCoord coord) {
        return get(coord.X(), coord.Y());
    }

    public Cell get(int x, int y) {
        return map.get(y - MIN_Y).get(x - MIN_X);
    }

    public List<List<Cell>> get(IntCoord coord1, IntCoord coord2) {
        return get(coord1, coord2, this.map);
    }

    public List<List<Cell>> get(int x0, int y0, int x1, int y1) {
        return get(x0, y0, x1, y1, this.map);
    }

    public List<Cell> getLine(int y) {
        return getLine(y, this.map);
    }

    public List<Cell> getRow(int y, int x0, int x1) {
        return getRow(y, x0, x1, this.map);
    }

    public List<Cell> getBetween(Pair<HSLS> pair){
        return getBetween(pair, this.map);
    }
    //endregion

    //region GETTERS
    private Cell get(int x, int y, List<List<Cell>> cellRect) {
        return cellRect.get(y - MIN_Y).get(x - MIN_X);
    }

    private Cell get(IntCoord coord, List<List<Cell>> cellRect) {
        return get(coord.X(), coord.Y(), cellRect);
    }

    private List<List<Cell>> get(IntCoord coord1, IntCoord coord2, List<List<Cell>> cellRect) {
        return get(coord1.X(), coord1.Y(), coord2.X(), coord2.Y(), cellRect);
    }

    private List<List<Cell>> get(int x0, int y0, int x1, int y1, List<List<Cell>> cellRect) {
        List<List<Cell>> region = new ArrayList<>();
        for (int y = y0; y < y1; y++)
            region.add(getRow(y, x0, x1, cellRect));

        return region;
    }

    private List<Cell> getLine(int y, List<List<Cell>> cellRect) {
        return cellRect.get(y - MIN_Y);
    }

    private List<Cell> getRow(int y, int x0, int x1, List<List<Cell>> cellRect) {
        return getLine(y, cellRect).subList(x0, x1);
    }

    private List<Cell> getBetween(Pair<HSLS> pair, List<List<Cell>> cellRect){
        return getRow(pair.X().y(), pair.X().xL(), pair.Y().xR(), cellRect);
    }
    //endregion

    //region    RECTANGLE
    private List<List<Cell>> cellRectangle(int x0, int y0, int x1, int y1) {
        Cell cell;
        List<List<Cell>> map = new ArrayList<>();
        List<Cell> cellLine = new ArrayList<>();

        try {
            //  first line
            cell = Cell.at(x0, y0);
            cellLine.add(cell);
            for (int w = x0 + 1; w < x1; w++) {
                cell = Cell.at(w, y0);
                cell.makeNeighbour(cellLine.get(w-x0-1), Cell.VEC_LEFT);
                cellLine.add(cell);
            }
            map.add(cellLine);

            //  center lines
            for (int h = y0 + 1; h < y1; h++) {
                cell = Cell.at(x0, h);
                cellLine = new ArrayList<>();
                cellLine.add(cell);
                for (int w = x0 + 1; w < x1; w++) {
                    cell = Cell.at(w, h);
                    cell.makeNeighbour(cellLine.get(w-x0-1), Cell.VEC_LEFT);
                    cell.makeNeighbour(get(w-1, h-1), Cell.VEC_TOP_LEFT);
                    cell.makeNeighbour(get(w, h-1), Cell.VEC_TOP);
                    cellLine.add(cell);
                }
                map.add(cellLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private List<List<Cell>> cellRectangle(IntCoord coord1, IntCoord coord2) {
        return cellRectangle(coord1.X(), coord1.Y(), coord2.X(), coord2.Y());
    }

    private List<List<Cell>> cellRectangle(Zone zone) {
        return cellRectangle(zone.MIN_X(), zone.MIN_Y(), zone.MAX_X(), zone.MAX_Y());
    }
    //endregion

    private List<List<Cell>> figure(Zone zone) {
        var ET = EdgeTable(null);
        var cellRect = cellRectangle(zone);


        List<List<Cell>> cellMap = new ArrayList<>();
        List<Cell> cellLine = new ArrayList<>();
        List<HSLS> bucket;
        HSLS hsls0, hsls1;
        Iterator<HSLS> it;
        for (int y = 0; y < ET.size(); y++) {
            bucket = ET.get(y);
            it = bucket.iterator();
            while (it.hasNext()){
                hsls0 = it.next();
                hsls1 = it.next();
                cellLine.addAll(getRow(hsls0.y(), hsls0.xL(), hsls1.xR(), cellRect));
            }
            cellMap.set(y, cellLine);
            cellLine.clear();
        }

        return cellMap;
    }

    private List<List<HSLS>> EdgeTable (List<HSLS> HPolygon) {
        HSLS curr = HPolygon.remove(0);
        HSLS prev = curr;
        HSLS next;

        List<List<HSLS>> ET = new ArrayList<>();
//        ET.add(curr)
        for (HSLS hel : HPolygon) {

        }

        return ET;
    }

    private List<List<HSLS>> groupHPolygon(List<HSLS> HArray, int y_min, int y_max) {
        List<List<HSLS>> groups = new ArrayList<>(Collections
                .nCopies(y_max - y_min + 1, new ArrayList<>()));

        for (HSLS hsls : HArray)
            groups.get(hsls.y()).add(hsls);

        return groups;
    }

    private List<HSLS> toHPolygon(List<Side> zoneShape) {
        return zoneShape.stream()
                .flatMap(side -> toHLine(side.getCoord1(), side.getCoord2()).stream())
                .collect(Collectors.toList());
    }

    private List<HSLS> toHLine(IntCoord coord1, IntCoord coord2) {
        int x0 = coord1.X();
        int y0 = coord1.Y();
        int x1 = coord2.X();
        int y1 = coord2.Y();

        if (x0 < x1) {
            if (y0 < y1)    return toHLine(x0, y0, x1, y1, 1);
            else            return toHLine(x0, y0, x1, y1, -1);
        } else {
            if (y0 < y1)    return toHLine(x1, y1, x0, y0, -1);
            else            return toHLine(x1, y1, x0, y0, 1);
        }
    }


    /**
     * uses Bresenham algorithm
     */
    private List<HSLS> toHLine(int x0, int y0, int x1, int y1, int y_step) {
        int dx = x1 - x0;
        int D = 2 * (y1 - y0) * y_step;
        int error = D - dx;

        List<HSLS> HSLSArr = new ArrayList<>();
        for (int xR = x0, xL = x0, y = y0; xR < x1; xR++) {
            error += D;
            if (error >= 0){
                HSLSArr.add(new HSLS(y, xL, x0));
                error -= 2 * dx;

                y += y_step;
                xL = xR + 1;
            }
        }
        return HSLSArr;
    }

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
