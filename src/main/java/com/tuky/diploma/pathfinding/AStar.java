package com.tuky.diploma.pathfinding;

import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.structures.graph.Node2D;

import java.util.*;

public class AStar extends Dijkstra{

    protected Node2D<?, Integer> target;

    protected <N extends Node2D<?,Integer>> AStar(N target) {
        this.target = target;
    }

    public static <N extends Node2D<?,Integer>, G extends Graph<N>> List<N> path (G graph, N source, N target) {
        return new AStar(target)._path(graph, source, target);
    }

    protected <N extends Node2D<?,Integer>> double potential
            (N node, N target) {
        double dx = node.getCoord().X() - target.getCoord().X();
        double dy = node.getCoord().Y() - target.getCoord().Y();
        return Math.sqrt(dx*dx + dy*dy);
    }

    protected  <N extends Node2D<?,Integer>, G extends Graph<N>> void process
            (N curr, G graph, Map<N, Double> dist, Map<N, N> parent, Queue<N> open, Set<N> closed) {

        double lenPi = 0;
        for (var tr : graph.getAdjTable().get(curr)) {
            if (tr == null)
                continue;

            lenPi = tr.getWeight() - potential(curr, target) + potential(tr.getEnd(), target);
            relax(curr, tr.getEnd(), lenPi, dist, parent, open, closed);
        }
        closed.add(curr);
    }



}
