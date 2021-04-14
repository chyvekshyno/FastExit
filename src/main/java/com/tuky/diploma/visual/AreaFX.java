package com.tuky.diploma.visual;

import com.tuky.diploma.structures.area.Area;
import com.tuky.diploma.structures.area.IntCoord;
import com.tuky.diploma.structures.area.Zone;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

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

    private static List<Double> PolygonPoints(Zone zone) {
        List<Double> points = new ArrayList<>();
        for (IntCoord coord : zone.getPoints()) {
            points.add((double) coord.X());
            points.add((double) coord.Y());
        }
        return points;
    }
}
