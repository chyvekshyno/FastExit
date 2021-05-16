package com.tuky.diploma.processing;

import com.tuky.diploma.camodels.FireCellMoore2DStochastic;
import com.tuky.diploma.camodels.FireSpreadCAStochastic;
import com.tuky.diploma.pathfinding.*;
import com.tuky.diploma.structures.area.regularnet.RegularNetMoore2D;
import com.tuky.diploma.structures.graph.*;
import com.tuky.diploma.visual.AreaFX;
import com.tuky.diploma.visual.ControllerFX;
import javafx.scene.shape.Shape;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ControllerFire{

    //  region  Fields
    protected final RegularNetMoore2D<FireCellMoore2DStochastic> graph;
//    protected final Map<Agent<FireCellMoore2DStochastic>, Pathfinding<FireCellMoore2DStochastic>> agentsPathfinding;
    protected final Map<FireCellMoore2DStochastic, Pathfinding<FireCellMoore2DStochastic>> pathfinders;
    protected final Map<Agent<FireCellMoore2DStochastic>, Pathfinding<FireCellMoore2DStochastic>> agents;
    protected final List<FireCellMoore2DStochastic> exits;
    protected final FireSpreadCAStochastic CA;
    protected final ControllerFX fx;

    protected final Map<FireCellMoore2DStochastic, Shape> gridFX;

    protected int ts;
    protected final AtomicBoolean processing;
    //endregion

    public ControllerFire(ControllerFX fx,
                          List<FireCellMoore2DStochastic> exits,
                          FireSpreadCAStochastic CA,
                          int ts) {
        this.fx = fx;
        this.graph = fx.getGrid();
        this.exits = exits;
        this.CA = CA;
        this.gridFX = fx.getGridMap();
//        this.agentsPathfinding = new HashMap<>();
        this.pathfinders = new HashMap<>();
        this.agents = new HashMap<>();
        this.ts = ts;
        processing = new AtomicBoolean(true);

        initPathfinders(exits);
        initAgents(fx.getAgentPaths().keySet());

//        initAgentsPathfinding(fx.getAgentPaths().keySet());
    }

    protected void initPathfinders(List<FireCellMoore2DStochastic> exits) {
        exits.forEach(exit -> pathfinders.put(exit, choosePathfinding(graph)));
    }

    protected void initAgents(Set<Agent<FireCellMoore2DStochastic>> agents) {
        agents.forEach(agent -> this.agents.put(agent, null));
    }


//    protected void initAgentsPathfinding
//            (Set<Agent<FireCellMoore2DStochastic>> agents) {
//        for (var agent : agents)
//            agentsPathfinding.put(agent, choosePathfinding(graph));
//    }

    protected Pathfinding<FireCellMoore2DStochastic> choosePathfinding
            (Graph<FireCellMoore2DStochastic> graph) {
        return new AStar<>(graph);
    }

    public void start() throws InterruptedException {
        updatePaths();
        reDraw();
        resume();
    }

    public void stop() {
        processing.set(false);
    }

    public void resume() throws InterruptedException {
        processing.set(true);
        Thread.sleep(ts);
        while (processing.get() && !agents.isEmpty()) {
            step();
            Thread.sleep(ts);
        }
    }

    public void abort() {
        processing.set(false);
        gridFX.clear();
        agents.clear();
        pathfinders.clear();
        exits.clear();
    }

    public void step() {
        var changed = stepCA();
        updatePaths(changed);
        stepAgents();
        reDraw();
        agents.keySet().removeIf(this::isSafe);
    }

    protected void stepAgent(Agent<FireCellMoore2DStochastic> agent) {
        agent.move();
    }

    protected void stepAgents() {
        agents.keySet().forEach(this::stepAgent);
    }

    protected Set<FireCellMoore2DStochastic> stepCA() {
        CA.nextState();
        return CA.getChanged();
    }

    protected boolean updatePathCondition(Agent<FireCellMoore2DStochastic> agent,
                                          Set<FireCellMoore2DStochastic> changedCost) {

        for (var changed : changedCost) {
            if (agent.getPath().containsKey(changed))
                return true;
        }
        return false;
    }

    private void updatePaths() {
        for (var agent : agents.keySet()) {
            List<Thread> update = pathfinders.keySet().stream()
                    .map(exit -> updatePath(agent, exit))
                    .collect(Collectors.toList());

            System.out.println("threads:\t"+update.size());
            try {
                for (Thread thread : update)
                    thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            choosePath(agent);
        }
    }

    protected void updatePaths(Set<FireCellMoore2DStochastic> changedCost) {
        for (var agent : agents.keySet()) {
            if (updatePathCondition(agent, changedCost)) {
                List<Thread> update = pathfinders.keySet().stream()
                                        .map(exit -> updatePath(agent, exit))
                                        .collect(Collectors.toList());
                try {
                    for (Thread thread : update)
                        thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                choosePath(agent);
            }
        }
    }

    protected Thread updatePath(Agent<FireCellMoore2DStochastic> agent,
                                FireCellMoore2DStochastic exit) {
        Thread t = new Thread(updatePathRunnable(agent, exit));
        t.start();
        return t;
    }

    protected Runnable updatePathRunnable(Agent<FireCellMoore2DStochastic> agent,
                                          FireCellMoore2DStochastic exit) {
        return () -> pathfinders.get(exit).path(agent.getPosition(), exit);
    }

    protected void choosePath(Agent<FireCellMoore2DStochastic> agent) {
        var bestPF = pathfinders.values().stream()
                .min(Comparator.comparing(Pathfinding::getPathLen))
                .orElseThrow();

        agents.replace(agent, bestPF);
        agent.updatePath(bestPF.getPath());
        System.out.println("Success");
    }

    protected boolean isSafe(Agent<FireCellMoore2DStochastic> agent) {
        return exits.contains(agent.getPosition());
    }

    protected void reDraw() {
        agents.keySet().forEach(fx::updatePathFX);
        AreaFX.updateGrid(gridFX,
                agents.keySet().stream().map(Agent::getPosition)
                        .collect(Collectors.toList()),
                exits);
    }
}
