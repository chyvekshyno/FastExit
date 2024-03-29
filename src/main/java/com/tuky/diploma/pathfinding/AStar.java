package com.tuky.diploma.pathfinding;

import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.structures.graph.Node2D;

import java.util.*;

public class AStar <N extends Node2D<?, Integer>>
        extends Dijkstra<N>{

    protected Map<N, Double> H;

    public AStar(Graph<N> graph) {
        super(graph);
    }

    public Map<N, Double> getH() {
        return H;
    }

    public static <N extends Node2D<?,Integer>> Map<N, N> path (Graph<N> graph, N source, N target) {
        return new AStar<>(graph).path(source, target);
    }

    protected double potential(N node, N target) {
        double dx = node.getCoord().X() - target.getCoord().X();
        double dy = node.getCoord().Y() - target.getCoord().Y();
        return Math.sqrt(dx*dx + dy*dy);
    }

    @Override
    protected void process(N curr,
                           Map<N, Double> dist,
                           Map<N, N> parent,
                           Queue<N> open,
                           Set<N> closed) {

        for (var tr : graph.getAdjTable().get(curr)) {
            if (tr == null) continue;

            initNode(tr.getEnd());
            relax(curr, tr.getEnd(), tr.getWeight(), dist, parent, open, closed);
        }
        closed.add(curr);
    }

    @Override
    protected void initData(N source, N target) {
        super.initData(source, target);
        initNode(source);
    }

    protected void initNode(N node) {
        if (H.get(node) < 0)
            H.put(node, potential(node, target));
    }

    @Override
    protected void initStructures() {
        super.initStructures();
        H = new HashMap<>();
        graph.getAdjTable().keySet()
                .forEach(cell -> H.put(cell, Double.NEGATIVE_INFINITY));
    }

    @Override
    protected Comparator<N> openComparator() {
        return Comparator.comparing(node -> dist.get(node) + H.get(node));
    }
}
