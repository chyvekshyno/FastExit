package com.tuky.diploma.processing;

import com.tuky.diploma.camodels.FireCellMoore2DStochastic;
import com.tuky.diploma.camodels.FireSpreadCAStochastic;
import com.tuky.diploma.pathfinding.Pathfinding;
import com.tuky.diploma.pathfinding.TreeAAStar;
import com.tuky.diploma.pathfinding.TreeAAStarRisk;
import com.tuky.diploma.structures.addition.Utils;
import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.visual.ControllerFX;

import java.util.*;
import java.util.stream.Collectors;

public class ControllerTAARiskFire extends ControllerTAAFire{


    protected final Map<FireCellMoore2DStochastic, List<FireCellMoore2DStochastic>> covered;
    protected final Map<FireCellMoore2DStochastic, Double> Risk;
    private final double dangerR;

    public ControllerTAARiskFire(ControllerFX fx,
                                 List<FireCellMoore2DStochastic> exits,
                                 FireSpreadCAStochastic CA,
                                 double dangerRadius,
                                 int ts) {
        super(fx, exits, CA, ts);

        this.dangerR = dangerRadius;

        this.covered = new HashMap<>();
        initCovered();

        this.Risk = new HashMap<>();
        initRisk();

        initPathfinders();
        initAgents(fx.getAgentPaths().keySet());

        updatePaths();
    }

    @Override
    public void step() {
        var changed = changedRisk(stepCA());
        updateRisk(changed);
        updatePaths(changed);
        stepAgents();
        reDraw();
        agentPF.keySet().removeIf(this::isSafe);
    }

    @Override
    protected Pathfinding<FireCellMoore2DStochastic> choosePathfinding
            (Graph<FireCellMoore2DStochastic> graph) {
        return new TreeAAStarRisk<>(graph, Risk, dangerR, 1., 1.);
    }

    private void initCovered() {
        for (var node : graph.getAdjTable().keySet())
            covered.put(node, Utils.coveredBy(node, graph, dangerR));
    }

    public void updateRisk(Set<FireCellMoore2DStochastic> changed) {
        for (var node : changed)
            Risk.replace(node, Utils.risk(node, covered, dangerR));
    }

    private void initRisk() {
        for (var node : graph.getAdjTable().keySet())
            Risk.put(node, Utils.risk(node, covered, dangerR));
    }

    private Set<FireCellMoore2DStochastic> changedRisk(Set<FireCellMoore2DStochastic> changed) {
        return changed.stream()
                .map(covered::get)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @Override
    protected void choosePath(Agent<FireCellMoore2DStochastic> agent) {
        var bestPF = pathfinders.get(agent).values().stream()
                .map(pf -> (TreeAAStarRisk<FireCellMoore2DStochastic>) pf)
                .filter(pf -> pf.getPathRisk() < 50)
                .min(Comparator.comparing(Pathfinding::getPathLen))
                .orElse((TreeAAStarRisk<FireCellMoore2DStochastic>) pathfinders.get(agent).values().stream().findAny().get());

        agentPF.replace(agent, bestPF);
        agent.updatePath(bestPF.getPath());
    }
}
