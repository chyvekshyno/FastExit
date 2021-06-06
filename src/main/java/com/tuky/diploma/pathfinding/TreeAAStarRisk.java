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

    @Override
    protected void updateH(N curr) {
        closed.forEach(node -> {
            H.put(node, dist.get(curr) + distW * H.get(curr) - dist.get(node) + riskW * R.get(node));
//            if (H.get(node) < 0.)
//                System.out.println("H_new:\t"+H.get(node));

        });
    }

//    @Override
//    protected void updateH(N curr) {
//        closed.stream()
//                .filter(node -> dist.get(node) <= dist.get(curr))
//                .forEach(node -> H.put(node, dist.get(curr) + distW * H.get(curr)));
//    }

    @Override
    protected Comparator<N> openComparator() {
        return Comparator.comparing(node -> dist.get(node)
                                            + distW * H.get(node)
                                            + riskW * R.get(node));
    }

//    @Override
//    protected boolean relaxCondition(N curr, N next, double weight) {
//        return super.relaxCondition(curr, next, weight) && R.get(next) < 20;
//    }
}
