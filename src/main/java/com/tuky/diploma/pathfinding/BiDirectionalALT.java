package com.tuky.diploma.pathfinding;

import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.structures.graph.Node2D;

import java.util.List;
import java.util.Map;

public class BiDirectionalALT <N extends Node2D<?, Integer>, G extends Graph<N>>
        extends BiDirectionalAStar<N, G>{

    private final Map<N, Map<N, Double>> landmarks;

    public BiDirectionalALT(G graph) {
        super(graph);
        landmarks = genLandmarks(graph);
    }

    protected BiDirectionalALT(G graph, Map<N, Map<N, Double>> landmarks) {
        super(graph);
        this.landmarks = landmarks;
    }

    public static <N extends Node2D<?,Integer>, G extends Graph<N>> List<N> path (G graph, N source, N target) {
        return new BiDirectionalALT<>(graph).path(source, target);
    }

    public static <N extends Node2D<?,Integer>, G extends Graph<N>> List<N> path(G graph,
                                                                                 Map<N, Map<N, Double>> landmarks,
                                                                                 N source, N target) {
        return new BiDirectionalALT<>(graph, landmarks).path(source, target);
    }

    @Override
    protected double potential(N node, N target) {
        return landmarks.values().stream()
                .mapToDouble(map -> Math.abs(map.get(node) - map.get(target)))
                .max().orElse(0);
    }

    // FIXME: 03.05.2021
    private static <N extends Node2D<?, Integer>, G extends Graph<N>>  Map<N, Map<N, Double>> genLandmarks(G graph) {
        return null;
    }
}
