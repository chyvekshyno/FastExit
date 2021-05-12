package com.tuky.diploma.processing;

import com.sun.source.tree.Tree;
import com.tuky.diploma.camodels.FireCellMoore2DStochastic;
import com.tuky.diploma.camodels.FireSpreadCAStochastic;
import com.tuky.diploma.pathfinding.*;
import com.tuky.diploma.structures.area.regularnet.RegularNetMoore2D;
import com.tuky.diploma.structures.graph.Moore2D;
import com.tuky.diploma.structures.graph.Node2D;
import com.tuky.diploma.structures.graph.NodeMoore2D;
import com.tuky.diploma.structures.graph.Transition;
import com.tuky.diploma.visual.AreaFX;
import com.tuky.diploma.visual.ControllerFX;
import javafx.scene.shape.Shape;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ControllerFireMoore2D{

    //  region  Fields
    private final RegularNetMoore2D<FireCellMoore2DStochastic> graph;
    private final Map<Agent<FireCellMoore2DStochastic>, Pathfinding<FireCellMoore2DStochastic>> agentsPathfinding;
    private final List<FireCellMoore2DStochastic> exits;
    private final FireSpreadCAStochastic CA;
    private final ControllerFX fx;

    private final Map<FireCellMoore2DStochastic, Shape> gridFX;

    private int ts;
    private final AtomicBoolean processing;
    //endregion

    public ControllerFireMoore2D(ControllerFX fx,
                                 List<FireCellMoore2DStochastic> exits,
                                 FireSpreadCAStochastic CA,
                                 int ts) {
        this.fx = fx;
        this.graph = fx.getGrid();
        this.exits = exits;
        this.CA = CA;
        this.gridFX = fx.getGridMap();
        this.agentsPathfinding = initAgentsPathfinding(fx.getAgentPaths().keySet());
        this.ts = ts;
        processing = new AtomicBoolean(true);
    }

    private Map<Agent<FireCellMoore2DStochastic>, Pathfinding<FireCellMoore2DStochastic>> initAgentsPathfinding
            (Set<Agent<FireCellMoore2DStochastic>> agents) {
        Map<Agent<FireCellMoore2DStochastic>, Pathfinding<FireCellMoore2DStochastic>> res = new HashMap<>();
        for (var agent : agents)
            res.put(agent, new TreeAAStar<>(graph));
        return res;
    }

    public void start() throws InterruptedException {
        reDraw();
        resume();
    }

    public void stop() {
        processing.set(false);
    }

    public void resume() throws InterruptedException {
        processing.set(true);
        Thread.sleep(ts);
        while (processing.get() && !agentsPathfinding.isEmpty()) {
            step();
            Thread.sleep(ts);
        }
    }

    public void abort() {
        processing.set(false);
        gridFX.clear();
        agentsPathfinding.clear();
        exits.clear();
    }

    public void step() {
        var changed = stepCA();
        updatePaths(changed);
        stepAgents();
        reDraw();
        agentsPathfinding.keySet().removeIf(this::isSafe);
    }

    private void stepAgent(Agent<FireCellMoore2DStochastic> agent) {
        agent.move();

        Transition<FireCellMoore2DStochastic> tr;
        System.out.println("---------------------------");
        for (int i = 0; i < Moore2D.NEIGHBOURS_COUNT; i++) {
            tr = graph.getAdjTable().get(agent.getPosition()).get(i);
            if (tr == null) {
                System.out.println("vec("+i+"):\t" + null);
                continue;
            }
            System.out.println("vec("+i+"):\t" + tr.getWeight());
        }
        System.out.println("---------------------------");
    }

    private void stepAgents() {
        agentsPathfinding.keySet()
                .forEach(this::stepAgent);
    }

    private Set<Transition<FireCellMoore2DStochastic>> stepCA() {
        CA.nextState();
        return CA.getChanged();
    }

    protected boolean updatePathCondition(Agent<FireCellMoore2DStochastic> agent) {
        return true;
    }

    protected boolean updatePathConditionTreeAA(Agent<FireCellMoore2DStochastic> agent) {
        var pathfinding = (TreeAAStar<FireCellMoore2DStochastic>) agentsPathfinding.get(agent);
        double h_curr = pathfinding.getH()
                        .get(agent.getPosition());
        double H_max = pathfinding.getH_max()
                        .get(pathfinding.getId()
                            .get(agent.getPosition()));

        System.out.println("=============================");
        System.out.println("h_curr: " + h_curr);
        System.out.println("H_max: " + H_max);

        return h_curr >= H_max;
    }

    private void updateTreeAAStar(TreeAAStar<FireCellMoore2DStochastic> pathfinding,
                                  Set<Transition<FireCellMoore2DStochastic>> changedCost) {
        for (Transition<FireCellMoore2DStochastic> tr : changedCost)
            if (pathfinding.getPathTree().get(tr.getStart()) == tr.getEnd())
                pathfinding.removePath(tr.getStart());
    }

    private Thread updatePath(Agent<FireCellMoore2DStochastic> agent,
                              Set<Transition<FireCellMoore2DStochastic>> changedCost) {
        if (agentsPathfinding.get(agent) instanceof TreeAAStar) {
            updateTreeAAStar(
                    (TreeAAStar<FireCellMoore2DStochastic>) agentsPathfinding.get(agent),
                    changedCost
            );

            if (updatePathConditionTreeAA(agent)) {
                Thread t = new Thread(updatePathRunnable(agent));
                t.start();
                return t;
            }
        }
        if (updatePathCondition(agent)) {
            Thread t = new Thread(updatePathRunnable(agent));
            t.start();
            return t;
        }


        return null;
    }

    private void updatePaths(Set<Transition<FireCellMoore2DStochastic>> changedCost) {
        var update = agentsPathfinding.keySet().stream()
                .map(agent -> updatePath(agent, changedCost))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        try {
            for (Thread thread : update)
                thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Runnable updatePathRunnable(Agent<FireCellMoore2DStochastic> agent) {
        return () -> agent.updatePath(agentsPathfinding.get(agent), exits.get(0));
    }

    private boolean isSafe(Agent<FireCellMoore2DStochastic> agent) {
        return exits.contains(agent.getPosition());
    }

    private void reDraw() {
        agentsPathfinding.keySet().forEach(fx::updatePathFX);
        AreaFX.updateGrid(gridFX,
                agentsPathfinding.keySet().stream().map(Agent::getPosition)
                        .collect(Collectors.toList()),
                exits);
    }
}
