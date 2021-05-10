package com.tuky.diploma.pathfinding;

import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.structures.graph.Node2D;

import java.util.*;

public class BiDirectionalAStar<N extends Node2D<?, Integer>>
        extends BiDirectionalDijkstra<N>{

    public BiDirectionalAStar(Graph<N> graph) {
        super(graph);
    }

    public static <N extends Node2D<?,Integer>> Map<N, N> path (Graph<N> graph, N source, N target) {
        return new BiDirectionalAStar<>(graph).path(source, target);
    }

    @Override
    protected Map<N, N> algorithm(N source, N target) {
        N u, v;
        while (!open.isEmpty() && !openB.isEmpty()) {
            u = open.poll();
            process(u, target, dist, parent, open, closed);
            if (closedB.contains(u))
                return pathTraceback(source, target, u, parent, parentB);

            v = openB.poll();
            process(v, source, distB, parentB, openB, closedB);
            if (closed.contains(v))
                return pathTraceback(source, target, v, parent, parentB);
        }
        return null;
    }

    protected void process(N curr, N target,
                           Map<N, Double> dist,
                           Map<N,N> parent,
                           Queue<N> open,
                           Set<N> closed) {
        double lenPi;
        for (var tr : graph.getAdjTable().get(curr)) {
            if (tr == null)
                continue;

            lenPi = tr.getWeight() - potential(curr, target) + potential(tr.getEnd(), target);
            relax(curr, tr.getEnd(), lenPi, dist, parent, open, closed);
        }
        closed.add(curr);
    }

    protected double potential(N node, N target) {
        double dx = node.getCoord().X() - target.getCoord().X();
        double dy = node.getCoord().Y() - target.getCoord().Y();
        return Math.sqrt(dx*dx + dy*dy);
    }
}
