package com.tuky.diploma.structures.area.regularnet;

import com.tuky.diploma.structures.addition.HSLS;
import com.tuky.diploma.structures.area.Area;
import com.tuky.diploma.structures.area.IntCoord;
import com.tuky.diploma.structures.area.Side;
import com.tuky.diploma.structures.area.Zone;
import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.structures.graph.Node;
import com.tuky.diploma.structures.graph.Transition;

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
public class CellNodeGrid extends Graph {

    //region    FIELDS
    private int MIN_X;
    private int MIN_Y;
    private List<List<UnitCellNode>> grid;

    //endregion

    //region    CONSTRUCTORS


    public CellNodeGrid() {
        super();
    }

    public CellNodeGrid(int x0, int y0, int x1, int y1) {
        this();
        initMINS(x0, y0);
        grid = cellRectangle(x0, y0, x1, y1);
    }

    public CellNodeGrid(IntCoord coord1, IntCoord coord2) {
        this(coord1.X(), coord1.Y(), coord2.X(), coord2.Y());
    }

    public CellNodeGrid(Zone zone) {
        this();
        initMINS(zone.MIN_X(), zone.MIN_Y());
        grid = figure(zone);
    }

    //endregion


    //region    METHODS

    @Override
    public void addNode(Node node) {
        if (node instanceof UnitCellNode && !adjNodes.containsKey(node))
            initCellNodeEntry((UnitCellNode) node);
    }

    @Override
    public void removeNode(Node node) {
        super.removeNode(node);
    }

    @Override
    protected void removeRelationsOf(Node node) {
        adjNodes.get(node).stream()
                .filter(Objects::nonNull)
                .map(Transition::getEnd)
                .map(adjNodes::get)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .filter(tr -> tr.getEnd() == node)
                .forEach(tr -> tr = null);
    }

    public void addTransition(Node start, Node end, int vec) throws Exception {
        addTransition(start, end, vec, true);
    }

    private void addTransition(Node start, Node end, int vec, boolean first) throws Exception {
        addNode(start);
        if (adjNodes.get(start).get(vec) != null)
            throw new Exception("Cell already has neighbour on current vector");
        adjNodes.get(start).set(vec, new Transition(start, end));
        if (first)
            addTransition(end, start, UnitCellNode.vecReverse(vec), false);
    }


    private void initCellNodeEntry(UnitCellNode node) {
        this.adjNodes.put(node, new ArrayList<>(Collections
                .nCopies(UnitCellNode.NEIGHBOURS_COUNT, null)));
    }

    private int getIndX(int x) { return x - MIN_X; }
    private int getIndY(int y) { return y - MIN_Y; }

    private void initMINS(int x, int y) {
        MIN_X = x;
        MIN_Y = y;
    }

    //region    THIS MAP GETTERS
    public List<List<UnitCellNode>> getGrid() {
        return grid;
    }

    public UnitCellNode get(IntCoord coord) {
        return get(coord.X(), coord.Y());
    }

    public UnitCellNode get(int x, int y) {
        return grid.get(y - MIN_Y).get(x - MIN_X);
    }

    public List<List<UnitCellNode>> get(IntCoord coord1, IntCoord coord2) {
        return get(coord1, coord2, this.grid);
    }

    public List<List<UnitCellNode>> get(int x0, int y0, int x1, int y1) {
        return get(x0, y0, x1, y1, this.grid);
    }

    public List<UnitCellNode> getLine(int y) {
        return getLine(y, this.grid);
    }
    public List<UnitCellNode> getRow(int y, int x0, int x1) {
        return getRow(y, x0, x1, this.grid);
    }

    //endregion

    //region GETTERS

    private UnitCellNode get(int x, int y, List<List<UnitCellNode>> cellRect) {
        return cellRect.get(getIndY(y)).get(getIndX(x));
    }

    private UnitCellNode get(IntCoord coord, List<List<UnitCellNode>> cellRect) {
        return get(coord.X(), coord.Y(), cellRect);
    }

    private List<List<UnitCellNode>> get(IntCoord coord1, IntCoord coord2, List<List<UnitCellNode>> cellRect) {
        return get(coord1.X(), coord1.Y(), coord2.X(), coord2.Y(), cellRect);
    }

    private List<List<UnitCellNode>> get(int x0, int y0, int x1, int y1, List<List<UnitCellNode>> cellRect) {
        List<List<UnitCellNode>> region = new ArrayList<>();
        for (int y = y0; y < y1; y++)
            region.add(getRow(y, x0, x1, cellRect));

        return region;
    }

