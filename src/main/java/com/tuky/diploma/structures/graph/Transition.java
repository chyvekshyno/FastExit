package com.tuky.diploma.structures.graph;

public class Transition {
    private final Node start;
    private final Node end;
    private double weight;

    public Transition(Node start, Node end) {
        this(start, end, 0);
    }

    public Transition(Node start, Node end, double weight) {
        this.start = start;
        this.end = end;
        this.weight = weight;
    }

    public Node getStart() {
        return start;
    }

    public Node getEnd() {
        return end;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)    return false;
        if (obj instanceof Transition)
            return equalsTo((Transition) obj);
        return false;
    }

    private boolean equalsTo(Transition transition) {
        return this.start == transition.start &&
                this.end == transition.end;
    }
}
