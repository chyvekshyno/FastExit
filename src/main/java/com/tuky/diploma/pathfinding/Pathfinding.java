package com.tuky.diploma.pathfinding;

import com.tuky.diploma.structures.graph.Node;

import java.util.Map;

public interface Pathfinding<N extends Node<?>> {
    Map<N, N> path(N source, N goal);


    /**
     * @return last founded path
     */
    Map<N, N> getPath();

    /**
     * @return length of last founded path
     */
    double getPathLen();
}
