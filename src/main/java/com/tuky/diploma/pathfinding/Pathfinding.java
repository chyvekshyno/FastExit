package com.tuky.diploma.pathfinding;

import com.tuky.diploma.structures.graph.Node;

import java.util.List;

public interface Pathfinding<N extends Node<?>> {
    List<N> path(N source, N goal);
}