    private List<UnitCellNode> getLine(int y, List<List<UnitCellNode>> cellRect) {
        return cellRect.get(getIndY(y));
    }
    private List<UnitCellNode> getRow(int y, int x0, int x1, List<List<UnitCellNode>> cellRect) {
        return getLine(y, cellRect).subList(getIndX(x0), getIndX(x1) + 1);
    }

    //endregion
    //region    RECTANGLE

    public List<List<UnitCellNode>> cellRectangle(int x0, int y0, int x1, int y1) {
        UnitCellNode cell;
        List<List<UnitCellNode>> map = new ArrayList<>();
        List<UnitCellNode> cellLine = new ArrayList<>();

        try {
            //  first line
            cell = UnitCellNode.at(x0, y0);
            cellLine.add(cell);
            for (int w = x0 + 1; w < x1+1; w++) {
                cell = UnitCellNode.at(w, y0);
                cellLine.add(cell);
                addTransition(cell, cellLine.get(w-x0-1), UnitCellNode.VEC_LEFT);
            }
            map.add(cellLine);

            //  center lines
            for (int h = y0 + 1; h < y1+1; h++) {
                cell = UnitCellNode.at(x0, h);
                cellLine = new ArrayList<>();
                cellLine.add(cell);
                for (int w = x0 + 1; w < x1; w++) {
                    cell = UnitCellNode.at(w, h);
                    cellLine.add(cell);
                    addTransition(cell, cellLine.get(w-x0-1), UnitCellNode.VEC_LEFT);
                    addTransition(cell, get(w-1, h-1, map)  , UnitCellNode.VEC_TOP_LEFT);
                    addTransition(cell, get(w, h-1, map)    , UnitCellNode.VEC_TOP);
                    addTransition(cell, get(w+1, h-1, map)  , UnitCellNode.VEC_TOP_RIGHT);
                }
                cell = UnitCellNode.at(x1, h);
                cellLine.add(cell);
                addTransition(cell, cellLine.get(x1-x0-1), UnitCellNode.VEC_LEFT);
                addTransition(cell, get(x1-1, h-1, map)  , UnitCellNode.VEC_TOP_LEFT);
                addTransition(cell, get(x1, h-1, map)    , UnitCellNode.VEC_TOP);
                map.add(cellLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private List<List<UnitCellNode>> cellRectangle(IntCoord coord1, IntCoord coord2) {
        return cellRectangle(coord1.X(), coord1.Y(), coord2.X(), coord2.Y());
    }
    private List<List<UnitCellNode>> cellRectangle(Zone zone) {
        return cellRectangle(zone.MIN_X(), zone.MIN_Y(), zone.MAX_X(), zone.MAX_Y());
    }

    //endregion

    private List<List<UnitCellNode>> figure(Zone zone) {
        var ET = EdgeTable(zone);
        var cellRect = cellRectangle(zone);

        List<List<UnitCellNode>> cellMap = new ArrayList<>();
        List<UnitCellNode> cellLine = new ArrayList<>();
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

            //  remove cells NOT included to Zone
            for (var cell : cellRect.get(y))
                if (!cellLine.contains(cell))
                    removeNode(cell);


            cellMap.add(cellLine);
            cellLine.clear();
        }

        return cellMap;
    }

    private List<List<HSLS>> EdgeTable (Zone zone) {
        return EdgeTable(toHPolygon(zone.getShape()), zone.MAX_Y() - zone.MIN_Y() + 1);
    }

    private List<List<HSLS>> EdgeTable (List<HSLS> HPolygon, int height) {
        List<List<HSLS>> ET = new ArrayList<>(height);
        for (int k = 0; k < height; k++)
            ET.add(new ArrayList<>());

        HSLS prev2 = HPolygon.remove(0);
        HSLS prev = HPolygon.remove(0);
        HSLS curr = HPolygon.remove(0);
        HSLS next = HPolygon.get(HPolygon.size()-1);
        HSLS first = HPolygon.get(HPolygon.size()-2);

        //process first n second hsls
        processET(first, next, prev2, prev, ET);
        processET(next, prev2, prev, curr, ET);

        first = prev2;

        for (HSLS hel : HPolygon) {
            next = hel;
            processET(prev2, prev, curr, next, ET);

            prev2 = prev;
            prev = curr;
            curr = next;
        }

        //  process last hsls
        processET(prev2, prev, curr, first, ET);

        ET.forEach(list -> list.sort(Comparator.comparing(HSLS::xL)
                                        .thenComparing(HSLS::xR)));
        return ET;
    }

    private void processET (HSLS prev2, HSLS prev, HSLS curr, HSLS next, List<List<HSLS>> ET) {

        ET.get(getIndY(curr.y())).add(curr);
        if (prev.y() == curr.y())
            if (curr.y() == next.y() ||
                    (prev2.y() != curr.y() && prev2.y() != next.y()))
                ET.get(getIndY(curr.y())).add(curr);


//        if (curr.y() == next.y()) {
//            if (curr.y() == prev.y()) {
//                ET.get(getIndY(curr.y())).add(curr);
//                ET.get(getIndY(curr.y())).add(curr);
//            } else if (curr.width() >= next.width()) {
//                ET.get(getIndY(curr.y())).add(curr);
//            }
//        } else if (curr.y() == prev.y()) {
//            if (curr.width() > prev.width()) {
//                ET.get(getIndY(curr.y())).add(curr);
//            }
//        } else  ET.get(getIndY(curr.y())).add(curr);
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

        if (y0 == y1) {
            if      (x0 < x1)   return new ArrayList<>() {{  add(new HSLS(y0, x0, x1));  }};
            else                return new ArrayList<>() {{  add(new HSLS(y0, x1, x0));  }};
        }

        if (y0 < y1) {
            if      (x0 < x1) {
                if  (x1-x0 > y1-y0)     return toHLineLow (x0, y0, x1, y1, +1, false);
                else                    return toHLineHigh(x0, y0, x1, y1, +1, false);
            }
            else if (x0 > x1) {
                if (x0-x1 > y1-y0)      return toHLineLow (x1, y1, x0, y0, -1, true);
                else                    return toHLineHigh(x0, y0, x1, y1, -1, false);
            }
            else                        return new ArrayList<>() {{
                                            for (int y = y0; y < y1 + 1; y++)
                                                add(new HSLS(y, x0, x1));
                                        }};
        } else {
            if      (x0 < x1) {
                if  (x1-x0 > y0-y1)     return toHLineLow (x0, y0, x1, y1, -1, false);
                else                    return toHLineHigh(x1, y1, x0, y0, -1, true);
            }
            else if (x0 > x1) {
                if (x0-x1 > y1-y0)      return toHLineLow (x1, y1, x0, y0, +1, true);
                else                    return toHLineHigh(x1, y1, x0, y0, +1, true);
            }
            else                        return new ArrayList<>() {{
                                            for (int y = y0; y > y1 - 1; y--)
                                                add(new HSLS(y, x0, x1));
                                        }};
        }
    }

    /**
     * uses Bresenham algorithm
     */
    private List<HSLS> toHLineLow(int x0, int y0, int x1, int y1, int y_step, boolean rev) {
        int _2dy = y1 - y0;
        int _2dx = x1 - x0;
        int errD = 2 * _2dy * y_step;
        int err = errD - _2dx;

        List<HSLS> HSLSArr = new ArrayList<>();
        int  xL = x0;
        int y = y0;
        boolean flag = false;
        for (int xR = x0; xR < x1 + 1; xR++) {
            flag = false;
            if (err >= 0){
                err -= 2 * _2dx;
                HSLSArr.add(new HSLS(y, xL, xR));
                y += y_step;
                xL = xR + 1;
                flag = true;
            }
            err += errD;
        }
        if (!flag)
            HSLSArr.add(new HSLS(y, xL, x1));

        if (rev)
            Collections.reverse(HSLSArr);

        return HSLSArr;
    }

    /**
     * uses Bresenham algorithm swapped coord dims
     */
    private List<HSLS> toHLineHigh(int x0, int y0, int x1, int y1, int x_step, boolean rev) {
        int _2dy = y1 - y0;
        int _2dx = x1 - x0;
        int errD = 2 * _2dx * x_step;
        int err = errD - _2dy;

        List<HSLS> HSLSArr = new ArrayList<>();
        int x = x0;
        for (int y = y0; y < y1 + 1; y++) {
            HSLSArr.add(new HSLS(y, x, x));
            if (err >= 0){
                err -= 2 * _2dy;
                x += x_step;
            }
            err += errD;
        }

        if (rev)
            Collections.reverse(HSLSArr);

        return HSLSArr;
    }

    //endregion


    //region    STATIC METHODS
    public static CellNodeGrid of (IntCoord coord1, IntCoord coord2) {
        return new CellNodeGrid(coord1, coord2);
    }

    public static CellNodeGrid of (Zone zone) {
        return new CellNodeGrid(zone);
    }

    public static CellNodeGrid of (Area area) {
        return null;
    }
    //endregion
}
