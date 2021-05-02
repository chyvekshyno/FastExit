package com.tuky.diploma.structures.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph<N extends Node<? extends Comparable<?>>> {
    protected final Map<N, List<Transition<N>>> adjTable;


    public Graph() {
        adjTable = new HashMap<>();
    }

    public Graph(Map<N, List<Transition<N>>> adjNodes) {
        this.adjTable = adjNodes;
    }

    public Map<N, List<Transition<N>>> getAdjTable() {
        return adjTable;
    }

    public void addNode(N node) {
        adjTable.putIfAbsent(node, new ArrayList<>());
    }

    public void removeNode(N node) {
        if (adjTable.containsKey(node)) {
            removeRelationsOf(node);
            adjTable.remove(node);
        }
    }

    public void addTransition(N start, N end) {
        if (adjTable.containsKey(start)) {
            adjTable.get(start).add(new Transition<>(start, end));
        } else {
            adjTable.put(start, new ArrayList<>() {{
                add(new Transition<>(start, end));
            }});
        }
    }


    public boolean removeTransition(N start, N end) {
        return adjTable.get(start)
                .removeIf(tr -> tr.equals(new Transition<>(start, end)));
    }

    protected void removeRelationsOf(N node) {
        for (List<Transition<N>> list : adjTable.values()) {
            list.removeIf(tr -> tr.getEnd() == node);
        }
    }
}
