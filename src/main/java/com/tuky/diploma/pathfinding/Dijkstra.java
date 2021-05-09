package com.tuky.diploma.pathfinding;

import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.structures.graph.Node;

import java.util.*;

public class Dijkstra <N extends Node<?>, G extends Graph<N>>
        implements Pathfinding<N> {

    protected final G graph;
    protected Map<N, N> parent;
    protected Map<N, Double> dist;
    protected Queue<N> open;
    protected Set<N> closed;

    public Dijkstra(G graph) {
        this.graph = graph;
    }

    public static <N extends Node<?>, G extends Graph<N>> List<N> path (G graph, N source, N target) {
        return new Dijkstra<N, G>(graph).path(source, target);
    }

    public static <N extends Node<?>, G extends Graph<N>> Map<N, Double> distTree (G graph, N source) {
        return new Dijkstra<N, G>(graph)._distTree(source);
    }

    public List<N> path(N source, N target) {
        initStructures();
        initData(source, target);
        return algorithm(source, target);
    }

    protected Map<N, Double> _distTree (N source) {
        initStructures();
        initData(source, null);

        //  start an algorithm
        N current;
        while (!open.isEmpty()) {
            current = open.poll();
            process(current, dist, parent, open, closed);
        }
        return dist;
    }

    protected void initStructures() {
        parent  = new HashMap<>();
        dist    = new HashMap<>();
        open    = new PriorityQueue<>(Comparator.comparing(dist::get));
        closed  = new HashSet<>();
    }

    protected void initData(N source, N target) {
        parent.put(source, null);
        open.add(source);
        dist.put(source, 0.);
    }

    protected List<N> algorithm(N source, N target) {
        N current;
        while (!open.isEmpty()) {
            current = open.poll();
            process(current, dist, parent, open, closed);
            if (current == target)
                return pathTraceback(source, current, parent);
        }
        return null;
    }

    protected void process(N current,
                           Map<N, Double> dist,
                           Map<N, N> parent,
                           Queue<N> open,
                           Set<N> closed) {
        for (var tr : graph.getAdjTable().get(current)) {
            if (tr == null)
                continue;
            relax(current, tr.getEnd(), tr.getWeight(), dist, parent, open, closed);
        }
        closed.add(current);
    }

    protected void relax(N curr, N neighbour, double weight,
                         Map<N, Double> dist,
                         Map<N, N> parent,
                         Queue<N> open,
                         Set<N> closed) {
        if (!dist.containsKey(neighbour) || dist.get(neighbour) > dist.get(curr) + weight) {
            dist.put(neighbour, dist.get(curr) + weight);
            parent.put(neighbour, curr);
            if (!open.contains(neighbour) && !closed.contains(neighbour))
                open.add(neighbour);
        }
    }

    protected List<N> pathTraceback(N source, N target, Map<N,N> parent) {
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
