package com.tuky.diploma.structures.graph;

import java.util.ArrayList;
import java.util.List;

public class Node implements Comparable<Node> {
    private int value;

    public Node() { }

    public Node(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public int compareTo(Node node) {
        return Integer.compare(this.value, node.getValue());
    }
}
