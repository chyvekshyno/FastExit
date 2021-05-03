package com.tuky.diploma.pathfinding;

import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.structures.graph.Node;
import com.tuky.diploma.structures.graph.Node2D;

import java.util.*;

public class BiDirectionalDijkstra extends Dijkstra {

    protected BiDirectionalDijkstra() {
        super();
    }

    public static <N extends Node2D<?,Integer>, G extends Graph<N>> List<N> path (G graph, N source, N target) {
        return new BiDirectionalDijkstra()._path(graph, source, target);
    }

    @Override
    protected <N extends Node2D<?,Integer>, G extends Graph<N>> List<N> _path (G graph, N source, N target) {
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
            process(u, graph, distF, parentF, openF, closedF);
            if (closedB.contains(u))
                return pathTraceback(source, target, u, parentF, parentB);

            v = openB.poll();
            process(v, graph, distB, parentB, openB, closedB);
            if (closedF.contains(v))
                return pathTraceback(source, target, v, parentF, parentB);
        }
        return null;
    }

    protected <N extends Node2D<?,Integer>> List<N> pathTraceback
            (N source, N target, N touch, Map<N,N> parentF, Map<N,N> parentB) {

        List<N> path = new ArrayList<>();
        N last = touch;
        while (last != source) {
            path.add(last);
            last = parentF.get(last);
        } path.add(source);
        Collections.reverse(path);

        last = touch;
        while (last != target) {
            path.add(last);
            last = parentB.get(last);
        } path.add(target);

        return path;
    }

    protected <N extends Node2D<?,Integer>> List<N> shortestPath
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
        return pathTraceback(source, target, nodeBest, parentF, parentB);
    }

}
