package com.tuky.diploma.visual;

import com.tuky.diploma.structures.area.*;
import com.tuky.diploma.structures.graph.NodeMoore2D;
import com.tuky.diploma.structures.graph.Node2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class AreaFX {
    public static void draw(Area area, GraphicsContext gc) {

    }

    public static Polygon toJavaFXPolygon(Zone zone) {
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(PolygonPoints(zone));
        return polygon;
    }

    public static List<Double> toJavaFXPathPoints(List<Node2D<Double, Integer>> nodes) {
        List<Double> points = new ArrayList<>();
        for (var node : nodes) {
            points.add(node.getCoord().X() + 0.5);
            points.add(node.getCoord().Y() + 0.5);
        }

        return points;
    }

    private static List<Double> PolygonPoints(Zone zone) {
        List<Double> points = new ArrayList<>();
        for (Side side : zone.getShape()) {
            points.add((double) side.getCoord1().X());
            points.add((double) side.getCoord1().Y());
        }
        return points;
    }

    public static Rectangle toJavaFXUnitSquare(NodeMoore2D<Double, Integer> cell) {
        return new Rectangle(cell.getCoord().X(), cell.getCoord().Y(), 1, 1);
    }
}
