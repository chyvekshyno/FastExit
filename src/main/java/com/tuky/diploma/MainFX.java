package com.tuky.diploma;

import com.tuky.diploma.tests.JavaFXAreaDrawingTest;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
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
    public void start(Stage primaryStage) {
        JavaFXAreaDrawingTest.run(primaryStage);
    }

    private void initUI(Stage primaryStage) {

    }
}
