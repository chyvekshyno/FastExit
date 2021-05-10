package com.tuky.diploma.visual;

import com.tuky.diploma.camodels.FireCellMoore2DHeat;
import com.tuky.diploma.camodels.FireCellMoore2DStochastic;
import com.tuky.diploma.structures.area.*;
import com.tuky.diploma.structures.graph.NodeMoore2D;
import com.tuky.diploma.structures.graph.Node2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AreaFX {

    public static Color COLOR_FIRE_CELL = Color.ORANGE;
    public static Color COLOR_FUEL_CELL = Color.DARKGRAY;
    public static Color COLOR_BURNED_CELL = Color.BROWN;
    public static Color COLOR_NONFUEL_CELL = Color.DIMGRAY;
    public static Color COLOR_EXIT_CELL = Color.GREEN;
    public static Color COLOR_AGENT_CELL = Color.BLUEVIOLET;

    public static List<Double> toJavaFXPathPoints(List<? extends Node2D<Double, Integer>> nodes) {
        List<Double> points = new ArrayList<>();
        for (var node : nodes) {
            points.add((double) node.getCoord().X());
            points.add((double) node.getCoord().Y());
        }

        return points;
    }

    public static Circle toJavaFXUnitCircle(Node2D<Double, Integer> cell){
        return new Circle(cell.getCoord().X(), cell.getCoord().Y(), 0.05);

    }

    public static Rectangle toJavaFXUnitSquare(NodeMoore2D<Double, Integer> cell) {
        return new Rectangle(cell.getCoord().X(), cell.getCoord().Y(), 1, 1);
    }

    public static List<Line> areaJavaFXLines(Area area) {
        return area.getZones().stream()
                .map(AreaFX::zoneJavaFXLines)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public static List<Line> zoneJavaFXLines(Zone zone) {
        return zone.getShape().stream()
                .filter(side -> !(side instanceof Exit))
                .map(side  ->  new Line(side.getCoord1().X(),
                                        side.getCoord1().Y(),
                                        side.getCoord2().X(),
                                        side.getCoord2().Y()))
                .collect(Collectors.toList());
    }

    public static void paintGridCell(Shape cell) {
        cell.setFill(Color.DARKGRAY);
        cell.setStroke(Color.DARKGRAY);
        cell.setStrokeWidth(0.1);
    }

    public static void paintBoundLine(Line line) {
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(0.1);
    }

    public static void paintPath(Polyline path) {
        path.setStroke(Color.GREEN);
        path.setStrokeWidth(0.15);
        path.setFill(Color.TRANSPARENT);
    }

    public static Polyline getPolylinePath (List<? extends Node2D<Double,Integer>> path) {
        Polyline polyline = new Polyline();
        polyline.getPoints().addAll(AreaFX.toJavaFXPathPoints(path));
        AreaFX.paintPath(polyline);
        return polyline;
    }

    public static void updateGrid (Map<FireCellMoore2DStochastic, Shape> gridMap,
                                   List<FireCellMoore2DStochastic> agents,
                                   List<FireCellMoore2DStochastic> exits) {
        for (var entry : gridMap.entrySet()) {
            if (agents.contains(entry.getKey()))
                paintCellAgent(entry.getValue());
            else if (exits.contains(entry.getKey()))
                paintCellExit(entry.getValue());
            else {
                switch (entry.getKey().getState()) {
                    case FUEL -> paintCellFUEL(entry.getValue());
                    case NonFUEL -> paintCellNonFUEL(entry.getValue());
                    case BURNED -> paintCellBURNED(entry.getValue());
                    case FIRE -> paintCellFIRE(entry.getValue());
                }
            }
        }
    }

    private static void paintCellFUEL(Shape cell) {
        cell.setFill(COLOR_FUEL_CELL);
        cell.setStroke(COLOR_FUEL_CELL);
        cell.setStrokeWidth(0.1);
    }

    private static void paintCellNonFUEL(Shape cell) {
        cell.setFill(COLOR_NONFUEL_CELL);
        cell.setStroke(COLOR_NONFUEL_CELL);
        cell.setStrokeWidth(0.1);
    }

    private static void paintCellBURNED(Shape cell) {
        cell.setFill(COLOR_BURNED_CELL);
        cell.setStroke(COLOR_BURNED_CELL);
        cell.setStrokeWidth(0.5);
    }

    private static void paintCellFIRE(Shape cell) {
        cell.setFill(COLOR_FIRE_CELL);
        cell.setStroke(COLOR_FIRE_CELL);
        cell.setStrokeWidth(0.5);
    }

    public static void paintCellAgent(Shape cell) {
        cell.setFill(COLOR_AGENT_CELL);
        cell.setStroke(COLOR_AGENT_CELL);
        cell.setStrokeWidth(0.4);
    }

    private static void paintCellExit(Shape cell) {
        cell.setFill(COLOR_EXIT_CELL);
        cell.setStroke(COLOR_EXIT_CELL);
        cell.setStrokeWidth(1.2);
    }
}
