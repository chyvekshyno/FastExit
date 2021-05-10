package com.tuky.diploma.pathfinding;

import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.structures.graph.Node;

import java.util.*;

public class Dijkstra <N extends Node<?>>
        implements Pathfinding<N> {

    protected final Graph<N> graph;
    protected Map<N, N> parent;
    protected Map<N, Double> dist;
    protected Queue<N> open;
    protected Set<N> closed;

    public Dijkstra(Graph<N> graph) {
        this.graph = graph;
        initStructures();
    }

    public static <N extends Node<?>> Map<N, N> path (Graph<N> graph, N source, N target) {
        return new Dijkstra<N>(graph).path(source, target);
    }

    public static <N extends Node<?>> Map<N, Double> distTree (Graph<N> graph, N source) {
        return new Dijkstra<N>(graph)._distTree(source);
    }

    public Map<N, N> path(N source, N target) {
        initData(source, target);
        var path = algorithm(source, target);
        clearData();
        return path;
    }

    protected Map<N, Double> _distTree (N source) {
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

    protected void clearData(){
        parent.clear();
        dist  .clear();
        open  .clear();
        closed.clear();
    }

    protected Map<N, N> algorithm(N source, N target) {
        N current;
        while (!open.isEmpty()) {
            current = open.poll();
            if (current == target)
                return pathTraceback(source, current, parent);

            process(current, dist, parent, open, closed);
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
        if (relaxCondition(curr, neighbour, weight)) {
            dist.put(neighbour, dist.get(curr) + weight);
            parent.put(neighbour, curr);
            if (!open.contains(neighbour) && !closed.contains(neighbour))
                open.add(neighbour);
        }
    }

    protected Map<N, N> pathTraceback(N source, N target, Map<N,N> parent) {
        Map<N, N> path = new HashMap<>();
        N last = parent.get(target);
        while (last != source) {
            path.put(last, target);
            target = last;
            last = parent.get(last);
        }
        path.put(source, target);
        return path;
    }

    protected boolean relaxCondition(N curr, N next, double weight) {
        return  !dist.containsKey(next) || dist.get(next) > dist.get(curr) + weight;
    }
}
