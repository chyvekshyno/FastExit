package com.tuky.diploma.visual;

import com.tuky.diploma.structures.area.Area;
import com.tuky.diploma.structures.area.IntCoord;
import com.tuky.diploma.structures.area.Side;
import com.tuky.diploma.structures.area.Zone;
import com.tuky.diploma.structures.area.regularnet.UnitCellNode;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
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
        for (Side side : zone.getShape()) {
            points.add((double) side.getCoord1().X());
            points.add((double) side.getCoord1().Y());
        }
        return points;
    }

    public static Rectangle toJavaFXUnitSquare(UnitCellNode cell) {
        return new Rectangle(cell.getCoord().X(), cell.getCoord().Y(), 1, 1);
    }
}
