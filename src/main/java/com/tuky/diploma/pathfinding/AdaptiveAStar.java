package com.tuky.diploma.pathfinding;

import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.structures.graph.Node2D;

import java.util.Map;

public class AdaptiveAStar<N extends Node2D<?, Integer>>
        extends AStar<N> {

    public AdaptiveAStar(Graph<N> graph) {
        super(graph);
    }

    public static <N extends Node2D<?,Integer>> Map<N, N> path (Graph<N> graph, N source, N target) {
        return new AdaptiveAStar<>(graph).path(source, target);
    }

    protected void updateH(N curr) {
        closed.forEach(node -> H.put(node, dist.get(curr) + H.get(curr) - dist.get(node)));
    }

    @Override
    protected Map<N, N> algorithm(N source, N target) {
        N current;
        while (!open.isEmpty()) {
            current = open.poll();
            process(current, dist, parent, open, closed);
            if (current == target) {
                updateH(current);
                return pathTraceback(source, current, parent);
            }
        }
        return null;
    }
}
