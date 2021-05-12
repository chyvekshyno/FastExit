package com.tuky.diploma.pathfinding;

import com.tuky.diploma.structures.addition.Risk;
import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.structures.graph.Node2D;

import java.util.*;

public class TreeAAStarRisk <N extends Node2D<?, Integer> & Risk>
        extends TreeAAStar<N>  {

    protected double riskW;
    protected double distW;
    protected double dangerR;
    protected Map<N, Double> R;

    public TreeAAStarRisk(Graph<N> graph,
                          double riskWeight,
                          double distanceWeight,
                          double dangerRadius) {
        super(graph);
        this.riskW = riskWeight;
        this.distW = distanceWeight;
        this.dangerR = dangerRadius;
    }

    //region    GETTER's n SETTER's

    public double getRiskWeight() {
        return riskW;
    }

    public void setRiskWeight(double riskW) {
        this.riskW = riskW;
    }

    public double getDistWeight() {
        return distW;
    }

    public void setDistWeight(double distW) {
        this.distW = distW;
    }

    public double getDangerRadius() {
        return dangerR;
    }

    public void setDangerRadius(double dangerR) {
        this.dangerR = dangerR;
    }
    //endregion

    //region    OVERRIDING

    @Override
    protected void clearData() {
        super.clearData();
        R = new HashMap<>();
    }

    @Override
    protected void initStructures() {
        super.initStructures();
        R = new HashMap<>();
        open = new PriorityQueue<>(Comparator
                .comparing(node -> dist.get(node) + H.get(node) + R.get(node)));

    }

    @Override
    protected void initData(N source, N target) {
        super.initData(source, target);
    }

    @Override
    protected void initNode(N node) {
        super.initNode(node);
        R.put(node, risk(node));
    }
    //endregion

    //region  RISK CALCULATING

    protected double risk(N curr) {
        double risk = 0.;
        for (var damageNode : riskCovered(curr))
            risk += damage(curr, damageNode);
        return risk;
    }

    protected List<N> riskCovered(N node) {
        List<N> damageNodes = new ArrayList<>();
        Queue<N> queue = new LinkedList<>();

        for (var tr : graph.getAdjTable().get(node)) {
            if (tr == null) continue;
            queue.add(tr.getEnd());
        }

        N curr;
        while (!queue.isEmpty()) {
            curr = queue.poll();
            if (potential(node, curr) < dangerR) {
                damageNodes.add(curr);
                for (var tr : graph.getAdjTable().get(curr)) {
                    if (tr == null) continue;
                    queue.add(tr.getEnd());
                }
            }
        }

        return damageNodes;
    }

    protected double damage(N curr, N danger) {
        return danger.getMaxDamage() *
                (1 - potential(curr, danger) / dangerR);
    }
    //endregion
}
