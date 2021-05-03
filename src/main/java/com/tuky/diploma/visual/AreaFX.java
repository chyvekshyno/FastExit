package com.tuky.diploma.visual;

import com.tuky.diploma.structures.area.*;
import com.tuky.diploma.structures.graph.NodeMoore2D;
import com.tuky.diploma.structures.graph.Node2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AreaFX {

    public static Color FIRE_CELL_COLOR = Color.ORANGE;
    public static Color FUEL_CELL_COLOR = Color.WHITESMOKE;
    public static Color BURNED_CELL_COLOR = Color.BROWN;
    public static Color NONFUEL_CELL_COLOR = Color.DARKGRAY;

    public static void draw(Area area, GraphicsContext gc) {

    }

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

    public static void paintFireCell(Rectangle cell) {

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
}
