package Controller;

import Model.Constant;
import Model.OperatorBubble;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javax.jms.JMSException;
import java.io.IOException;

public class ChatController {
    public ScrollPane messageDisplay;
    public TextArea messageTextField;
    public GridPane chatHolder;
    public int IDtracker;
    public ChatController controller;
    public PostRequestController postRequestController;


    public ChatController() throws JMSException {
        chatHolder = getGridPane();
        IDtracker = 0;


        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                messageDisplay.setContent(chatHolder);
                messageDisplay.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                String message= null;
                postRequestController = new PostRequestController(getInstance());
                try {
                    postRequestController.SendMessageAIML(Constant.DIRDEVELOPERSTART);

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    public void slideWindow(ActionEvent actionEvent) {
    }

    public void sendMessage(ActionEvent actionEvent) {
        try {
            sendMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

        c1.setPercentWidth(95);
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
        String enteredmessage = messageTextField.getText();
        messageTextField.clear();

        try {

            OperatorBubble bubble = new OperatorBubble("USER",enteredmessage, "S" );
            chatHolder.addRow(getIDtracker(), bubble.getRoot());
            Platform.runLater(() -> controller.messageDisplay.setVvalue(controller.messageDisplay.getVmax()));
        } catch (IOException e) {
            e.printStackTrace();
        }

/*        try {
            //Thread.sleep(1000);

            String message = PostRequestController.SendMessageAI(enteredmessage);
            state = PostRequestController.state;

           if(!message.equals("")){

               Platform.runLater(() -> {
                   messageDisplay.setVvalue(messageDisplay.getVmax());
                   /////////
               });
           }

        } catch (IOException e) {
            e.printStackTrace();
        }*/


        postRequestController.routeMessage(enteredmessage);
    }



    public synchronized ChatController getInstance(){
        if(controller==null){
            controller = this;
        }
        return  controller;
    }
    public int getIDtracker() {
        IDtracker++;
        return IDtracker;
    }
    public Stage getStage(){
        return  null;
    }
}
