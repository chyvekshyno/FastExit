package com.tuky.diploma.pathfinding;

import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.structures.graph.Node;
import com.tuky.diploma.structures.graph.Node2D;

import java.util.*;

public class BiDirectionalDijkstra<N extends Node<?>>
        extends Dijkstra<N> {

    protected Map<N, N> parentB;
    protected Map<N, Double> distB;
    protected Queue<N> openB;
    protected Set<N> closedB;

    public BiDirectionalDijkstra(Graph<N> graph) {
        super(graph);
    }

    public static <N extends Node2D<?,Integer>> Map<N, N> path (Graph<N> graph, N source, N target) {
        return new BiDirectionalDijkstra<>(graph).path(source, target);
    }

    @Override
    protected void initStructures() {
        super.initStructures();
        parentB = new HashMap<>();
        distB   = new HashMap<>();
        openB   = new PriorityQueue<>(Comparator.comparing(distB::get));
        closedB = new HashSet<>();
    }

    @Override
    protected void initData(N source, N target) {
        parent .put(source, null);
        parentB.put(target, null);

        open .add(source);
        openB.add(target);

        dist .put(source, 0.);
        distB.put(target, 0.);
    }

    @Override
    protected Map<N, N> algorithm(N source, N target) {
        N u, v;
        while (!open.isEmpty() && !openB.isEmpty()) {
            u = open.poll();
            process(u, dist, parent, open, closed);
            if (closedB.contains(u))
                return pathTraceback(source, target, u, parent, parentB);

            v = openB.poll();
            process(v, distB, parentB, openB, closedB);
            if (closed.contains(v))
                return pathTraceback(source, target, v, parent, parentB);
        }
        return null;
    }

    protected Map<N, N> pathTraceback(N source, N target, N touch,
                                    Map<N,N> parentF,
                                    Map<N,N> parentB) {

        Map<N, N> path = new HashMap<>();
        N curr = touch;
        N prev = parentF.get(curr);
        while (curr != source) {
            path.put(prev, curr);
            curr = prev;
            prev = parentF.get(prev);
        } path.put(prev, curr);
        path.putAll(parentB);
        return path;
    }
}
