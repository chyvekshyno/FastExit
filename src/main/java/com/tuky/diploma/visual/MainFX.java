package com.tuky.diploma.visual;

import com.tuky.diploma.camodels.FireCellMoore2DStochastic;
import com.tuky.diploma.camodels.FireSpreadCAHeat;
import com.tuky.diploma.structures.area.regularnet.RegularNetMoore2D;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class MainFX extends Application {

    public static final String PATH_AREA = "res\\area_json\\area1.json";
    public static final String PATH_FXML = "main.fxml";

    private Map<FireCellMoore2DStochastic, Shape> gridMap;
    private RegularNetMoore2D<FireCellMoore2DStochastic> grid;
    private FireSpreadCAHeat caFire;

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) {

        try {
            initUI(primaryStage);
            run(primaryStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void run(Stage primaryStage) {
        primaryStage.show();
    }

    private void initUI(Stage primaryStage) throws IOException {

        Parent root = FXMLLoader.load(
                Objects.requireNonNull(
                        getClass().getClassLoader().getResource(PATH_FXML)));

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

    }
}
