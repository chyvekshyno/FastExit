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
    protected final Map<Agent<FireCellMoore2DStochastic>, Pathfinding<FireCellMoore2DStochastic>> agentsPathfinding;
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
        this.agentsPathfinding = new HashMap<>();
        this.ts = ts;
        processing = new AtomicBoolean(true);
        initAgentsPathfinding(fx.getAgentPaths().keySet());
    }

    protected void initAgentsPathfinding
            (Set<Agent<FireCellMoore2DStochastic>> agents) {
        for (var agent : agents)
            agentsPathfinding.put(agent, choosePathfinding(graph));
    }

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

    protected void stepAgent(Agent<FireCellMoore2DStochastic> agent) {
        agent.move();
    }

    protected void stepAgents() {
        agentsPathfinding.keySet()
                .forEach(this::stepAgent);
    }

    protected Set<FireCellMoore2DStochastic> stepCA() {
        CA.nextState();
        return CA.getChanged();
    }

    protected boolean updatePathCondition(Agent<FireCellMoore2DStochastic> agent) {
        return true;
    }

    protected Thread updatePath(Agent<FireCellMoore2DStochastic> agent) {
        Thread t = new Thread(updatePathRunnable(agent));
        t.start();
        return t;
    }

    protected Thread updatePath(Agent<FireCellMoore2DStochastic> agent,
                              Set<FireCellMoore2DStochastic> changedCost) {

        if (updatePathCondition(agent)) {
            return updatePath(agent);
        }
        return null;
    }

    private void updatePaths() {
        var update = agentsPathfinding.keySet().stream()
                .map(this::updatePath)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        try {
            for (Thread thread : update)
                thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void updatePaths(Set<FireCellMoore2DStochastic> changedCost) {
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

    protected Runnable updatePathRunnable(Agent<FireCellMoore2DStochastic> agent) {
        return () -> agent.updatePath(agentsPathfinding.get(agent), exits.get(0));
    }

    protected boolean isSafe(Agent<FireCellMoore2DStochastic> agent) {
        return exits.contains(agent.getPosition());
    }

    protected void reDraw() {
        agentsPathfinding.keySet().forEach(fx::updatePathFX);
        AreaFX.updateGrid(gridFX,
                agentsPathfinding.keySet().stream().map(Agent::getPosition)
                        .collect(Collectors.toList()),
                exits);
    }
}
