package com.tuky.diploma.visual;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.util.Duration;

public class NodeTransformsFX {

    private static class DragContext {

        double mouseAnchorX;
        double mouseAnchorY;

        double translateAnchorX;
        double translateAnchorY;

    }

    private static final DragContext dragContext = new DragContext();

    /**
     * Allow to zoom/scale any node with pivot at scene (x,y) coordinates.
     */
    public static void zoom(Timeline timeline, Node node, double factor, double x, double y) {
        double oldScale = node.getScaleX();
        double scale = oldScale * factor;
        if (scale < 0.5) scale = 0.5;
        if (scale > 50)  scale = 50;

        double  f = (scale / oldScale)-1;
        Bounds bounds = node.localToScene(node.getBoundsInLocal());
        double dx = (x - (bounds.getWidth()/2 + bounds.getMinX()));
        double dy = (y - (bounds.getHeight()/2 + bounds.getMinY()));

        timeline.getKeyFrames().clear();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(200), new KeyValue(node.translateXProperty(), node.getTranslateX() - f * dx)),
                new KeyFrame(Duration.millis(200), new KeyValue(node.translateYProperty(), node.getTranslateY() - f * dy)),
                new KeyFrame(Duration.millis(200), new KeyValue(node.scaleXProperty(), scale)),
                new KeyFrame(Duration.millis(200), new KeyValue(node.scaleYProperty(), scale))
        );
        timeline.play();
    }

    public static void zoom(Timeline timeline, Node node, ScrollEvent event) {
        zoom(timeline, node, Math.pow(1.01, event.getDeltaY()), event.getSceneX(), event.getSceneY());
    }
    public static void zoom(Timeline timeline, Node node, ZoomEvent event) {
        zoom(timeline, node, event.getZoomFactor(), event.getSceneX(), event.getSceneY());
    }

    public static void shift(Node node, double x, double y) {
        node.setTranslateX(dragContext.translateAnchorX + x - dragContext.mouseAnchorX);
        node.setTranslateY(dragContext.translateAnchorY + y - dragContext.mouseAnchorY);
    }

    public static void setDragContext(Node node, MouseEvent event) {

        if( !event.isPrimaryButtonDown())   // left mouse button => dragging
            return;

        dragContext.mouseAnchorX = event.getSceneX();
        dragContext.mouseAnchorY = event.getSceneY();
        dragContext.translateAnchorX = node.getTranslateX();
        dragContext.translateAnchorY = node.getTranslateY();
    }

    public static void shift(Node node, MouseEvent event) {
        if( !event.isPrimaryButtonDown())   // left mouse button => dragging
            return;

        shift(node, event.getSceneX(), event.getSceneY());
        event.consume();
    }
}
