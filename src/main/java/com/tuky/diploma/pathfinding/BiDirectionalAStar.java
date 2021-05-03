package com.tuky.diploma.pathfinding;

import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.structures.graph.Node2D;

import java.util.*;

public class BiDirectionalAStar extends BiDirectionalDijkstra{

    public static <N extends Node2D<?,Integer>, G extends Graph<N>> List<N> path (G graph, N source, N target) {
        return new BiDirectionalAStar()._path(graph, source, target);
    }

    @Override
    protected <N extends Node2D<?, Integer>, G extends Graph<N>> List<N> _path(G graph, N source, N target) {
        Map<N, N> parentF = new HashMap<>();
        Map<N, N> parentB = new HashMap<>();

        Map<N, Double> distF = new HashMap<>();
        Map<N, Double> distB = new HashMap<>();

        Queue<N> openF = new PriorityQueue<>(Comparator.comparing(distF::get));
        Queue<N> openB = new PriorityQueue<>(Comparator.comparing(distB::get));

        Set<N> closedF = new HashSet<>();
        Set<N> closedB = new HashSet<>();

        parentF.put(source, null);
        parentB.put(target, null);

        openF.add(source);
        openB.add(target);

        distF.put(source, 0.);
        distB.put(target, 0.);

        //  start an algorithm
        N u, v;
        while (!openF.isEmpty() && !openB.isEmpty()) {
            u = openF.poll();
            process(u, target, graph, distF, parentF, openF, closedF);
            if (closedB.contains(u))
                return pathTraceback(source, target, u, parentF, parentB);

            v = openB.poll();
            process(v, source, graph, distB, parentB, openB, closedB);
            if (closedF.contains(v))
                return pathTraceback(source, target, v, parentF, parentB);
        }
        return null;
    }

    protected <N extends Node2D<?, Integer>, G extends Graph<N>> void process
            (N curr, N target, G graph, Map<N, Double> dist, Map<N,N> parent, Queue<N> open, Set<N> closed) {
        double lenPi;
        for (var tr : graph.getAdjTable().get(curr)) {
            if (tr == null)
                continue;

            lenPi = tr.getWeight() - potential(curr, target) + potential(tr.getEnd(), target);
            relax(curr, tr.getEnd(), lenPi, dist, parent, open, closed);
        }
        closed.add(curr);
    }

    protected <N extends Node2D<?,Integer>> double potential
            (N node, N target) {
        double dx = node.getCoord().X() - target.getCoord().X();
        double dy = node.getCoord().Y() - target.getCoord().Y();
        return Math.sqrt(dx*dx + dy*dy);
    }
}
