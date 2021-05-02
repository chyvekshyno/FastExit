package com.tuky.diploma.pathfinding;

import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.structures.graph.Node2D;

import java.util.*;

public class AStar {

    public static <N extends Node2D<?, Integer>, G extends Graph<N>> List<N> path (G graph, N start, N end) {
        Map<N, N> cameFrom = new HashMap<>();

        if (!graph.getAdjTable().containsKey(start))
            return null;

        Map<N, Double> gScore = new HashMap<>();
        gScore.put(start, 0.);

        Map<N, Double> fScore = new HashMap<>();
        fScore.put(start, heuristic(start, end));

        Queue<N> openSet = new PriorityQueue<>(Comparator
                                .comparingDouble(fScore::get));
        openSet.add(start);

        N current;
        N neighbour;
        double tentative_gScore;
        while (!openSet.isEmpty()) {
            current = openSet.poll();

            if (current == end)
                return reconstructPath(cameFrom, current);

            for (var tr : graph.getAdjTable().get(current)) {
                if (tr == null)
                    continue;

                neighbour = tr.getEnd();
                tentative_gScore = gScore.get(current) + tr.getWeight();
                if (!gScore.containsKey(neighbour) || tentative_gScore < gScore.get(neighbour)) {
                    cameFrom.put(neighbour, current);
                    gScore.put(neighbour, tentative_gScore);
                    fScore.put(neighbour, tentative_gScore + heuristic(neighbour, end));
                    if (!openSet.contains(neighbour))
                        openSet.add(neighbour);
                }
            }
        }

        return null;
    }

    private static <N extends Node2D<?, Integer>> double heuristic(N node0, N node1) {
        double dx = node0.getCoord().X() - node1.getCoord().X();
        double dy = node0.getCoord().Y() - node1.getCoord().Y();
        return dx*dx + dy*dy;
    }

    private static <N extends Node2D<?,?>> List<N> reconstructPath(Map<N, N> cameFrom, N current) {
        List<N> path = new ArrayList<>();
        path.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(current);
        }
        return path;
    }

}
