package com.tuky.diploma.visual;

import com.tuky.diploma.camodels.FireCellMoore2DStochastic;
import com.tuky.diploma.camodels.FireSpreadCAHeat;
import com.tuky.diploma.camodels.FireSpreadCAStochastic;
import com.tuky.diploma.pathfinding.AdaptiveAStar;
import com.tuky.diploma.processing.Agent;
import com.tuky.diploma.processing.ControllerFireMoore2D;
import com.tuky.diploma.structures.area.Area;
import com.tuky.diploma.structures.area.AreaJSONParser;
import com.tuky.diploma.structures.area.regularnet.RegularNet2D;
import com.tuky.diploma.structures.area.regularnet.RegularNetMoore2D;
import com.tuky.diploma.structures.graph.Node;
import com.tuky.diploma.structures.graph.Node2D;
import com.tuky.diploma.structures.graph.NodeMoore2D;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Scale;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ControllerFX {

    @FXML   private Button btn_start;
    @FXML   private Button btn_resume;
    @FXML   private Button btn_stop;
    @FXML   private Button btn_abort;
    @FXML   private Pane pane_draw;
    @FXML   private VBox vbox_controller;

    public static final String PATH_AREA = "res\\area_json\\area1.json";

    private Map<FireCellMoore2DStochastic, Shape> gridMap;
    private RegularNetMoore2D<FireCellMoore2DStochastic> grid;
    private FireSpreadCAStochastic caFire;
    private ControllerFireMoore2D modelController;

    @FXML
    public void initialize() {
        clickedInit();
    }

    @FXML
    public void clickedInit() {
        initGridMap(PATH_AREA);
        btn_start.setDisable(false);
        btn_resume.setDisable(true);
        btn_stop.setDisable(true);
        btn_abort.setDisable(true);
    }

    @FXML
    public void clickedStart() {
        btn_stop.setDisable(false);

        modelController = new ControllerFireMoore2D(grid,
                gridMap,
                new AdaptiveAStar<>(grid),
                new ArrayList<>() {{  add(new Agent<>(grid.get(13, 13)));
                    add(new Agent<>(grid.get(30, 25)));  }},
                new ArrayList<>() {{  add(grid.get(45, 35));  }},
                new FireSpreadCAStochastic(grid, 1000),
                200);

        Thread t = new Thread(() -> {
            try {
                grid.get(45,7).setFire();
                modelController.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();

        btn_resume.setDisable(true);
        btn_abort.setDisable(false);
        btn_start.setDisable(true);
    }

    @FXML
    public void clickedResume() {
        Thread t = new Thread(() -> {
            try {
                modelController.resume();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        btn_resume.setDisable(true);
        btn_stop.setDisable(false);
        t.start();
    }
    @FXML
    public void clickedStop() {
        modelController.stop();
        btn_resume.setDisable(false);
        btn_stop.setDisable(true);
    }

    @FXML
    public void clickedAbort() {
        modelController.abort();
        pane_draw.getChildren().clear();

        btn_start.setDisable(false);
        btn_resume.setDisable(true);
        btn_stop.setDisable(true);
        btn_abort.setDisable(true);
    }

    private void initGridMap(String path) {
        pane_draw.getChildren().clear();

        gridMap = new HashMap<>();
        Group group = new Group();
        pane_draw.getChildren().add(group);

        try {
            Area area = AreaJSONParser.parse(path);
            grid = getGrid(area);
            drawBounds(area, group);
            drawGridInit(grid, group);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        scale(group, new Scale(10,10));
    }

    //  get anonymous class
    private RegularNetMoore2D<FireCellMoore2DStochastic> getGrid(Area area) {
        return new RegularNetMoore2D<>(area) {
            @Override
            protected List<FireCellMoore2DStochastic> cellLineFirst
                    (int y, int x0, int x1, List<List<FireCellMoore2DStochastic>> map) {
                FireCellMoore2DStochastic cell = FireCellMoore2DStochastic.at(x0, y);
                List<FireCellMoore2DStochastic> cellLine = new ArrayList<>();
                cellLine.add(cell);
                try {
                    for (int w = x0 + 1; w < x1 + 1; w++) {
                        cell = FireCellMoore2DStochastic.at(w, y);
                        cellLine.add(cell);
                        addTransition(cell, cellLine.get(w - x0 - 1), NodeMoore2D.VEC_LEFT, 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return cellLine;
            }

            @Override
            protected List<FireCellMoore2DStochastic> cellLine
                    (int y, int x0, int x1, List<List<FireCellMoore2DStochastic>> map) {
                var cell = FireCellMoore2DStochastic.at(x0, y);
                List<FireCellMoore2DStochastic> cellLine = new ArrayList<>();
                cellLine.add(cell);
                try {
                    for (int w = x0 + 1; w < x1; w++) {
                        cell = FireCellMoore2DStochastic.at(w, y);
                        cellLine.add(cell);
                        addTransition(cell, cellLine.get(w - x0 - 1), NodeMoore2D.VEC_LEFT, 1);
                        addTransition(cell, getAtRect(w - 1, y - 1, map), NodeMoore2D.VEC_TOP_LEFT, 1.3);
                        addTransition(cell, getAtRect(w, y - 1, map), NodeMoore2D.VEC_TOP, 1);
                        addTransition(cell, getAtRect(w + 1, y - 1, map), NodeMoore2D.VEC_TOP_RIGHT, 1.3);
                    }
                    cell = FireCellMoore2DStochastic.at(x1, y);
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
    }

    private void scale(Parent p, Scale scale) {
        p.getChildrenUnmodifiable().forEach(o -> o.getTransforms().add(scale));
    }

    private void drawBounds(Area area, Group group) {
        var lines = AreaFX.areaJavaFXLines(area);
        lines.forEach(AreaFX::paintBoundLine);
        group.getChildren().addAll(lines);
    }

    private void drawGridInit (RegularNet2D<FireCellMoore2DStochastic> grid, Group group) {
        Circle circle;
        for (var cell : grid.getAdjTable().keySet()) {
            cell.setValue(0.);
            circle = AreaFX.toJavaFXUnitCircle(cell);
            gridMap.put(cell, circle);

            AreaFX.paintGridCell(circle);
            group.getChildren().add(circle);
        }
    }

    public void drawPath (List<? extends Node2D<Double,Integer>> path, Group group) {
        group.getChildren().addAll(AreaFX.getPolylinePath(path));
    }

}
