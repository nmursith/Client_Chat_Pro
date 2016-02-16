package Controller;

import Model.ChatMessage;
import Model.Constant;
import Model.OperatorBubble;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.Button;
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
    public Button sendButton;
    public Button disconnectButton;


    protected  Stage primaryStage;

    public ChatController() throws JMSException {
        chatHolder = getGridPane();
        IDtracker = 0;




        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                messageDisplay.setContent(chatHolder);
                messageDisplay.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                messageDisplay.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
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
        gridPane.setStyle("-fx-background-color:#1F3C92;-fx-border-color:#1F3C92; -fx-padding:0em;");
        ColumnConstraints c1 = new ColumnConstraints();

        c1.setPercentWidth(98);
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


        Task<Void> task = new Task<Void>() {
            @Override protected Void call() throws Exception {
                Platform.runLater(() -> {

                    String enteredmessage = messageTextField.getText();
                    messageTextField.clear();

                    try {
                        ChatMessage chatMessage = getObjectMessage(enteredmessage);

                        OperatorBubble bubble = new OperatorBubble(Constant.USERNAME,chatMessage.getTextMessage(), chatMessage.getTime() );
                        chatHolder.addRow(getIDtracker(), bubble.getRoot());
                        Platform.runLater(() -> controller.messageDisplay.setVvalue(controller.messageDisplay.getVmax()));
                        postRequestController.routeMessage(enteredmessage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                return null;
            }
        };

        Platform.runLater(task);

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



    }

    public ChatMessage getObjectMessage(String messageText){
        ChatMessage chatMessage =  new ChatMessage();
        chatMessage.setTextMessage(messageText);
        return chatMessage;
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



    public void ToggleBackgroundWhite(Event event) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                messageDisplay.setPrefHeight(650);
                messageDisplay.setFitToHeight(true);
                messageDisplay.setVvalue(messageDisplay.getVmax());
            }
        });

        //messageTextField.setStyle("-fx-control-inner-background:white;-fx-padding:0em;");
    }

    public void ToggleBackgroundBlue(Event event) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //primaryStage.setIconified(true);
                //primaryStage.hide();
                messageTextField.setVisible(false);
                sendButton.setVisible(false);
                disconnectButton.setVisible(false);

                messageDisplay.setPrefHeight(650);
                messageDisplay.setFitToHeight(true);
                messageDisplay.setVvalue(messageDisplay.getVmax());
            }
        });
        //messageTextField.setStyle("-fx-background-color:#174172;");
//        messageTextField.setStyle("-fx-control-inner-background:#174172;-fx-padding:0em;");



    }
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void disconnectChat(ActionEvent actionEvent) {
        try {
            postRequestController.routeMessage(Constant.exitUserMessage);
            ChatMessage chatMessage = getObjectMessage("Chat Closed By User");

            OperatorBubble bubble = new OperatorBubble(Constant.USERNAME,chatMessage.getTextMessage(), chatMessage.getTime() );
            chatHolder.addRow(getIDtracker(), bubble.getRoot());

            disconnectButton.setVisible(false);
            Platform.runLater(() -> controller.messageDisplay.setVvalue(controller.messageDisplay.getVmax()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ShowComponents(Event event) {


        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                messageTextField.setVisible(true);
                sendButton.setVisible(true);

                if(postRequestController.state==2)
                    disconnectButton.setVisible(true);

                messageDisplay.setPrefHeight(507);
                messageDisplay.setFitToHeight(true);
                messageDisplay.setVvalue(messageDisplay.getVmax());
            }
        });
    //    System.out.println("EEEEEEEEEEEEE");
    }

    public void HideComponents(Event event) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                messageTextField.setVisible(false);
                sendButton.setVisible(false);

                //if(postRequestController.state==2)
                    disconnectButton.setVisible(false);

                messageDisplay.setPrefHeight(650);
                messageDisplay.setFitToHeight(true);
                messageDisplay.setVvalue(messageDisplay.getVmax());
            }
        });
    }
}
