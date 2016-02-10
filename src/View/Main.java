package View;

import Controller.ChatController;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.WritableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.CacheHint;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {



        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Client.fxml"));
        Parent root = fxmlLoader.load();

        root.setCache(true);
        root.setCacheHint(CacheHint.DEFAULT);
        stage.setTitle("Main");



        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);

        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        //double screenRightEdge = primScreenBounds.getMaxX();
        double screenLeftEdge = primScreenBounds.getMinX();
        System.out.println(screenLeftEdge-400);
        stage.setX(screenLeftEdge-400);
        //stage.setX(500);
        System.out.println(primScreenBounds.getWidth());
        stage.setY(primScreenBounds.getMinY());
        //stage.setY(100);
        stage.setWidth(0);
        stage.setHeight(primScreenBounds.getHeight());

        ChatController chatController = fxmlLoader.<ChatController>getController();
        System.out.println("ChatController Started");
        stage.show();
        //chatController.setStage(primaryStage);

        Timeline timeline = new Timeline();

        WritableValue<Double> writableWidth = new WritableValue<Double>() {
            @Override
            public Double getValue() {
                return stage.getWidth();
            }

            @Override
            public void setValue(Double value) {
                stage.setX(screenLeftEdge);
                stage.setWidth(value);
            }
        };

        KeyValue kv = new KeyValue(writableWidth, 400d);
        KeyFrame kf = new KeyFrame(Duration.millis(2000), kv);
        timeline.getKeyFrames().addAll(kf);
        timeline.play();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Timeline timeline = new Timeline();
                KeyFrame endFrame = new KeyFrame(Duration.millis(2000), new KeyValue(writableWidth, 0.0));
                timeline.getKeyFrames().add(endFrame);
                timeline.setOnFinished(e -> Platform.runLater(() -> stage.hide()));
                timeline.play();
                event.consume();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
