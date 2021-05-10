package com.tuky.diploma.pathfinding;

import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.structures.graph.Node2D;

import java.util.List;
import java.util.Map;

public class ALT <N extends Node2D<?, Integer>>
        extends AStar<N> {

    private final Map<N,Map<N, Double>> landmarks;

    public ALT(Graph<N> graph) {
        super(graph);
        this.landmarks = genLandmarks(graph);
    }

    public ALT (Graph<N> graph, Map<N, Map<N, Double>> landmarks) {
        super(graph);
        this.landmarks = landmarks;
    }

    public static <N extends Node2D<?,Integer>> Map<N, N> path(Graph<N> graph, N source, N target) {
        return new ALT<>(graph).path(source, target);
    }

    public static <N extends Node2D<?,Integer>> Map<N, N> path
            (Graph<N> graph, Map<N, Map<N, Double>> landmarks, N source, N target) {
        return new ALT<>(graph, landmarks).path(source, target);
    }

    @Override
    protected double potential(N node, N target) {
        return landmarks.values().stream()
                .mapToDouble(map -> Math.abs(map.get(node) - map.get(target)))
                .max().orElse(0);
    }

    // FIXME: 03.05.2021
    public static <N extends Node2D<?,Integer>> Map<N, Map<N, Double>> genLandmarks(Graph<N> graph) {
        return null;
    }
}
