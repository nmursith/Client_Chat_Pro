package View;

import Controller.PostRequestController;
import Model.Constant;
import Model.OperatorBubble;
import Model.UserBubble;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class ChatController {
    public ScrollPane messageDisplay;
    public TextArea messageTextField;
    public GridPane chatHolder;
    public int IDtracker;

    public ChatController(){
        chatHolder = getGridPane();
        IDtracker = 0;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                messageDisplay.setContent(chatHolder);
                String message= null;
                try {
                    message = PostRequestController.SendMessageAIML(Constant.DIRDEVELOPERSTART);
                    UserBubble bubble = new UserBubble("BOT",message, "S" );
                    chatHolder.addRow(getIDtracker(), bubble.getRoot());
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    public void slideWindow(ActionEvent actionEvent) {
    }

    public void sendMessage(ActionEvent actionEvent) {


    }


    public GridPane getGridPane() {
        GridPane gridPane = new GridPane();
        //gridPane.setMaxSize(431, 413);
        int width = 380;
        gridPane.setPrefWidth(width);
        gridPane.setMinWidth(width);
        gridPane.setMaxWidth(width);
        gridPane.setPrefHeight(507);
        gridPane.setVgap(7);
        ColumnConstraints c1 = new ColumnConstraints();

        c1.setPercentWidth(100);
        gridPane.getColumnConstraints().add(c1);

        return gridPane;
    }

    public void doSendMessage(Event event) throws IOException {
        if (((KeyEvent)event).getCode().equals(KeyCode.ENTER)){
            sendMessage();
            event.consume();
            //System.out.println("sending");

        }
    }

    private void sendMessage() throws IOException {
        String message = messageTextField.getText();
        messageTextField.clear();

        try {

            OperatorBubble bubble = new OperatorBubble("USER",message, "S" );
            chatHolder.addRow(getIDtracker(), bubble.getRoot());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            message = PostRequestController.SendMessageAI(message);
            UserBubble bubble = new UserBubble("BOT",message, "S" );
            chatHolder.addRow(getIDtracker(), bubble.getRoot());
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    messageDisplay.setVvalue(messageDisplay.getVmax());
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public int getIDtracker() {
        IDtracker++;
        return IDtracker;
    }
}
