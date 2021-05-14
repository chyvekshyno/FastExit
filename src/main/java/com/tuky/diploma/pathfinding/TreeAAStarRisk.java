package com.tuky.diploma.pathfinding;

import com.tuky.diploma.structures.addition.Risk;
import com.tuky.diploma.structures.graph.Graph;
import com.tuky.diploma.structures.graph.NodeMoore2D;

import java.util.*;

public class TreeAAStarRisk <N extends NodeMoore2D<?, Integer> & Risk>
        extends TreeAAStar<N>  {

    protected double riskW;
    protected double distW;
    protected double dangerR;
    protected Map<N, Double> R;

    public TreeAAStarRisk(Graph<N> graph,
                          Map<N, Double> riskMap,
                          double dangerRadius,
                          double riskWeight,
                          double distanceWeight) {
        super(graph);
        this.riskW = riskWeight;
        this.distW = distanceWeight;
        this.dangerR = dangerRadius;
        this.R = riskMap;
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
    }

    @Override
    protected void initStructures() {
        super.initStructures();
    }

    @Override
    protected Comparator<N> openComparator() {
        return Comparator.comparing(node -> dist.get(node)
                                            + distW * H.get(node)
                                            + riskW * R.get(node));
    }

    @Override
    protected void initNode(N node) {
        super.initNode(node);
    }
    //endregion
}
