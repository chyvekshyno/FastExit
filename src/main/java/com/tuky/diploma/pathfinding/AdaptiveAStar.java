package com.tuky.diploma.pathfinding;

import com.tuky.diploma.processing.Agent;
import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.structures.graph.Node2D;

import java.util.List;
import java.util.Map;

public class AdaptiveAStar<N extends Node2D<?, Integer>>
        extends AStar<N> {

    public AdaptiveAStar(Graph<N> graph) {
        super(graph);
    }

    public static <N extends Node2D<?,Integer>> Map<N, N> path (Graph<N> graph, N source, N target) {
        return new AdaptiveAStar<>(graph).path(source, target);
    }

    protected void updateH() {
        for (var node : closed) {
            H.put(node, dist.get(target) + H.get(target) - dist.get(node));
        }
    }

    @Override
    protected Map<N, N> algorithm(N source, N target) {
        N current;
        while (!open.isEmpty()) {
            current = open.poll();
            process(current, dist, parent, open, closed);
            if (current == target) {
                updateH();
                return pathTraceback(source, current, parent);
            }
        }
        return null;
    }

    @Override
    protected void initData(N source, N target) {
        super.initData(source, target);
    }
}
