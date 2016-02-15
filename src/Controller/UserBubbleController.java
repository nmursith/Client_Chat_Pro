package Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by mmursith on 12/16/2015.
 */
public class UserBubbleController implements Initializable {
    public ImageView user_pic_image_view;
    @FXML
    Label username;
    @FXML
    Label usermessage;
    @FXML
    private
    Label time ;

    public UserBubbleController(){

    }
    public void setOperator_pic_image_view(Image pic_image_view) {
        this.user_pic_image_view.setImage(pic_image_view);
    }
    public void setUsernamename(String operatorname) {
//        System.out.println("seetting");

        this.username.setText(operatorname);
    }

    public void setUsermessage(String operatormessage) {
        this.usermessage.setText(operatormessage);

    }
    public void setTime(String time){
        this.time.setText(time);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {



    }
}
