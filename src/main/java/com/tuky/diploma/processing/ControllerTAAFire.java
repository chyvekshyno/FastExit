package com.tuky.diploma.processing;

import com.tuky.diploma.camodels.FireCellMoore2DStochastic;
import com.tuky.diploma.camodels.FireSpreadCAStochastic;
import com.tuky.diploma.pathfinding.Pathfinding;
import com.tuky.diploma.pathfinding.TreeAAStar;
import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.structures.graph.Transition;
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

    protected boolean updatePathConditionTreeAA(Agent<FireCellMoore2DStochastic> agent) {
        var pathfinding = (TreeAAStar<FireCellMoore2DStochastic>) agentsPathfinding.get(agent);
        double h_curr = pathfinding.getH()
                .get(agent.getPosition());
        double H_max = pathfinding.getH_max()
                .get(pathfinding.getId()
                        .get(agent.getPosition()));
        return h_curr >= H_max;
    }

    protected void updateTreeAAStar(TreeAAStar<FireCellMoore2DStochastic> pathfinding,
                                    Set<FireCellMoore2DStochastic> changedCost) {
        for (var node : changedCost)
            if (pathfinding.getPathTree().containsKey(node))
                pathfinding.removePath(node);
    }

    @Override
    protected Thread updatePath(Agent<FireCellMoore2DStochastic> agent,
                                Set<FireCellMoore2DStochastic> changedCost) {

        updateTreeAAStar(
                (TreeAAStar<FireCellMoore2DStochastic>) agentsPathfinding.get(agent),
                changedCost);

        if (updatePathConditionTreeAA(agent)) {
            Thread t = new Thread(updatePathRunnable(agent));
            t.start();
            return t;
        }
        return null;
    }
}
