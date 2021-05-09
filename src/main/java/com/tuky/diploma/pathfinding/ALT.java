package com.tuky.diploma.pathfinding;

import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.structures.graph.Node2D;

import java.util.List;
import java.util.Map;

public class ALT <N extends Node2D<?, Integer>, G extends Graph<N>>
        extends AStar<N, G> {

    private final Map<Node2D<?,?>
                    ,Map<? extends Node2D<?,?>, Double>> landmarks;

    public ALT(G graph) {
        super(graph);
        this.landmarks = genLandmarks(graph);
    }

    public ALT (G graph, Map<Node2D<?,?>, Map<? extends Node2D<?,?>, Double>> landmarks) {
        super(graph);
        this.landmarks = landmarks;
    }

    public static <N extends Node2D<?,Integer>, G extends Graph<N>> List<N> path(G graph, N source, N target) {
        return new ALT<>(graph).path(source, target);
    }

    public static <N extends Node2D<?,Integer>, G extends Graph<N>> List<N> path
            (G graph, Map<Node2D<?,?>, Map<? extends Node2D<?,?>, Double>> landmarks, N source, N target) {
        return new ALT<>(graph, landmarks).path(source, target);
    }

    @Override
    protected double potential(N node, N target) {
        return landmarks.values().stream()
                .mapToDouble(map -> Math.abs(map.get(node) - map.get(target)))
                .max().orElse(0);
    }

    // FIXME: 03.05.2021
    private static  <G extends Graph<?>> Map<Node2D<?,?>, Map<? extends Node2D<?,?>, Double>> genLandmarks(G graph) {
        return null;
    }
}
