package com.tuky.diploma.structures.graph;

public class Transition<N extends Node<?>> {
    private final N start;
    private final N end;

    private double weight;

    public Transition(N start, N end) {
        this(start, end, 1);
    }

    public Transition(N start, N end, double weight) {
        this.start = start;
        this.end = end;
        this.weight = weight;
    }

    public N getStart() {
        return start;
    }

    public N getEnd() {
        return end;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)    return false;
        if (obj instanceof Transition<?>)
            return equalsTo((Transition<?>) obj);
        return false;
    }

    private boolean equalsTo(Transition<?> transition) {
        return this.start == transition.start &&
                this.end == transition.end;
    }
}
