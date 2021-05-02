package com.tuky.diploma.pathfinding;

import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.structures.graph.Node;
import com.tuky.diploma.structures.graph.Node2D;

import java.util.*;

public class BiDirectionalDijkstra {

    public static <N extends Node2D<?,?>, G extends Graph<N>> List<N> path (G graph, N source, N target) {

        //  init data structures
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
        N u = null;
        N v = null;
        while (!openF.isEmpty() && !openB.isEmpty()) {
            u = openF.poll();
            process(u, graph, distF, parentF, openF, closedF);
            if (closedB.contains(u))
                return shortestPath(source, parentF, distF, closedF,
                                    target, parentB, distB, closedB);

            v = openB.poll();
            process(v, graph, distB, parentB, openB, closedB);
            if (closedF.contains(v))
                return shortestPath(source, parentF, distF, closedF,
                                    target, parentB, distB, closedB);
        }
        return null;
    }

    private static <N extends Node2D<?,?>> N getMin (Map<N, Double> dist) {
        return dist.keySet().stream().min(Comparator.comparing(dist::get)).orElse(null);
    }

    private static <N extends Node2D<?,?>, G extends Graph<N>> void process
            (N u, G graph, Map<N, Double> dist, Map<N, N> parent, Queue<N> open, Set<N> closed) {

        for (var tr : graph.getAdjTable().get(u)) {
            if (tr == null)
                continue;

            relax(u, tr.getEnd(), tr.getWeight(), dist, parent, open, closed);
        }
        closed.add(u);
    }

    private static <N extends Node2D<?,?>> void relax
            (N u, N v, double weight, Map<N, Double> dist, Map<N, N> parent, Queue<N> open, Set<N> closed) {
        if (!dist.containsKey(v) || dist.get(v) > dist.get(u) + weight) {
            dist.put(v, dist.get(u) + weight);
            parent.put(v, u);
            if (!open.contains(v) && !closed.contains(v))
                open.add(v);
        }
    }

    private static <N extends Node2D<?,?>, G extends Graph<N>> List<N> shortestPath
            (N source, Map<N,N> parentF, Map<N, Double> distF, Set<N> closedF,
             N target, Map<N,N> parentB, Map<N, Double> distB, Set<N> closedB) {

        double distBest = Double.POSITIVE_INFINITY;
        N nodeBest = null;

        Set<N> closedCommon = new HashSet<>(closedF);
        closedCommon.addAll(closedB);

        for (var node : closedCommon) {
            if (!distF.containsKey(node) || !distB.containsKey(node))
                continue;

            if (distF.get(node) + distB.get(node) < distBest) {
                distBest = distF.get(node) + distB.get(node);
                nodeBest = node;
            }
        }

        List<N> path = new ArrayList<>();
        N last = nodeBest;
        while (last != source) {
            path.add(last);
            last = parentF.get(last);
        } path.add(source);
        Collections.reverse(path);

        last = nodeBest;
        while (last != target) {
            path.add(last);
            last = parentB.get(last);
        } path.add(target);

        return path;
    }

}
