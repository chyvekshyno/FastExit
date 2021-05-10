package com.tuky.diploma.processing;

import com.tuky.diploma.camodels.FireCellMoore2DHeat;
import com.tuky.diploma.camodels.FireCellMoore2DStochastic;
import com.tuky.diploma.camodels.FireSpreadCAStochastic;
import com.tuky.diploma.pathfinding.*;
import com.tuky.diploma.structures.area.regularnet.RegularNetMoore2D;
import com.tuky.diploma.structures.cellular.CellularAutomata;
import com.tuky.diploma.visual.AreaFX;
import javafx.scene.Group;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ControllerFireMoore2D {

    //  region  Fields
    private RegularNetMoore2D<FireCellMoore2DStochastic> graph;
    private Map<Agent<FireCellMoore2DStochastic>, Polyline> agents;
    private List<FireCellMoore2DStochastic> exits;
    private FireSpreadCAStochastic CA;

    private Pathfinding<FireCellMoore2DStochastic> pathfinding;

    private Map<FireCellMoore2DStochastic, Shape> gridFX;
    private Group group;

    private int ts;
    private AtomicBoolean processing;
    //endregion

    public ControllerFireMoore2D(RegularNetMoore2D<FireCellMoore2DStochastic> graph,
                                 Map<FireCellMoore2DStochastic, Shape> gridFX,
                                 Pathfinding<FireCellMoore2DStochastic> pathfinding,
                                 List<Agent<FireCellMoore2DStochastic>> agents,
                                 List<FireCellMoore2DStochastic> exits,
                                 FireSpreadCAStochastic CA,
                                 int ts) {
        this.graph = graph;
        this.gridFX = gridFX;
        this.pathfinding = pathfinding;
        this.agents = initAgentMap(agents);
        this.exits = exits;
        this.CA = CA;
        this.ts = ts;
        processing = new AtomicBoolean(true);
    }

    private Map<Agent<FireCellMoore2DStochastic>, Polyline> initAgentMap(List<Agent<FireCellMoore2DStochastic>> agents) {
        Map<Agent<FireCellMoore2DStochastic>, Polyline> res = new HashMap<>();
        for (var agent : agents)
            res.put(agent, new Polyline());
        return res;
    }

    public void start() throws InterruptedException {
        updatePaths();
        resume();
    }

    public void stop() {
        processing.set(false);
    }

    public void resume() throws InterruptedException {
        Thread.sleep(ts);
        processing.set(true);
        step();
        while (processing.get() && !agents.isEmpty()) {
            step();
            Thread.sleep(ts);
        }
    }

    // FIXME: 09.05.2021    NEED TO BE DESCRIBED
    public void abort() {}

    private void step() {
        stepAgents();
        stepCA();
        updatePaths();
        reDraw();
    }

    private void stepAgents() {
        try {
            for (var agentpath : agents.entrySet()) {
                agentpath.getKey().move();
                agentpath.getValue().getPoints().remove(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void stepCA() {
        CA.nextState();
    }

    private void updatePaths() {
        agents.keySet().removeIf(this::isSafe);

        List<Thread> threadList = agents.keySet().stream()
                                        .map(this::updatePathRunnable)
                                        .map(Thread::new)
                                        .collect(Collectors.toList());

        threadList.forEach(Thread::start);

        try {
            for (Thread thread : threadList)
                thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        agents.forEach((key, value) -> {
            value.getPoints().clear();
            value.getPoints().addAll(
                    AreaFX.toJavaFXPathPoints(key.getPath(), key.getPosition())
            );
        });

    }

    private Runnable updatePathRunnable(Agent<FireCellMoore2DStochastic> agent) {
        return () -> agent.updatePath(new TreeAAStar<>(graph), exits.get(0));
    }

    private boolean isSafe(Agent<FireCellMoore2DStochastic> agent) {
        return exits.contains(agent.getPosition());
    }

    private void reDraw() {
        AreaFX.updateGrid(gridFX,
                agents.keySet().stream()
                        .map(Agent::getPosition)
                        .collect(Collectors.toList()),
                exits);
    }

}
