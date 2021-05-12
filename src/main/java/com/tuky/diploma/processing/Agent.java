package com.tuky.diploma.processing;

import com.tuky.diploma.pathfinding.Pathfinding;
import com.tuky.diploma.structures.graph.Node;

import java.util.HashMap;
import java.util.Map;

public class Agent<N extends Node<?>> {
    private N position;
    private Map<N, N> path;

    public Agent(N position) {
        this.position = position;
        path = new HashMap<>();
    }

    public N getPosition() {
        return position;
    }

    public Map<N,N> getPath() {
        return path;
    }

    public void move(){
        N temp = position;
        position = path.get(position);
        path.remove(temp);
    }

    public void updatePath(Pathfinding<N> pathfinding, N target) {
        path = pathfinding.path(position, target);
    }
}
