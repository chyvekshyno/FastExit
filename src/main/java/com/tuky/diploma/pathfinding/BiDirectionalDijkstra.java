package com.tuky.diploma.pathfinding;

import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.structures.graph.Node;
import com.tuky.diploma.structures.graph.Node2D;

import java.util.*;

public class BiDirectionalDijkstra<N extends Node<?>, G extends Graph<N>>
        extends Dijkstra<N, G> {

    protected Map<N, N> parentB;
    protected Map<N, Double> distB;
    protected Queue<N> openB;
    protected Set<N> closedB;

    public BiDirectionalDijkstra(G graph) {
        super(graph);
    }

    public static <N extends Node2D<?,Integer>, G extends Graph<N>> List<N> path (G graph, N source, N target) {
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
    protected List<N> algorithm(N source, N target) {
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

    protected List<N> pathTraceback(N source, N target, N touch,
                                    Map<N,N> parentF,
                                    Map<N,N> parentB) {

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

    protected List<N> shortestPath
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
