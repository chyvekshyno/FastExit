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
public class UnitCellGrid {

    //region    FIELDS
    private final int MIN_X = 0;
    private final int MIN_Y = 0;
    private List<List<UnitCell>> grid;

    //endregion

    //region    CONSTRUCTORS

    public UnitCellGrid(IntCoord coord1, IntCoord coord2) {
        grid = cellRectangle(coord1, coord2);
    }

    public UnitCellGrid(Zone zone) {
        grid = figure(zone);
    }

    //endregion


    //region    METHODS

    private int getIndX(int x) { return x - MIN_X; }
    private int getIndY(int y) { return y - MIN_Y; }

    //region    THIS MAP GETTERS
    public List<List<UnitCell>> getGrid() {
        return grid;
    }

    public UnitCell get(IntCoord coord) {
        return get(coord.X(), coord.Y());
    }

    public UnitCell get(int x, int y) {
        return grid.get(y - MIN_Y).get(x - MIN_X);
    }

    public List<List<UnitCell>> get(IntCoord coord1, IntCoord coord2) {
        return get(coord1, coord2, this.grid);
    }

    public List<List<UnitCell>> get(int x0, int y0, int x1, int y1) {
        return get(x0, y0, x1, y1, this.grid);
    }

    public List<UnitCell> getLine(int y) {
        return getLine(y, this.grid);
    }

    public List<UnitCell> getRow(int y, int x0, int x1) {
        return getRow(y, x0, x1, this.grid);
    }

    public List<UnitCell> getBetween(Pair<HSLS> pair){
        return getBetween(pair, this.grid);
    }
    //endregion

    //region GETTERS
    private UnitCell get(int x, int y, List<List<UnitCell>> cellRect) {
        return cellRect.get(getIndY(y)).get(getIndX(x));
    }

    private UnitCell get(IntCoord coord, List<List<UnitCell>> cellRect) {
        return get(coord.X(), coord.Y(), cellRect);
    }

    private List<List<UnitCell>> get(IntCoord coord1, IntCoord coord2, List<List<UnitCell>> cellRect) {
        return get(coord1.X(), coord1.Y(), coord2.X(), coord2.Y(), cellRect);
    }

    private List<List<UnitCell>> get(int x0, int y0, int x1, int y1, List<List<UnitCell>> cellRect) {
        List<List<UnitCell>> region = new ArrayList<>();
        for (int y = y0; y < y1; y++)
            region.add(getRow(y, x0, x1, cellRect));

        return region;
    }

    private List<UnitCell> getLine(int y, List<List<UnitCell>> cellRect) {
        return cellRect.get(getIndY(y));
    }

    private List<UnitCell> getRow(int y, int x0, int x1, List<List<UnitCell>> cellRect) {
        return getLine(y, cellRect).subList(x0, x1);
    }

    private List<UnitCell> getBetween(Pair<HSLS> pair, List<List<UnitCell>> cellRect){
        return getRow(pair.X().y(), pair.X().xL(), pair.Y().xR(), cellRect);
    }
    //endregion

    //region    RECTANGLE
    private List<List<UnitCell>> cellRectangle(int x0, int y0, int x1, int y1) {
        UnitCell cell;
        List<List<UnitCell>> map = new ArrayList<>();
        List<UnitCell> cellLine = new ArrayList<>();

        try {
            //  first line
            cell = UnitCell.at(x0, y0);
            cellLine.add(cell);
            for (int w = x0 + 1; w < x1; w++) {
                cell = UnitCell.at(w, y0);
                cell.meetNeighbour(cellLine.get(w-x0-1), UnitCell.VEC_LEFT);
                cellLine.add(cell);
            }
            map.add(cellLine);

            //  center lines
            for (int h = y0 + 1; h < y1; h++) {
                cell = UnitCell.at(x0, h);
                cellLine = new ArrayList<>();
                cellLine.add(cell);
                for (int w = x0 + 1; w < x1; w++) {
                    cell = UnitCell.at(w, h);
                    cell.meetNeighbour(cellLine.get(w-x0-1), UnitCell.VEC_LEFT);
                    cell.meetNeighbour(get(w-1, h-1), UnitCell.VEC_TOP_LEFT);
                    cell.meetNeighbour(get(w, h-1), UnitCell.VEC_TOP);
                    cellLine.add(cell);
                }
                map.add(cellLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private List<List<UnitCell>> cellRectangle(IntCoord coord1, IntCoord coord2) {
        return cellRectangle(coord1.X(), coord1.Y(), coord2.X(), coord2.Y());
    }

    private List<List<UnitCell>> cellRectangle(Zone zone) {
        return cellRectangle(zone.MIN_X(), zone.MIN_Y(), zone.MAX_X(), zone.MAX_Y());
    }
    //endregion

    private List<List<UnitCell>> figure(Zone zone) {
        var ET = EdgeTable(zone);
        var cellRect = cellRectangle(zone);


        List<List<UnitCell>> cellMap = new ArrayList<>();
        List<UnitCell> cellLine = new ArrayList<>();
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

            //  remove neighbourhoods of cells NOT included to Zone
            cellRect.get(y).forEach(cell -> {
                if (!cellLine.contains(cell))
                    cell.leaveAlone();
            });

            cellMap.set(y, cellLine);
            cellLine.clear();
        }

        return cellMap;
    }

    private List<List<HSLS>> EdgeTable (Zone zone) {
        return EdgeTable(toHPolygon(zone.getShape()), zone.MAX_Y() - zone.MIN_Y() + 1);
    }

    private List<List<HSLS>> EdgeTable (List<HSLS> HPolygon, int height) {
        HSLS prev = HPolygon.remove(0);
        HSLS curr = HPolygon.remove(0);
        HSLS next;

        List<List<HSLS>> ET = new ArrayList<>(Collections.nCopies(height, new ArrayList<>()));
        ET.get(getIndY(prev.y())).add(prev);
        ET.get(getIndY(curr.y())).add(curr);
        for (HSLS hel : HPolygon) {
            next = hel;
            if     ((prev.y() > curr.y() && next.y() < curr.y()) ||
                    (prev.y() < curr.y() && next.y() > curr.y())) {
                ET.get(getIndY(curr.y())).add(curr);
            } else if  ((prev.y() > curr.y() && next.y() > curr.y()) ||
                        (prev.y() < curr.y() && next.y() < curr.y())) {
                ET.get(getIndY(curr.y())).add(curr);
                ET.get(getIndY(curr.y())).add(curr);
            }
            prev = curr;
            curr = next;
        }

        ET.forEach(list -> list.sort(Comparator.comparing(HSLS::xL)));
        return ET;
    }

//    private List<List<HSLS>> groupHPolygon(List<HSLS> HArray, int y_min, int y_max) {
//        List<List<HSLS>> groups = new ArrayList<>(Collections
//                .nCopies(y_max - y_min + 1, new ArrayList<>()));
//
//        for (HSLS hsls : HArray)
//            groups.get(hsls.y()).add(hsls);
//
//        return groups;
//    }

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
    public static UnitCellGrid of (IntCoord coord1, IntCoord coord2) {
        return new UnitCellGrid(coord1, coord2);
    }

    public static UnitCellGrid of (Zone zone) {
        return new UnitCellGrid(zone);
    }

    public static UnitCellGrid of (Area area) {
        return null;
    }
    //endregion
}
