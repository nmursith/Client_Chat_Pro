package Model;

import Controller.OperatorBubbleController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.CacheHint;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;

import java.io.IOException;

/**
 * Created by mmursith on 12/16/2015.
 */
public class OperatorBubble {
    private static Parent root;
    private  static OperatorBubbleController operatorBubbleController;
    private static FXMLLoader fxmlLoader;

    public OperatorBubble(String name, String  chatMessage, String time) throws IOException {
        //long time1 = System.currentTimeMillis();
        fxmlLoader = new FXMLLoader(getClass().getResource("operatorbubble.fxml"));
        root = fxmlLoader.load();
        //long time2 = System.currentTimeMillis();
       // System.out.println("Time:  "+ (time2-time1));

        operatorBubbleController= fxmlLoader.<OperatorBubbleController>getController();
        operatorBubbleController.setOperatorname(name);
        operatorBubbleController.setOperatormessage(chatMessage);
        operatorBubbleController.setTime(time);

    }

    public void setImage(Image image){
        operatorBubbleController.setOperator_pic_image_view(image);

    }
    public Parent getRoot() {
        root.setCache(true);
        root.setCacheHint(CacheHint.DEFAULT);

        GridPane.setHalignment(root, HPos.RIGHT);

        return root;
    }

}
