package com.tuky.diploma.pathfinding;

import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.structures.graph.Node2D;

import java.util.*;

public class TreeAAStar <N extends Node2D<?, Integer>>
        extends AdaptiveAStar<N>{

    protected int counter;
    protected N source;
    protected Map<N, Integer> generated;
    protected Map<N, Integer> Id;
    protected Map<N, N> pathTree;
    protected Map<Integer, List<Integer>> paths;
    protected Map<Integer, Double> H_max;
    protected Map<Integer, Double> H_min;


    public TreeAAStar(Graph<N> graph) {
        super(graph);
    }


    @Override
    protected void initNode(N node) {
        if (generated.get(node) == 0) {
            dist.put(node, Double.POSITIVE_INFINITY);
            super.initNode(node);
        } else if (generated.get(node) != counter) {
            dist.put(node, Double.POSITIVE_INFINITY);
        }
        generated.put(node, counter);
    }

    @Override
    protected void initStructures() {
        super.initStructures();
        counter = 1;
        generated = new HashMap<>();
        pathTree = new HashMap<>();
        Id = new HashMap<>();
        paths = new HashMap<>();
        H_max = new HashMap<>();    H_max.put(0, -1.);
        H_min = new HashMap<>();
        for (N node : graph.getAdjTable().keySet()) {
            generated.put(node, 0);
            Id.put(node, 0);
        }
    }

    @Override
    protected void initData(N source, N target) {
        initNode(source);
        dist.put(source, 0.);
        open.add(source);

    }

    @Override
    protected void clearData() {
        dist.clear();
        closed.clear();
        open.clear();
    }

    @Override
    protected Map<N, N> algorithm(N source, N target) {
        N current;
        while (!open.isEmpty()) {
            current = open.poll();
            if (current == target || H.get(current) <= H_max.get(Id.get(current))) {
                updateH();
                addPath(current);
                return pathTree;
            }
            process(current, dist, parent, open, closed);
        }
        return null;
    }

    private void addPath(N curr) {
        if (curr != target)
            paths.get(Id.get(curr)).add(counter);

        H_min.put(counter, H.get(curr));
        H_max.put(counter, H.get(source));

        paths.put(counter, new ArrayList<>());
        N next;
        while (curr != source) {
            next = curr;
            curr = parent.get(curr);
            Id.put(curr, counter);
            pathTree.put(curr, next);
        }
    }

    private void removePath(N curr) {
        int pathCurr = Id.get(curr);
        if (H_max.get(pathCurr) > H.get(pathTree.get(curr)))
            H_max.put(pathCurr, H.get(pathTree.get(curr)));

        Queue<Integer> queue = new LinkedList<>();
        for (var pathNext : paths.get(pathCurr)) {
            if (H_max.get(pathCurr) < H_min.get(pathNext)){
                queue.add(pathNext);
                paths.remove(pathNext);
            }
        }
        int path;
        while (!queue.isEmpty()) {
            path = queue.poll();
            if (H_max.get(path) > H_min.get(path)) {
                H_max.put(path, H_min.get(path));
                for (var pathNext : paths.get(path)) {
                    queue.add(pathNext);
                    paths.remove(pathNext);
                }
            }
        }
    }
}
