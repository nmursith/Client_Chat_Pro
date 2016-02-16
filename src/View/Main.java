package View;

import Controller.ChatController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.CacheHint;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.IOException;

public class Main extends Application {
    private Stage stage;


    @Override
    public void start(Stage primarystage) throws IOException {

        stage = primarystage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Client.fxml"));
        Parent root = fxmlLoader.load();

        root.setCache(true);
        root.setCacheHint(CacheHint.DEFAULT);
        stage.setTitle("vAssistant");
      //  check();


        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("theme.css").toExternalForm());
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);

        stage.show();
        stage.setX(0);

//        stage.initModality(Modality.WINDOW_MODAL);
        ChatController chatController = fxmlLoader.<ChatController>getController();
        System.out.println("ChatController Started");

        chatController.setPrimaryStage(stage);
        stage.setIconified(false);
        stage.requestFocus();

//
//        scene.addEventFilter(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent mouseEvent) {
//                System.out.println("mouse click detected! " + mouseEvent.getSource());
//            }
//        });


/*        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
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
        });*/

            new MouseHandler().start();
    }

    public void SlideLTR(){


    }
    public void SlideRTL(){

    }

    public static void main(String[] args) {


        launch(args);
    }

    class MouseHandler extends Thread{

        Thread thread = this;
        volatile boolean isRunning = true;
        double X;
        public void run() {
            thread = Thread.currentThread();
            isRunning = true;

            //Constant.getRandomString();

            while(isRunning ){
                //   System.out.println("network thread   "+isRunning);
                //System.out.println("Im running");
                X = MouseInfo.getPointerInfo().getLocation().getX();

                if(X==0){
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            stage.setIconified(false);
                            stage.requestFocus();

                        }
                    });
                }
                else if(X>400){
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            stage.setIconified(true);
                            //stage.requestFocus();

                        }
                    });
                }
                //System.out.println(X);

                try {
                    thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


//            stopThread();
            }
        }

        public  void stopThread(){

            isRunning = false;
            //Thread t = thread;
            thread = null;
            //t.interrupt();
        }
    }
}
