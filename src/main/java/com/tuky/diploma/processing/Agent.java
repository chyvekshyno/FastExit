package com.tuky.diploma.processing;

import com.tuky.diploma.pathfinding.Pathfinding;
import com.tuky.diploma.structures.graph.Node;

import java.util.List;

public class Agent<N extends Node<?>> {
    private N position;
    private List<N> path;

    public Agent(N position) {
        this.position = position;
    }

    public void move() throws Exception {
        if (position != path.get(0))
            throw new Exception("agents position and path are not synchronised ");
        path.remove(0);
        position = path.get(0);
    }

    public void updatePath(Pathfinding<N> pathfinding, N target) {
        path = pathfinding.path(position, target);
    }
}
