package com.tuky.diploma.structures.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph<N extends Node<? extends Comparable<?>>> {
    protected final Map<N, List<Transition<N>>> adjNodes;


    public Graph() {
        adjNodes = new HashMap<>();
    }

    public Graph(Map<N, List<Transition<N>>> adjNodes) {
        this.adjNodes = adjNodes;
    }

    public Map<N, List<Transition<N>>> getAdjTable() {
        return adjNodes;
    }

    public void addNode(N node) {
        adjNodes.putIfAbsent(node, new ArrayList<>());
    }

    public void removeNode(N node) {
        if (adjNodes.containsKey(node)) {
            removeRelationsOf(node);
            adjNodes.remove(node);
        }
    }

    public void addTransition(N start, N end) {
        if (adjNodes.containsKey(start)) {
            adjNodes.get(start).add(new Transition<N>(start, end));
        } else {
            adjNodes.put(start, new ArrayList<>() {{
                add(new Transition<N>(start, end));
            }});
        }
    }


    public boolean removeTransition(N start, N end) {
        return adjNodes.get(start)
                .removeIf(tr -> tr.equals(new Transition<N>(start, end)));
    }

    protected void removeRelationsOf(N node) {
        for (List<Transition<N>> list : adjNodes.values()) {
            list.removeIf(tr -> tr.getEnd() == node);
        }
    }
}
