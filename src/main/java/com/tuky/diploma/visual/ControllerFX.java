package com.tuky.diploma.visual;

import com.tuky.diploma.camodels.FireCellMoore2DStochastic;
import com.tuky.diploma.camodels.FireSpreadCAStochastic;
import com.tuky.diploma.processing.Agent;
import com.tuky.diploma.processing.ControllerFire;
import com.tuky.diploma.processing.ControllerTAAFire;
import com.tuky.diploma.processing.ControllerTAARiskFire;
import com.tuky.diploma.structures.addition.Utils;
import com.tuky.diploma.structures.area.Area;
import com.tuky.diploma.structures.area.AreaJSONParser;
import com.tuky.diploma.structures.area.regularnet.RegularNet2D;
import com.tuky.diploma.structures.area.regularnet.RegularNetMoore2D;
import com.tuky.diploma.structures.graph.Node2D;
import com.tuky.diploma.structures.graph.NodeMoore2D;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Scale;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ControllerFX {

    @FXML   private Button btn_start;
    @FXML   private Button btn_resume;
    @FXML   private Button btn_pause;
    @FXML   private Button btn_step;
    @FXML   private Button btn_abort;
    @FXML   private Pane pane_draw;
    @FXML   private VBox vbox_controller;

    public static final String PATH_AREA_RECT = "res\\area_json\\area_simple_rectangle.json";
    public static final String PATH_AREA0 = "res\\area_json\\area0.json";
    public static final String PATH_AREA_FORUMLVIV = "res\\area_json\\area_forum_lviv.json";

    private Map<FireCellMoore2DStochastic, Rectangle> gridMap;
    private Map<Agent<FireCellMoore2DStochastic>, Polyline> agentPaths;
    private RegularNetMoore2D<FireCellMoore2DStochastic> grid;
    private List<FireCellMoore2DStochastic> exits;
    private FireSpreadCAStochastic caFire;
    private ControllerFire modelController;

    private Group group;
    private Scale sc;

    public Map<FireCellMoore2DStochastic, Rectangle> getGridMap() {
        return gridMap;
    }

    public Map<Agent<FireCellMoore2DStochastic>, Polyline> getAgentPaths() {
        return agentPaths;
    }

    public RegularNetMoore2D<FireCellMoore2DStochastic> getGrid() {
        return grid;
    }

    public static Timeline timeline = new Timeline(60);

    @FXML
    public void initialize() {
        sc = new Scale(3, 3);
        clickedInit();
    }

    private void initCellMouseRClick() {
        group.getChildren().forEach(
                o -> o.setOnMouseClicked(mouseEvent -> {
                    if (mouseEvent.getButton() == MouseButton.SECONDARY)
                        clickedPaneObject(o);
                }));
    }


    private void clickedPaneObject(Node o) {
        Shape s = (Shape) o;
        gridMap.entrySet().stream()
                .filter(entry -> entry.getValue() == s)
                .map(Map.Entry::getKey)
                .findFirst()
                .ifPresent(cell -> {
                    cell.setFire();
                    getGrid().isolate(cell);
                });

    }

    @FXML
    public void clickedInit() {
        initGridMap(PATH_AREA_FORUMLVIV);
        agentPaths = AreaFX.initAgentsPath(
                Utils.randomAgents(
                        new ArrayList<>(grid.getAdjTable().keySet()),
                        100));

        for (var path : agentPaths.values()) {
            AreaFX.paintPath(path);
            path.getTransforms().add(sc);
            group.getChildren().add(path);
        }

        grid.getAdjTable().keySet().stream().limit(4).forEach(FireCellMoore2DStochastic::setFire);
        modelController = new ControllerTAARiskFire(this, exits,
                new FireSpreadCAStochastic(grid, 1000), 5.0, 100);

        AreaFX.updateGrid(gridMap,
                agentPaths.keySet().stream().map(Agent::getPosition).collect(Collectors.toList()),
                exits);

        initCellMouseRClick();

        //  buttons RE
        btn_start.setDisable(false);
        btn_resume.setDisable(true);
        btn_pause.setDisable(true);
        btn_step.setDisable(true);
        btn_abort.setDisable(true);
    }

    @FXML
    public void clickedStart() {
        btn_pause.setDisable(false);

        Thread t = new Thread(() -> {
            try {
                modelController.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();

        btn_resume.setDisable(true);
        btn_abort.setDisable(false);
        btn_start.setDisable(true);
        btn_step.setDisable(true);
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
        btn_pause.setDisable(false);
        btn_step.setDisable(true);
        t.start();
    }

    @FXML
    public void clickedPause() {
        modelController.stop();
        btn_resume.setDisable(false);
        btn_pause.setDisable(true);
        btn_step.setDisable(false);
    }

    @FXML
    public void clickedStep() {
        modelController.step();
    }

    @FXML
    public void clickedAbort() {
        modelController.abort();
        pane_draw.getChildren().clear();

        btn_start.setDisable(false);
        btn_resume.setDisable(true);
        btn_pause.setDisable(true);
        btn_abort.setDisable(true);
    }

    private void initGridMap(String path) {
        pane_draw.getChildren().clear();

        gridMap = new HashMap<>();
        group = new Group();
        pane_draw.getChildren().add(group);

        try {
            Area area = AreaJSONParser.parse(path);
            grid = getGrid(area);
            exits = grid.getExits();
            drawBounds(area, group);
            drawGridInitRect(grid, group);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        pane_draw.setOnScroll(event -> NodeTransformsFX.zoom(timeline, group, event));
        pane_draw.setOnZoom(event -> NodeTransformsFX.zoom(timeline, group, event));

        pane_draw.setOnMousePressed(event -> NodeTransformsFX.setDragContext(group, event));
        pane_draw.setOnMouseDragged(event -> NodeTransformsFX.shift(group, event));

        vbox_controller.toFront();

        scale(group, sc);
    }

    private void scale(Parent p, Scale scale) {
        p.getChildrenUnmodifiable().forEach(o -> o.getTransforms().add(scale));
    }

    private void drawBounds(Area area, Group group) {
        var lines = AreaFX.areaShapeJFX(area);
        lines.forEach(AreaFX::paintBoundLine);

        var exits = AreaFX.areaExitsJFX(area);
        exits.forEach(AreaFX::paintExit);

        group.getChildren().addAll(lines);
        group.getChildren().addAll(exits);
    }

    private void drawGridInitRect(RegularNet2D<FireCellMoore2DStochastic> grid, Group group) {
        Rectangle rect;
        for (var cell : grid.getAdjTable().keySet()) {
            cell.setValue(0.);
            rect = AreaFX.toJavaFXUnitSquare(cell);
            gridMap.put(cell, rect);

            if (exits.contains(cell))
                AreaFX.paintCellExit(rect);
            else
                AreaFX.paintGridCell(rect);

            group.getChildren().add(rect);
        }
    }

    public void drawPath (List<? extends Node2D<Double,Integer>> path, Group group) {
        group.getChildren().addAll(AreaFX.getPolylinePath(path));
    }

    public void updatePathFX (Agent<FireCellMoore2DStochastic> agent) {
        agentPaths.get(agent).getPoints().clear();
        agentPaths.get(agent).getPoints().addAll(
                AreaFX.toJavaFXPathPoints(agent.getPath(), agent.getPosition()));
    }

    //  get anonymous class
    private RegularNetMoore2D<FireCellMoore2DStochastic> getGrid(Area area) {
        return new RegularNetMoore2D<>(area) {
            @Override
            protected List<FireCellMoore2DStochastic> cellLineFirst
                    (int y, int x0, int x1, List<List<FireCellMoore2DStochastic>> map) {
                var cell = FireCellMoore2DStochastic.at(x0, y);
//                addNode(cell);
                List<FireCellMoore2DStochastic> cellLine = new ArrayList<>();
                cellLine.add(cell);
                try {
                    for (int w = x0 + 1; w < x1 + 1; w++) {
                        cell = FireCellMoore2DStochastic.at(w, y);
//                        addNode(cell);
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
                List<FireCellMoore2DStochastic> cellLine = new ArrayList<>();

                try {
                    var cell = FireCellMoore2DStochastic.at(x0, y);
                    addTransition(cell, getAtRect(x0    , y - 1, map), NodeMoore2D.VEC_TOP     , 1.0);
                    addTransition(cell, getAtRect(x0 + 1, y - 1, map), NodeMoore2D.VEC_TOP_RIGHT, 1.3);
                    cellLine.add(cell);
                    for (int w = x0 + 1; w < x1; w++) {
                        cell = FireCellMoore2DStochastic.at(w, y);
                        cellLine.add(cell);
                        addTransition(cell, cellLine.get(w - x0 - 1)    , NodeMoore2D.VEC_LEFT     , 1.0);
                        addTransition(cell, getAtRect(w - 1, y - 1, map), NodeMoore2D.VEC_TOP_LEFT , 1.3);
                        addTransition(cell, getAtRect(w    , y - 1, map), NodeMoore2D.VEC_TOP      , 1.0);
                        addTransition(cell, getAtRect(w + 1, y - 1, map), NodeMoore2D.VEC_TOP_RIGHT, 1.3);
                    }
                    cell = FireCellMoore2DStochastic.at(x1, y);
                    cellLine.add(cell);
                    addTransition(cell, cellLine.get(x1 - x0 - 1)    , NodeMoore2D.VEC_LEFT    , 1.0);
                    addTransition(cell, getAtRect(x1 - 1, y - 1, map), NodeMoore2D.VEC_TOP_LEFT, 1.3);
                    addTransition(cell, getAtRect(x1    , y - 1, map), NodeMoore2D.VEC_TOP     , 1.0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return cellLine;
            }
        };
    }

}