package com.tuky.diploma.processing;

import com.tuky.diploma.camodels.FireCellMoore2DStochastic;
import com.tuky.diploma.camodels.FireSpreadCAStochastic;
import com.tuky.diploma.pathfinding.Pathfinding;
import com.tuky.diploma.pathfinding.TreeAAStar;
import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.visual.ControllerFX;

import java.util.List;
import java.util.Set;

public class ControllerTAAFire extends ControllerFire {


    public ControllerTAAFire(ControllerFX fx,
                             List<FireCellMoore2DStochastic> exits,
                             FireSpreadCAStochastic CA,
                             int ts) {
        super(fx, exits, CA, ts);
    }

    @Override
    protected Pathfinding<FireCellMoore2DStochastic> choosePathfinding
            (Graph<FireCellMoore2DStochastic> graph) {
        return new TreeAAStar<>(graph);
    }

    @Override
    protected void updatePaths(Set<FireCellMoore2DStochastic> changedCost) {
        for (var pf : pathfinders.values())
            updateTAAPaths((TreeAAStar<FireCellMoore2DStochastic>) pf, changedCost);

        super.updatePaths(changedCost);
    }

    @Override
    protected boolean updatePathCondition(Agent<FireCellMoore2DStochastic> agent,
                                          Set<FireCellMoore2DStochastic> changedCost) {
        return updatePathConditionTreeAA(agent);
    }

    protected void updateTAAPaths(TreeAAStar<FireCellMoore2DStochastic> pathfinding,
                                  Set<FireCellMoore2DStochastic> changedCost) {
        for (var node : changedCost) {
            if (pathfinding.getPathTree().containsKey(node)) {
                pathfinding.removePath(node);
            }
        }
    }

    protected boolean updatePathConditionTreeAA(Agent<FireCellMoore2DStochastic> agent) {
        var pathfinding = (TreeAAStar<FireCellMoore2DStochastic>) agents.get(agent);

        double h_curr = pathfinding.getH()
                .get(agent.getPosition());
        double H_max = pathfinding.getH_max()
                .get(pathfinding.getId()
                        .get(agent.getPosition()));
        return h_curr >= H_max;
    }
}
