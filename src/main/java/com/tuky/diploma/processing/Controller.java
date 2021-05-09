package com.tuky.diploma.processing;

import com.tuky.diploma.structures.cellular.CellularAutomata;
import com.tuky.diploma.structures.cellular.CellularAutomaton;
import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.structures.graph.Node;
import com.tuky.diploma.structures.graph.Node2D;
import javafx.scene.Group;

import java.util.List;

public class Controller
        <N extends Node2D<Double, Integer> & CellularAutomaton, G extends Graph<N>> {

    private G graph;
    private List<Agent<N>> agents;
    private List<N> exits;
    private CellularAutomata<N> CA;

    private int ts;

    public Controller(G graph,
                      List<Agent<N>> agents,
                      List<N> exits,
                      CellularAutomata<N> CA,
                      int ts) {
        this.graph = graph;
        this.agents = agents;
        this.exits = exits;
        this.CA = CA;
        this.ts = ts;
    }

    public void start() {
        while (!agents.isEmpty()) {
            stepAgents();
            stepCA();
            updatePaths();
        }

    }

    // FIXME: 09.05.2021    NEED TO BE DESCRIBED
    public void stop() {}

    // FIXME: 09.05.2021    NEED TO BE DESCRIBED
    public void resume() {}

    // FIXME: 09.05.2021    NEED TO BE DESCRIBED
    public void abort() {}

    private void stepAgents() {

    }

    private void stepCA() {

    }

    private void updatePaths() {

    }

    private void isSafe(Agent<N> agent) {

    }

}
