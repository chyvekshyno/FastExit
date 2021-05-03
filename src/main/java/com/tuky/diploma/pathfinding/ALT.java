package com.tuky.diploma.pathfinding;

import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.structures.graph.Node2D;

import java.util.List;
import java.util.Map;

public class ALT extends AStar {

    private final Map<Node2D<?,?>
                    ,Map<? extends Node2D<?,?>, Double>> landmarks;

    protected <N extends Node2D<?, Integer>> ALT
            (N target, Map<Node2D<?,?>, Map<? extends Node2D<?,?>, Double>> landmarks) {
        super(target);
        this.landmarks = landmarks;
    }

    public static <N extends Node2D<?,Integer>, G extends Graph<N>> List<N> path(G graph, N source, N target) {
        return new ALT(target, genLandmarks(graph))._path(graph, source, target);
    }

    public static <N extends Node2D<?,Integer>, G extends Graph<N>> List<N> path
            (G graph, Map<Node2D<?,?>, Map<? extends Node2D<?,?>, Double>> landmarks, N source, N target) {
        return new ALT(target, landmarks)._path(graph, source, target);
    }

    @Override
    protected <N extends Node2D<?, Integer>> double potential(N node, N target) {
        return landmarks.values().stream()
                .mapToDouble(map -> Math.abs(map.get(node) - map.get(target)))
                .max().orElse(0);
    }

    // FIXME: 03.05.2021
    private static  <G extends Graph<?>> Map<Node2D<?,?>, Map<? extends Node2D<?,?>, Double>> genLandmarks(G graph) {
        return null;
    }
}
