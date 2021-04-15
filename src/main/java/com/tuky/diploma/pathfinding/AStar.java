package com.tuky.diploma.pathfinding;

import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.structures.graph.Node;

import java.util.*;

public class AStar {

    public static List<Node> path (Graph graph, Node start, Node end) {
        Map<Node, Node> cameFrom = new HashMap<>();

        Map<Node, Double> gScore = new HashMap<>();
        gScore.put(start, 0.);

        Map<Node, Double> fScore = new HashMap<>();
        fScore.put(start, heuristic(start, end));

        Queue<Node> openSet = new PriorityQueue<>(Comparator
                                .comparingDouble(fScore::get));
        openSet.add(start);

        Node current;
        Node neighbour;
        double tentative_gScore;
        while (!openSet.isEmpty()) {
            current = openSet.remove();

            if (current == end)
                return reconstructPath(cameFrom, current);

            for (var tr : graph.getAdjNodes().get(current)) {
                neighbour = tr.getEnd();
                tentative_gScore = gScore.get(current) + tr.getWeight();
                cameFrom.put(neighbour, current);
                gScore.put(neighbour, tentative_gScore);
                fScore.put(neighbour, tentative_gScore + heuristic(neighbour, end));
                if (openSet.contains(neighbour))
                    openSet.add(neighbour);
            }
        }

        return null;
    }


    private static double heuristic(Node node0, Node node1) {
        return 0;
    }

    private static List<Node> reconstructPath(Map<Node, Node> cameFrom, Node current) {
        List<Node> path = new ArrayList<>();
        path.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(current);
        }
        return  path;
    }

}
