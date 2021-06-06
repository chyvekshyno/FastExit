package com.tuky.diploma.structures.area.regularnet;

import com.tuky.diploma.structures.addition.HSLS;
import com.tuky.diploma.structures.area.Area;
import com.tuky.diploma.structures.area.Zone;
import com.tuky.diploma.structures.graph.Moore2D;
import com.tuky.diploma.structures.graph.NodeMoore2D;
import com.tuky.diploma.structures.graph.Transition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class RegularNetMoore2D
        <N extends NodeMoore2D<? extends Comparable<?>, Integer>>
        extends RegularNet2D<N> {

    public RegularNetMoore2D(Area area) {
        super(area);
    }

    @Override
    protected void removeRelationsOf(N node) {
        for (int vec = 0; vec < Moore2D.NEIGHBOURS_COUNT; vec++) {
            removeRelationOf(node, vec);
        }
    }

    protected void removeRelationOf(N node, int vec) {
        var tr = adjTable.get(node).get(vec);
        if (tr != null) {
            adjTable.get(tr.getEnd()).set(Moore2D.vecReverse(vec), null);
        }
        adjTable.get(node).set(vec, null);
    }

    @Override
    protected void initCellNodeEntry(N node) {
        this.adjTable.put(node, new ArrayList<>(Collections
                .nCopies(NodeMoore2D.NEIGHBOURS_COUNT, null)));
    }

    @Override
    public void isolate(N node) {
        List<Transition<N>> tranList = adjTable.get(node);
        N neighbour;
        for (int vec = 0; vec < Moore2D.NEIGHBOURS_COUNT; vec++) {
            if (tranList.get(vec) == null)  continue;

            neighbour = tranList.get(vec).getEnd();
            adjTable.get(neighbour).get(Moore2D.vecReverse(vec)).setWeight(10000.);
//            tranList.get(vec).setWeight(10000.);
        }
    }

    @Override
    protected List<List<N>> figure(Zone zone, List<List<N>> cellRect) {
        var ET = EdgeTable(zone);
        var ExitHSLS = toHPolygon(zone.getExits());

        List<List<N>> cellMap = new ArrayList<>();
        List<N> cellLine = new ArrayList<>();
        HSLS hsls0, hsls1;
        for (List<HSLS> bucket : ET) {
            for (int i = 0; i < bucket.size(); i++) {
                hsls0 = bucket.get(i++);
                hsls1 = bucket.get(i);
                cellLine.addAll(getRowAtRect(hsls0.y(), hsls0.xR() + 1, hsls1.xL() - 1, cellRect));

                if (hsls0.getFlag() == -1) {
                    removeRelationOf(
                            cellRect.get(getIndY(hsls0.y())).get(getIndX(hsls0.xL() + 1)),
                            Moore2D.VEC_TOP_LEFT);
                } else if (hsls0.getFlag() == 1) {
                    removeRelationOf(
                            cellRect.get(getIndY(hsls0.y())).get(getIndX(hsls0.xL() + 1)),
                            Moore2D.VEC_BOTTOM_LEFT);
                }

                if (hsls1.getFlag() == -1) {
                    removeRelationOf(
                            cellRect.get(getIndY(hsls0.y())).get(getIndX(hsls1.xL())),
                            Moore2D.VEC_BOTTOM_RIGHT);
                } else if (hsls1.getFlag() == 1) {
                    removeRelationOf(
                            cellRect.get(getIndY(hsls0.y())).get(getIndX(hsls1.xL())),
                            Moore2D.VEC_TOP_RIGHT);
                }
            }

            cellMap.add(cellLine.stream()
                    .distinct()
                    .collect(Collectors.toList()));

            cellLine.clear();
        }

        for (var hsls : ExitHSLS)
            cellMap.get(getIndY(hsls.y()))
                    .addAll(getRowAtRect(hsls.y(), hsls.xL(), hsls.xR(), cellRect));

        return cellMap;
    }
}
