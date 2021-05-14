package com.tuky.diploma.structures.area.regularnet;

import com.tuky.diploma.structures.area.Area;
import com.tuky.diploma.structures.area.IntCoord;
import com.tuky.diploma.structures.area.Zone;
import com.tuky.diploma.structures.graph.Moore2D;
import com.tuky.diploma.structures.graph.NodeMoore2D;
import com.tuky.diploma.structures.graph.Transition;

import java.util.*;

public abstract class RegularNetMoore2D
        <N extends NodeMoore2D<? extends Comparable<?>, Integer>>
        extends RegularNet2D<N> {

    public RegularNetMoore2D(Area area) {
        super(area);
    }

    @Override
    protected void removeRelationsOf(N node) {
        List<Transition<N>> tranList = adjTable.get(node);
        N neighbour;
        for (int vec = 0; vec < Moore2D.NEIGHBOURS_COUNT; vec++) {
            if (tranList.get(vec) == null)
                continue;

            neighbour = tranList.get(vec).getEnd();
            adjTable.get(neighbour).set(Moore2D.vecReverse(vec), null);
        }
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
}
