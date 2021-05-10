package com.tuky.diploma.pathfinding;

import com.tuky.diploma.structures.graph.Node;

import java.util.Map;

public interface Pathfinding<N extends Node<?>> {
    Map<N, N> path(N source, N goal);
}
