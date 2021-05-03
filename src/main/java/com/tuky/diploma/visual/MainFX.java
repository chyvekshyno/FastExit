package com.tuky.diploma.visual;

import com.tuky.diploma.camodels.FireCellMoore2D;
import com.tuky.diploma.camodels.FireSpreadCA;
import com.tuky.diploma.structures.area.Area;
import com.tuky.diploma.structures.area.AreaJSONParser;
import com.tuky.diploma.structures.area.regularnet.RegularNet2D;
import com.tuky.diploma.structures.area.regularnet.RegularNetMoore2D;
import com.tuky.diploma.structures.graph.Node2D;
import com.tuky.diploma.structures.graph.NodeMoore2D;
import com.tuky.diploma.tests.JavaFXAreaDrawingTest;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFX extends Application {

    public static final String PATH = "res\\area_json\\area1.json";

    private Map<FireCellMoore2D, Shape> gridMap;
    private RegularNetMoore2D<FireCellMoore2D> grid;
    private FireSpreadCA caFire;

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) {
        initUI(primaryStage);
        run(primaryStage);
    }

    private void initUI(Stage primaryStage) {
        gridMap = new HashMap<>();
        Pane root = new Pane();
        Group group = new Group();

        root.getChildren().add(group);
        Scene scene = new Scene(root, 1200, 900, Color.WHITESMOKE);
        Area area = null;

        try {
            area = AreaJSONParser.parse(PATH);

            grid = new RegularNetMoore2D<>(area) {
                @Override
                protected List<FireCellMoore2D> cellLineFirst
                        (int y, int x0, int x1, List<List<FireCellMoore2D>> map) {
                    FireCellMoore2D cell = FireCellMoore2D.at(x0, y);
                    List<FireCellMoore2D> cellLine = new ArrayList<>();
                    cellLine.add(cell);
                    try {
                        for (int w = x0 + 1; w < x1 + 1; w++) {
                            cell = FireCellMoore2D.at(w, y);
                            cellLine.add(cell);
                            addTransition(cell, cellLine.get(w - x0 - 1), NodeMoore2D.VEC_LEFT, 1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return cellLine;
                }

                @Override
                protected List<FireCellMoore2D> cellLine
                        (int y, int x0, int x1, List<List<FireCellMoore2D>> map) {
                    var cell = FireCellMoore2D.at(x0, y);
                    List<FireCellMoore2D> cellLine = new ArrayList<>();
                    cellLine.add(cell);
                    try {
                        for (int w = x0 + 1; w < x1; w++) {
                            cell = FireCellMoore2D.at(w, y);
                            cellLine.add(cell);
                            addTransition(cell, cellLine.get(w - x0 - 1), NodeMoore2D.VEC_LEFT, 1);
                            addTransition(cell, getAtRect(w - 1, y - 1, map), NodeMoore2D.VEC_TOP_LEFT, 1.3);
                            addTransition(cell, getAtRect(w, y - 1, map), NodeMoore2D.VEC_TOP, 1);
                            addTransition(cell, getAtRect(w + 1, y - 1, map), NodeMoore2D.VEC_TOP_RIGHT, 1.3);
                        }
                        cell = FireCellMoore2D.at(x1, y);
                        cellLine.add(cell);
                        addTransition(cell, cellLine.get(x1 - x0 - 1), NodeMoore2D.VEC_LEFT, 1);
                        addTransition(cell, getAtRect(x1 - 1, y - 1, map), NodeMoore2D.VEC_TOP_LEFT, 1.3);
                        addTransition(cell, getAtRect(x1, y - 1, map), NodeMoore2D.VEC_TOP, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return cellLine;
                }
            };

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        drawBounds(area, group);
        drawGridInit(grid, group);
        scale(group, new Scale(20,20));

        primaryStage.setScene(scene);
    }


    private void run(Stage primaryStage) {
        primaryStage.show();
    }

    private void scale(Parent p, Scale scale) {
        p.getChildrenUnmodifiable().forEach(o -> o.getTransforms().add(scale));
    }

    private void drawBounds(Area area, Group group) {
        var lines = AreaFX.areaJavaFXLines(area);
        lines.forEach(AreaFX::paintBoundLine);
        group.getChildren().addAll(lines);
    }

    private void drawGridInit (RegularNet2D<FireCellMoore2D> grid, Group group) {
        Circle circle;
        for (var cell : grid.getAdjTable().keySet()) {
            cell.setValue(0.);
            circle = AreaFX.toJavaFXUnitCircle(cell);
            gridMap.put(cell, circle);

            AreaFX.paintGridCell(circle);
            group.getChildren().add(circle);
        }
    }

    private void drawPath (List<? extends Node2D<Double,Integer>> path, Group group) {
        Polyline polyline = new Polyline();
        polyline.getPoints().addAll(AreaFX.toJavaFXPathPoints(path));
        AreaFX.paintPath(polyline);

        group.getChildren().addAll(polyline);
    }


}
