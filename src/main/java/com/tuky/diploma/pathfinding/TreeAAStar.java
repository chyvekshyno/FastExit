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


    public Set<N> getClosed() {
        return closed;
    }
    public int getCounter() {
        return counter;
    }

    public Map<N, Integer> getId() {
        return Id;
    }

    public Map<N, N> getPathTree() {
        return pathTree;
    }

    public Map<Integer, List<Integer>> getPaths() {
        return paths;
    }

    public Map<Integer, Double> getH_max() {
        return H_max;
    }

    public Map<Integer, Double> getH_min() {
        return H_min;
    }

    public TreeAAStar(Graph<N> graph) {
        super(graph);
    }


    @Override
    protected void initNode(N node) {
        if (generated.get(node) == 0) {
            dist.put(node, Double.POSITIVE_INFINITY);
            H.put(node, potential(node, target));
        } else if (generated.get(node) != counter) {
            dist.put(node, Double.POSITIVE_INFINITY);
        }
        generated.put(node, counter);
    }

    @Override
    protected void initStructures() {
        super.initStructures();
        counter = 0;
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
        counter++;
        this.source = source;
        initNode(source);
        dist.put(source, 0.);
        open.add(source);
        pathTree.put(target, null);
    }

    @Override
    protected void clearData() {
        dist = new HashMap<>();
        closed.clear();
        open = new PriorityQueue<>(openComparator());
        parent.clear();
    }

    @Override
    protected Map<N, N> algorithm(N source, N target) {
        N current;
        int i = 1;
        while (!open.isEmpty()) {
            current = open.poll();
            if (current == target) {
                System.out.println("\n--------To TARGET--------");
                System.out.println("counter:\t" + getCounter());
                System.out.println("coords =\t" + current.getCoord().X() + "," + current.getCoord().Y());
                System.out.println("iterations:\t" + i);
                updateH(current);
                addPath(current);
                return pathTree;
            }
            if (H.get(current) <= H_max.get(Id.get(current))) {
                System.out.println("\n--------To TREE--------");
                System.out.println("counter:\t" + getCounter());
                System.out.print("coords =\t" + current.getCoord().X() + "_" + current.getCoord().Y());
                N tmp = pathTree.get(current);
                for (int j = 0; j < 10; j++){
                    if (tmp == null) {
                        System.out.print(" -> null");
                        break;
                    }
                    System.out.print(" -> " + tmp.getCoord().X() + "_" + tmp.getCoord().Y());
                    if (tmp == target) {
                        System.out.print(" -> " + tmp.getCoord().X() + "_" + tmp.getCoord().Y() + " = target");
                        break;
                    }
                    tmp = pathTree.get(tmp);
                }
                System.out.println("");
                System.out.println("iterations:\t" + i);
                System.out.println("H_current:\t" + H.get(current));
                System.out.println("Id_current:\t" + Id.get(current));
                System.out.println("H_max_current:\t" + H_max.get(Id.get(current)));
                updateH(current);
                addPath(current);
                return pathTree;
            }
            process(current, dist, parent, open, closed);
            i++;
        }
        return null;
    }

    private void addPath(N curr) {
//        System.out.println("--------------Add-----------------");
//        System.out.println("coords =\t" + curr.getCoord().X() + "," + curr.getCoord().Y());

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

    public void removePath(N curr) {
        if (pathTree.get(curr) == null)    //  remove's target
            return;

        int pathCurr = Id.get(curr);
        if (H_max.get(pathCurr) > H.get(pathTree.get(curr)))
            H_max.put(pathCurr, H.get(pathTree.get(curr)));

        Queue<Integer> queue = new LinkedList<>();
        for (var pathNext : paths.get(pathCurr))
            if (H_max.get(pathCurr) < H_min.get(pathNext))
                queue.add(pathNext);

        paths.get(pathCurr).removeAll(queue);

        int path;
        while (!queue.isEmpty()) {
            path = queue.poll();
            if (H_max.get(path) > H_min.get(path)) {
                H_max.put(path, 0.);
                H_min.put(path, 0.);
                queue.addAll(paths.get(path));
                paths.get(path).clear();
            }
        }
    }
}
