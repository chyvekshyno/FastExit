package com.tuky.diploma.structures.area.regularnet;

import com.tuky.diploma.structures.addition.HSLS;
import com.tuky.diploma.structures.area.IntCoord;
import com.tuky.diploma.structures.area.Side;
import com.tuky.diploma.structures.area.Zone;
import com.tuky.diploma.structures.graph.*;

import java.util.*;
import java.util.stream.Collectors;

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
public abstract class RegularNet<N extends Node2D<? extends Comparable<?>, Integer>>
        extends Graph<N> {

    //region    FIELDS
    private int MIN_X;
    private int MIN_Y;
    private List<List<N>> grid;
    private Class<N> nClass;

    //endregion

    //region    CONSTRUCTORS


    public RegularNet() {
        super();
    }

    public RegularNet(int x0, int y0, int x1, int y1) {
        this();
        initMINS(x0, y0);
        grid = cellRectangle(x0, y0, x1, y1);
    }

    public RegularNet(IntCoord coord1, IntCoord coord2) {
        this(coord1.X(), coord1.Y(), coord2.X(), coord2.Y());
    }

    public RegularNet(Zone zone) {
        this();
        initMINS(zone.MIN_X(), zone.MIN_Y());
        grid = figure(zone);
    }

    //endregion


    //region    METHODS
    @Override
    public void addNode(N node) {
        if (node != null && !adjNodes.containsKey(node))
            initCellNodeEntry(node);
    }

    @Override
    public void removeNode(N node) {
        super.removeNode(node);
    }

    @Override
    protected void removeRelationsOf(N node) {
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

    public void addTransition(N start, N end, int vec) throws Exception {
        addTransition(start, end, vec, true);
    }

    private void addTransition(N start, N end, int vec, boolean first) throws Exception {
        addNode(start);
        if (adjNodes.get(start).get(vec) != null)
            throw new Exception("Cell already has neighbour on current vector");
        adjNodes.get(start).set(vec, new Transition(start, end));
        if (first)
            addTransition(end, start, NodeMoore2D.vecReverse(vec), false);
    }


    private void initCellNodeEntry(N node) {
        this.adjNodes.put(node, new ArrayList<>(Collections
                .nCopies(NodeMoore2D.NEIGHBOURS_COUNT, null)));
    }

    private int getIndX(int x) { return x - MIN_X; }
    private int getIndY(int y) { return y - MIN_Y; }

    private void initMINS(int x, int y) {
        MIN_X = x;
        MIN_Y = y;
    }

    //region    THIS GRID MAP GETTERS
    public N get(IntCoord coord) {
        return get(coord.X(), coord.Y());
    }

    public N get(int x, int y) {
        return grid.get(y - MIN_Y)
                .stream()
                .filter(cell -> cell.getCoord().X() == x)
                .findFirst().orElse(null);
    }

    //endregion

    //region GETTERS

    protected N getAtRect(int x, int y, List<List<N>> cellRect) {
        return cellRect.get(getIndY(y)).get(getIndX(x));
    }

    protected N getAtRect(IntCoord coord, List<List<N>> cellRect) {
        return getAtRect(coord.X(), coord.Y(), cellRect);
    }

    protected List<List<N>> getAtRect(IntCoord coord1, IntCoord coord2, List<List<N>> cellRect) {
        return getAtRect(coord1.X(), coord1.Y(), coord2.X(), coord2.Y(), cellRect);
    }

    protected List<List<N>> getAtRect(int x0, int y0, int x1, int y1, List<List<N>> cellRect) {
        List<List<N>> region = new ArrayList<>();
        for (int y = y0; y < y1; y++)
            region.add(getRowAtRect(y, x0, x1, cellRect));

        return region;
    }

    protected List<N> getLine(int y, List<List<N>> cellRect) {
        return cellRect.get(getIndY(y));
    }

    protected List<N> getRowAtRect(int y, int x0, int x1, List<List<N>> cellRect) {
        return getLine(y, cellRect).subList(getIndX(x0), getIndX(x1) + 1);
    }

    //endregion

    //region    RECTANGLE BUILDING

    public List<List<N>> cellRectangle(int x0, int y0, int x1, int y1) {
        List<List<N>> map = new ArrayList<>();
        try {
            map.add(cellLineFirst(y0, x0, x1, map));    //  first line

            for (int h = y0 + 1; h < y1+1; h++)         //  center lines
                map.add(cellLine(h, x0, x1, map));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    protected abstract List<N> cellLineFirst(int y, int x0, int x1, List<List<N>> map);
    protected abstract List<N> cellLine(int y, int x0, int x1, List<List<N>> map);

    private List<List<N>> cellRectangle(IntCoord coord1, IntCoord coord2) {
        return cellRectangle(coord1.X(), coord1.Y(), coord2.X(), coord2.Y());
    }
    private List<List<N>> cellRectangle(Zone zone) {
        return cellRectangle(zone.MIN_X(), zone.MIN_Y(), zone.MAX_X(), zone.MAX_Y());
    }
    //endregion

    //region    ZONE BUILDING

    private List<List<N>> figure(Zone zone) {
        var ET = EdgeTable(zone);
        var cellRect = cellRectangle(zone);

        List<List<N>> cellMap = new ArrayList<>();
        List<N> cellLine = new ArrayList<>();
        List<HSLS> bucket;
        HSLS hsls0, hsls1;
        for (int y = 0; y < ET.size(); y++) {
            bucket = ET.get(y);

            for (int i = 0; i < bucket.size(); i++) {
                hsls0 = bucket.get(i++);
                hsls1 = bucket.get(i);
                cellLine.addAll(getRowAtRect(hsls0.y(), hsls0.xL(), hsls1.xR(), cellRect));
            }


            //  remove cells NOT included to Zone
            for (var cell : cellRect.get(y))
                if (!cellLine.contains(cell))
                    removeNode(cell);

            cellMap.add(cellLine.stream()
                        .distinct()
                        .collect(Collectors.toList()));
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

        if (curr.y() == next.y())
            if (!curr.isBigger(next))
                return;

        if (curr.y() == prev.y()) {
            if (prev2.y() == next.y())
                ET.get(getIndY(curr.y())).add(curr);
            if (curr.isLower(prev))
                return;
        }

        ET.get(getIndY(curr.y())).add(curr);
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
                                            for (int y = y0; y < y1; y++)
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
                                            for (int y = y0; y > y1; y--)
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

    //endregion
}
