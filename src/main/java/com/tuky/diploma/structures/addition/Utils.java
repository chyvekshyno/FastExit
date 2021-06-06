package com.tuky.diploma.structures.addition;

import com.tuky.diploma.processing.Agent;
import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.structures.graph.Node2D;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Utils{

    public static int flipCoin(){
        Random rand = new Random();
        return rand.nextInt(2);
    }

    public static <N extends Node2D<?, Integer>> List<Agent<N>> randomAgents(List<N> nodes, int number) {
        return new Random().ints(0, nodes.size())
                .distinct()
                .limit(number)
                .mapToObj(i -> new Agent<>(nodes.get(i)) )
                .collect(Collectors.toList());

    }

    public static <N extends Node2D<?, Integer> & Risk> double risk(N node, Map<N, List<N>> covered, double radius) {
        double risk = 0.;
        for (var damageNode : covered.get(node))
            risk += Utils.damage(node, damageNode, radius);
        return risk;
    }

    public static <N extends Node2D<?, Integer>> List<N> coveredBy(N node, Graph<N> graph, double radius) {
        List<N> damageNodes = new ArrayList<>();
        Queue<N> queue = new LinkedList<>();

        for (var tr : graph.getAdjTable().get(node)) {
            if (tr == null) continue;
            queue.add(tr.getEnd());
        }

        N curr;
        double distCurr;
        while (!queue.isEmpty()) {
            curr = queue.poll();
            distCurr = distEuclidean(node, curr);
//            System.out.println("queue count:\t" + queue.size());

            if (distCurr < radius) {
                damageNodes.add(curr);
                for (var tr : graph.getAdjTable().get(curr)) {
                    if (tr == null || damageNodes.contains(tr.getEnd()) || queue.contains(tr.getEnd())
                            || distCurr + distEuclidean(curr, tr.getEnd()) > radius)
                        continue;

                    queue.add(tr.getEnd());
                }
            }
        }

        return damageNodes;
    }

    public static  <N extends Node2D<?, Integer>> double distEuclidean(N node0, N node1) {
        double dx = node0.getCoord().X() - node1.getCoord().X();
        double dy = node0.getCoord().Y() - node1.getCoord().Y();
        return Math.sqrt(dx*dx + dy*dy);
    }

    public static <N extends Node2D<?, Integer> & Risk> double damage(N curr, N danger, double radius) {
        return danger.getMaxDamage() *
                (1 - distEuclidean(curr, danger) / radius);
    }
}