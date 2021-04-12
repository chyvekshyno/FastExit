package com.tuky.diploma;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.awt.*;

public class MainFX extends Application {

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initUI(primaryStage);
    }

    private void initUI(Stage primaryStage) {
        primaryStage.setTitle("FastExit");
        var root = new Pane();

        var canvas = new Canvas(600, 300);
        var gc = canvas.getGraphicsContext2D();
        drawStarShape(gc);
        root.getChildren().add(canvas);

        Scene scene = new Scene(root, 280, 200, Color.WHITESMOKE);


        primaryStage.setTitle("Colours");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void drawStarShape(GraphicsContext gc) {

        double[] xpoints = {10, 85, 110, 135, 210, 160,
                170, 110, 50, 60};
        double[] ypoints = {85, 75, 10, 75, 85, 125,
                190, 150, 190, 125};

        gc.strokePolygon(xpoints, ypoints, xpoints.length);
    }
}
