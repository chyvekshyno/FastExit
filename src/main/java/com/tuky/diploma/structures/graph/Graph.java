package com.tuky.diploma.structures.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    protected final Map<Node, List<Transition>> adjNodes;


    public Graph() {
        adjNodes = new HashMap<>();
    }

    public Graph(Map<Node, List<Transition>> adjNodes) {
        this.adjNodes = adjNodes;
    }

    public Map<Node, List<Transition>> getAdjNodes() {
        return adjNodes;
    }

    public void addNode(Node node) {
        adjNodes.putIfAbsent(node, new ArrayList<>());
    }

    public void removeNode(Node node) {
        if (adjNodes.containsKey(node)) {
            removeRelationsOf(node);
            adjNodes.remove(node);
        }
    }

    public void addTransition(Node start, Node end) {
        if (adjNodes.containsKey(start)) {
            adjNodes.get(start).add(new Transition(start, end));
        } else {
            adjNodes.put(start, new ArrayList<>() {{
                add(new Transition(start, end));
            }});
        }
    }


    public boolean removeTransition(Node start, Node end) {
        return adjNodes.get(start)
                .removeIf(tr -> tr.equals(new Transition(start, end)));
    }

    protected void removeRelationsOf(Node node) {
        for (List<Transition> list : adjNodes.values()) {
            list.removeIf(tr -> tr.getEnd() == node);
        }
    }
}
