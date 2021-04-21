package com.tuky.diploma.structures.graph;

import java.util.ArrayList;
import java.util.List;

public class Node<V extends Comparable<V>> implements Comparable<Node<V>> {
    private V value;

    public Node() {
        value = null;
    }

    public Node(V value) {
        this.value = value;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public int compareTo(V value) {
        return this.value.compareTo(value);
    }

    @Override
    public int compareTo(Node<V> another) {
        return value.compareTo(another.value);
    }
}
