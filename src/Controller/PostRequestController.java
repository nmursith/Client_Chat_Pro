package Controller;

import Model.ChatMessage;
import Model.Constant;
import Model.UserBubble;
import javafx.application.Platform;

import javax.jms.JMSException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mmursith on 2/10/2016.
 */
public class PostRequestController {
    public int state;
    public OperatorController operatorController;
    public ChatController chatController;
    public PostRequestController(ChatController controller){
        try {
            String ID = Constant.getRandomString();
            operatorController = new OperatorController(ID, "chat."+ID, controller);
            operatorController.setPostRequestController(this);
            chatController = controller;
            state = 0;
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    public  void main(String[]args){
//        try {
//
//
//              System.out.println("RESOASDFAD:   "+SendMessageAI("what is insightlive?"));
//            System.out.println("RESOASDFAD:   "+SendMessageAIML("can you take me to the beacon?"));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void routeMessage(String usermessage) throws IOException {

        //String message = evaluateResponse(usermessage);

        if (usermessage.trim() == "") {
            System.out.println("[BOTConsole] sayChat: ERROR nothing to send");
        } else {
            if (state == 0) {
                //Add send chat bubble, send the request and clear text box

                try {
                    SendMessageAIML(usermessage);
                //    msg = evaluateResponse(msg);

//                    UserBubble bubble = new UserBubble("AIML",msg, "S" );
//                    chatController.chatHolder.addRow(chatController.getIDtracker(), bubble.getRoot());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (state == 1) {
                //Add send chat bubble, send the request and clear text box

                SendMessageAI(usermessage);
                //state=0;
///                msg = evaluateResponse(msg);

             /*   if (state == 0) {
                    msg=SendMessageAIML(usermessage);
                    // return;
                } else if (state == 2) {
                    sendChatOperator(msg);
                    //There is not state change from 0 to 2
                    System.out.println("[BOTConsole] sendDirectivetoBot: ERROR state change from 0 to 2");
                }
                state = 0;*/

//                UserBubble bubble = new UserBubble("AI",msg, "S" );
//                chatController.chatHolder.addRow(chatController.getIDtracker(), bubble.getRoot());

            } else if (state == 2) {

                sendChatOperator(usermessage);

            }


            Platform.runLater(() -> {
                chatController.messageDisplay.setVvalue(chatController.messageDisplay.getVmax());
                /////////
            });

        }

    }

    private  void sendChatOperator(String usermessage) {
        if(!operatorController.isClosedAlready()){
            operatorController.createSession();
            operatorController.startDefaultOperatorAction();

        }
        try {
            ChatMessage chatMessage = getObjectMessage(usermessage);
            operatorController.sendMessage(chatMessage,operatorController);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public  void SendMessageAI(String message) throws IOException {

        String POST_URL = Constant.AI_URL;
        URL obj = new URL(POST_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
      //  con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
    //    con.setRequestProperty("dataType", "json");
        con.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("Access-Control-Allow-Origin","*");
        con.setRequestProperty("Access-Control-Allow-Methods","PUT, GET, POST, DELETE, OPTIONS");
        con.setRequestProperty("Access-Control-Allow-Headers", "Origin, Accept, Content-Type, X-Requested-With, X-CSRF-Token");
        con.setRequestProperty( "charset", "utf-8");
        String urlParameters = "user_question="+message;// $scope.usermessage;

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
     //   System.out.println("\nSending 'POST' request to URL : " + POST_URL);
       // System.out.println("Post parameters : " + urlParameters);
        //System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

        String res=  evaluateResponse(response.toString());

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(state==1){
                    UserBubble bubble = null;
                    try {
                        bubble = new UserBubble("AI",res, "S" );
                        chatController.chatHolder.addRow(chatController.getIDtracker(), bubble.getRoot());
                        Platform.runLater(() -> chatController.messageDisplay.setVvalue(chatController.messageDisplay.getVmax()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    state = 0;
                }
                else if(state==2){
                    sendChatOperator(message);
                    //return;

                }
            }
        });

    }

    public  void SendMessageAIML(String message) throws IOException {
        String POST_URL = Constant.AIML_URL;
        URL obj = new URL(POST_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        //  con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        //    con.setRequestProperty("dataType", "json");
        con.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("Access-Control-Allow-Origin","*");
        con.setRequestProperty("Access-Control-Allow-Methods","PUT, GET, POST, DELETE, OPTIONS");
        con.setRequestProperty("Access-Control-Allow-Headers", "Origin, Accept, Content-Type, X-Requested-With, X-CSRF-Token");
        con.setRequestProperty( "charset", "utf-8");
        String urlParameters = "say="+message;// $scope.usermessage;

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
  //      System.out.println("\nSending 'POST' request to URL : " + POST_URL);
   //     System.out.println("Post parameters : " + urlParameters);
   //     System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result

        String AIML = JSONFormatController.AIMLreadJSON(response.toString());
        System.out.println(AIML);

        String res =  evaluateResponse(AIML);

        Platform.runLater(() -> {
            if(state==1){
                try {
                    SendMessageAI(message);
                    System.out.println("SENDING AI");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(state==0){
                UserBubble bubble = null;
                try {
                    bubble = new UserBubble("AIMIL",res, "S" );
                    chatController.chatHolder.addRow(chatController.getIDtracker(), bubble.getRoot());
                    Platform.runLater(() -> chatController.messageDisplay.setVvalue(chatController.messageDisplay.getVmax()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            else if(state ==2){
                System.out.println("[BOTConsole] ERROR AIML");
                return;
            }
        });

    }

    public  String evaluateResponse(String msg){

        if (msg.indexOf("DIRROUTETOBOT") > -1) {

           System.out.println("[BOTConsole] evaluateResponse: State change to 0(BOT)");
            if (state == 2) {
                //Change state
                state = 0;
                operatorController.closeConnection();
                //destination = '/topic/chat.';
            }

            //Remove directive
            return msg.replace("DIRROUTETOBOT", "Operator Disconnected");

        }
                    /*changed traning bot question*/
        else if (msg.indexOf("DIRDONOTTRAIN") > -1) {
            System.out.println("[BOTConsole] evaluateResponse: Do not train the question & answer");
            return msg.replace("DIRDONOTTRAIN", "");

        } else if (msg.indexOf("DIRROUTETOAI") > -1) {
            state = 1;
            System.out.println("[BOTConsole] evaluateResponse: State change to 1(AI)" + state);
            //Change state


            //Remove directive
            return msg.replace("DIRROUTETOAI", "");

        } else if (msg.indexOf("DIRROUTETOOPERATOR") > -1) {

            String replace = "";
            state =2;

            return msg.replace("DIRROUTETOOPERATOR", replace); //.replace("DIRROUTETOOPERATOR", "");



        } else if (msg.indexOf("DIROPENINTRODUCEDVIOLATIONS") > -1) {

            //Fire event to open introduced violations
            //SetTimeout( $rootScope.$broadcast('appTrigger', "open_introduced_violations"), 5000 );


            //Remove directive
            return msg.replace("DIROPENINTRODUCEDVIOLATIONS", "");

        } else if (msg.indexOf("DIROPENRUNTIMEVIOLATIONS") > -1) {

            //Fire event to open introduced violations


            //Remove directive
            return msg.replace("DIROPENRUNTIMEVIOLATIONS", "");


        } else if (msg.indexOf("DIROPENBEACON") > -1) {

            return msg.replace("DIROPENBEACON", "");

        } else if (msg.indexOf("DIRDONOTHING") > -1) {

            //Do nothing
            return msg.replace("DIRDONOTHING", "");

        } else { //No directives

            return msg;
        }
    }

    public ChatMessage getObjectMessage(String messageText){
        ChatMessage chatMessage =  new ChatMessage();
        chatMessage.setTextMessage(messageText);
        return chatMessage;
    }






}
