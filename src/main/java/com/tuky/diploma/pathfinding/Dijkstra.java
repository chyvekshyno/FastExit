package com.tuky.diploma.pathfinding;

import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.structures.graph.Node2D;

import java.util.*;

public class Dijkstra {

    public static <N extends Node2D<?,Integer>, G extends Graph<N>> List<N> path (G graph, N source, N target) {
        return new Dijkstra()._path(graph, source, target);
    }

    public static <N extends Node2D<?,Integer>, G extends Graph<N>> Map<N, Double> distTree (G graph, N source) {
        return new Dijkstra()._distTree(graph, source);
    }

    protected <N extends Node2D<?,Integer>, G extends Graph<N>> Map<N, Double> _distTree (G graph, N source) {
        Map<N, N> parent = new HashMap<>();
        Map<N, Double> dist = new HashMap<>();
        Queue<N> open = new PriorityQueue<>(Comparator.comparing(dist::get));
        Set<N> closed = new HashSet<>();

        parent.put(source, null);
        open.add(source);
        dist.put(source, 0.);

        //  start an algorithm
        N current;
        while (!open.isEmpty()) {
            current = open.poll();
            process(current, graph, dist, parent, open, closed);
        }
        return dist;
    }

    protected  <N extends Node2D<?,Integer>, G extends Graph<N>> List<N> _path (G graph, N source, N target) {
        Map<N, N> parent = new HashMap<>();
        Map<N, Double> dist = new HashMap<>();
        Queue<N> open = new PriorityQueue<>(Comparator.comparing(dist::get));
        Set<N> closed = new HashSet<>();

        parent.put(source, null);
        open.add(source);
        dist.put(source, 0.);

        //  start an algorithm
        N current;
        while (!open.isEmpty()) {
            current = open.poll();
            process(current, graph, dist, parent, open, closed);
            if (current == target)
                return pathTraceback(source, current, parent);
        }
        return null;
    }

    protected <N extends Node2D<?,Integer>, G extends Graph<N>> void process
            (N u, G graph, Map<N, Double> dist, Map<N, N> parent, Queue<N> open, Set<N> closed) {

        for (var tr : graph.getAdjTable().get(u)) {
            if (tr == null)
                continue;
            relax(u, tr.getEnd(), tr.getWeight(), dist, parent, open, closed);
        }
        closed.add(u);
    }

    protected <N extends Node2D<?,Integer>> void relax
            (N u, N v, double weight, Map<N, Double> dist, Map<N, N> parent, Queue<N> open, Set<N> closed) {
        if (!dist.containsKey(v) || dist.get(v) > dist.get(u) + weight) {
            dist.put(v, dist.get(u) + weight);
            parent.put(v, u);
            if (!open.contains(v) && !closed.contains(v))
                open.add(v);
        }
    }

    protected <N extends Node2D<?,Integer>, G extends Graph<N>> List<N> pathTraceback
            (N source, N target, Map<N,N> parent) {

        List<N> path = new ArrayList<>();
        N last = target;
        while (last != source) {
            path.add(last);
            last = parent.get(last);
        }
        path.add(source);
        Collections.reverse(path);

        return path;
    }
}
