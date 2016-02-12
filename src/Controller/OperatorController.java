package Controller;

import Model.*;
import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.util.ByteSequence;

import javax.jms.IllegalStateException;
import javax.jms.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by mmursith on 11/24/2015.
 */
public class OperatorController implements MessageListener {

    private volatile Operator operator;
    private BlockingQueue<ChatMessage> chatMessagess;
    private Vector<String> messageProduceID;

    private ChatController controller;
    private MessageConsumer messageConsumer;
    private int messageCounter;
    private volatile int IDtracker;

    private NetworkDownHandler networkHandler;
    private OperatorController operatorController;      //static or volatile or something


    private MessageDistributionHandler messageDistributionHandler;
    private OfflineNetworkDownHandler offlineNetworkDownHandler;
    private volatile boolean isFirstime =true;
    private final Queue<ChatMessage> cachedMessages =  new LinkedList<>();
    private volatile Queue<Notification> pendingNotification;
    private final ExecutorService executor = Executors.newFixedThreadPool(100);  // 100 blink at a time
    private Timer timer;
    private volatile boolean isOnline;
    private boolean isSessionCreated;
    private String subscriptionName;
    private boolean isClosedAlready;
    private static ChatMessage chatMessage;
    private  JSONFormatController jsonFormatController;
    private  PostRequestController postRequestController;
    private boolean isReceived;


    public OperatorController(String subscriptionName, String topicName, ChatController controller) throws JMSException {
        this.operator = new Operator(subscriptionName, topicName);
        this.subscriptionName = subscriptionName;


        this.chatMessagess= new LinkedBlockingQueue<>();
        this.networkHandler = new NetworkDownHandler();
        this.controller = controller.getInstance();
        this.operatorController = this;
        this.offlineNetworkDownHandler = new OfflineNetworkDownHandler();
        this.messageDistributionHandler = new MessageDistributionHandler();

        this.pendingNotification = new LinkedList<>();
        this.jsonFormatController = new JSONFormatController();
        this.isSessionCreated = false;
        this.isClosedAlready = false;
        this.isReceived = false;

        this.messageCounter = -1;
        this.IDtracker = -1;


    }

    protected void startDefaultOperatorAction(){

        try {

/**** single producerID**/

            //controller.setOperatorProducerID(messageProduceID);
            messageConsumer = operator.getSession().createConsumer(getTopic());//, getSubscriptionName());//Constant.operatorID);
            messageConsumer.setMessageListener(this);


//                try{
//
//                    if(networkHandler.isAlive())
//                        networkHandler.stopThread();
//
//                    networkHandler = new NetworkDownHandler();
//                    networkHandler.start();
//                }
//                catch (Exception e){
//                    e.printStackTrace();
//                    //System.out.println("Sleepdetected");
//                }

                try{

                    if(messageDistributionHandler.isAlive())
                        messageDistributionHandler.stopThread();

                    messageDistributionHandler = new MessageDistributionHandler();
                    messageDistributionHandler.start();
                }
                catch (Exception e){
                    e.printStackTrace();
                    //System.out.println("Sleepdetected");
                }




                timer = new Timer();
 /*               TimerTask offlineModeTask = new TimerTask() {

                    @Override
                    public void run() {

                       //                        System.out.println("Timer Working online :  "+ isOnline);


                        try {
                            //if(operatorController.getSesssion()!=null)
                                operatorController.getSesssion().createTextMessage();
                        }
                        catch ( JMSException e){


                                System.out.println("Sleep Mode handling");

                                if(offlineNetworkDownHandler.isAlive()) {

                                    offlineNetworkDownHandler.stopThread();
                                }

                            operatorController.setListener();
                            isOnline = operatorController.operator.isConnected();
                            //      System.out.println("Message Added:  "+ chatMessage.getTextMessage() +"   "+cachedMessages.size());
                                offlineNetworkDownHandler = new OfflineNetworkDownHandler();
                                offlineNetworkDownHandler.start();

                            //e.printStackTrace();
                        }

                        catch (NullPointerException e){
                            e.printStackTrace();
                            System.out.println("operator null");
                           // timer.cancel();

                        }
                    }
                };



                Platform.runLater(() -> timer.schedule(offlineModeTask, 500, 3000));*/




            setClosedAlready(true);

        }
        catch (NullPointerException e){
            e.printStackTrace();
            System.out.println("Already Answering");
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    private void setListener(){
        try {
            operatorController.createSession();
            messageConsumer = operator.getSession().createDurableSubscriber(getTopic(), Constant.getRandomString());//getSubscriptionName());
            messageConsumer.setMessageListener(this);
        } catch (JMSException e) {
         //   e.printStackTrace();
        }

    }
    public void sendMessage(ChatMessage chatMessage,OperatorController operator) throws JMSException {

        // check if a message was received
        if (chatMessage != null ) {
            if(!operator.isSessionCreated()){
                operator.createSession();
                System.out.println("operator session:  " + operator.getSesssion());
            }


            if(operator.getSesssion()==null){
                operator.setSubscriptionName(operator.getSubscriptionName()+Constant.getRandomString());
                operator.createSession();
                System.out.println("null and operator session:  " + operator.getSesssion());

            }

            try{

                operator.getSesssion().createTextMessage();
            }
            catch(IllegalStateException e){
                operator.createSession();
                isOnline = operator.operator.isConnected();
            }
            catch (NullPointerException e ){
                e.printStackTrace();
            }


                try{

                   // System.out.println("opreato session :  "+controller.getHashMapOperator().get(defaultOperator).getOperatorController().getSesssion());
                    System.out.println("Message: "+ chatMessage.getTextMessage());
                    TextMessage response =   operator.getSesssion().createTextMessage(); //controller.getHashMapOperator().get(controller.getDefaultOperator()).getOperatorController().getSesssion().createTextMessage();
                    String myMessage = chatMessage.getTextMessage();
                    //System.out.println("message: "+ myMessage);
                    myMessage = myMessage.trim().equalsIgnoreCase("exit") ? "DIRROUTETOBOT":myMessage;
                    myMessage = jsonFormatController.createJSONmessage("NIFRAS",myMessage);
                    response.setText(myMessage);
                    //System.out.println("offline:   "+myMessage);
                    //System.out.println("offline:   "+myMessage);
                    String random = null;
         //           String JMSmessageID = Constant.JMSmessageID;
                    response.setJMSCorrelationID(random);

                    //response.acknowledge();

               //     response.setJMSMessageID(JMSmessageID);
                    //System.out.println("Getting producer");
                    operator.getMessageProducer().send(response);
                    //System.out.println("fine: "+ operator.getMessageProducer());

                }

                catch (IllegalStateException e){
                //e.printStackTrace();
//                  if(!isOnline){
                        //isOnline = false;
                      System.out.println("network handling while sending");
                      if(offlineNetworkDownHandler.isAlive()) {

                          offlineNetworkDownHandler.stopThread();
                      }

                      cachedMessages.add(chatMessage);
                      //      System.out.println("Message Added:  "+ chatMessage.getTextMessage() +"   "+cachedMessages.size());
                      offlineNetworkDownHandler = new OfflineNetworkDownHandler();
                      offlineNetworkDownHandler.start();
//                  }

                }
            catch (NullPointerException e){
             //   e.printStackTrace();
                System.out.println("Error caused  " +e.getClass() );

            }


        }

    }

     @Override
    public void onMessage(Message message) {
        String producerID = null;
        String correlationID =null;

        try {

            if (message instanceof TextMessage) {
                //System.out.println("Object: "+message.toString());
                TextMessage txtMsg = (TextMessage) message;
                String messageText = txtMsg.getText();

                String [] jsoNmessage = jsonFormatController.readJSONmessage(messageText);
                messageText = jsoNmessage[0];
                String owner = jsoNmessage[1];

                if(postRequestController!=null){
                   messageText =postRequestController.evaluateResponse(messageText);

                }
                System.out.println("Recieving......:      "+messageText);
                String destination = message.getJMSDestination().toString();
                destination = destination.substring(destination.indexOf('.') + 1);
                //                System.out.println("destination: "+ destination);
                producerID = destination;
                correlationID = message.getJMSCorrelationID();
                chatMessage =  new ChatMessage();
                chatMessage.setProducerID(producerID);
                chatMessage.setMessage(message);
                chatMessage.setTextMessage(messageText);
                chatMessage.setOwner(owner);
                this.chatMessagess.add(chatMessage);


            }
            else if (message instanceof ActiveMQBytesMessage){

                //System.out.println(text.getText());
                     ActiveMQBytesMessage activeMQBytesMessage = (ActiveMQBytesMessage) message;

                    String destination = ((ActiveMQBytesMessage) message).getDestination().getPhysicalName();

                    destination = destination.substring(destination.indexOf('.') + 1);

                    //                System.out.println("destination: "+ destination);
                    producerID = destination;
                    //producerID = activeMQBytesMessage.getProducerId().getConnectionId();

                    //                if(producerID!=null){
                    //                    producerID = producerID.replace("-","");
                    //                    producerID = producerID.replace(":","");
                    //                }

                    ByteSequence byteSequence = activeMQBytesMessage.getContent();
                    byte[] bytes = byteSequence.getData();
                    String messageText = new String(bytes, StandardCharsets.UTF_8);
                    String [] jsoNmessage = jsonFormatController.readJSONmessage(messageText);

                    messageText = jsoNmessage[0];
                    String owner = jsoNmessage[1];
                    correlationID = message.getJMSCorrelationID();
         //       System.out.println(messageText);

                if(postRequestController!=null){
                    messageText =postRequestController.evaluateResponse(messageText);

                }

                System.out.println("Recieving... byte...:      "+messageText);

                    chatMessage = new ChatMessage();
                    chatMessage.setProducerID(producerID);

                    chatMessage.setMessage(message);



                    chatMessage.setTextMessage(messageText);
                    chatMessage.setOwner(owner);

                //chatMessage.setTextMessage(messageText);
                    //                System.out.println("From Byte Client: "+ chatMessage.getTextMessage());

                    if(!messageText.contains(Constant.BOT_TAG) && !messageText.contains(Constant.HISTORY_TAG))
                    this.chatMessagess.add(chatMessage);

//                System.out.println(chatMessagess.isEmpty());
            }

            System.out.println(correlationID +"         PRODUCERID :  "+  producerID);

            if(correlationID==null){





                OperatorController operatorController = new OperatorController(producerID, Constant.topicPrefix+producerID, controller);
//                bindOperator.getChatHolder().addRow(0, SeperatorLine.getSeperator());//oldhistory);

//                if(!producerID.equals(defaultOperator))
//                    operatorController.getMessageConsumer().setMessageListener(null);


                if(chatMessage.getOwner()!=null || chatMessage.getOwner()!="") {

                }
                else {

                }

                System.out.println("Setting client Name from Mesasge: "+ chatMessage.getOwner());

                //Thread.sleep(20);





               }
            else {

                setENDisableUI(producerID);
               // System.out.println(controller.getHashMapOperator().get(producerID).getChatHolder().isDisabled());

            }










        } catch (Exception e){
            e.printStackTrace();
        }
         isReceived = true;
    }

    protected void setENDisableUI(String producerID){
        boolean isdisable = false;
        try{

        }
        catch (NullPointerException e){
     //       e.printStackTrace();
        }
        catch (Exception e){
            //       e.printStackTrace();
        }

    }




    private void routeChat() throws JMSException {

        String reply;
        Queue<ChatMessage> durablechatMessage;

        ChatMessage chatMessage;



        durablechatMessage = getChatMessagess();


            if(!durablechatMessage.isEmpty()) {
                while (!durablechatMessage.isEmpty()) {
                    //                  System.out.println("internal");
                    try {

                        chatMessage =durablechatMessage.remove();
                        reply = chatMessage.getTextMessage();



                        String correID = chatMessage.getMessage().getJMSCorrelationID();


                    //    System.out.println("chatmessage ID: "+chatMessage.getProducerID());

                        if(reply.equalsIgnoreCase(Constant.exitMessage))
                            reply = Constant.exitBubbleMessage;
                        else if(reply.equalsIgnoreCase(Constant.exitUserMessage))
                            reply = Constant.exitUserBubbleMessage;

                        try {
                            //|| !correID.equals(Constant.correalationID)

                                if (correID != null) {

                                    if (!correID.equalsIgnoreCase(Constant.correalationID)) {
                                        if (!chatMessage.getTextMessage().equals(Constant.exitMessage)) {

                                            UserBubble bubble = new UserBubble(chatMessage.getOwner(), chatMessage.getTextMessage(), chatMessage.getTime());
                                            controller.chatHolder.addRow(controller.getIDtracker(), bubble.getRoot());


                                        } else {

                                            if (chatMessage.getTextMessage().equals(Constant.exitMessage)) {
                                                chatMessage.setTextMessage(Constant.exitBubbleMessage);

                                            }

                                            OperatorBubble bubble = new OperatorBubble(chatMessage.getOwner(), chatMessage.getTextMessage(), chatMessage.getTime());
                                            controller.chatHolder.addRow(controller.getIDtracker(), bubble.getRoot());

                                            //    GridPane.setHalignment(bubble.getFromBubble(), HPos.RIGHT);



                                        }

                                        Platform.runLater(() -> controller.messageDisplay.setVvalue(controller.messageDisplay.getVmax()));
                                    }

//                                else {
//                                    UserBubble bubble = new UserBubble(username, chatMessage.getTextMessage(), chatMessage.getTime());
//                                    //         GridPane.setHalignment(bubble.getToBubble(), HPos.LEFT);
//                                    bindOperator.getChatHolder().addRow(ID, bubble.getRoot());
//                                }
                                }



                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                    catch (NullPointerException e){
                        e.printStackTrace();
                        System.out.println("Null");
                      //  e.printStackTrace();
                        //   break;
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }

    }

    public PostRequestController getPostRequestController() {
        return postRequestController;
    }

    public void setPostRequestController(PostRequestController postRequestController) {
        this.postRequestController = postRequestController;
    }

    public int getMessageCounter() {
        operatorController.messageCounter = operatorController.messageCounter+1;
        return operatorController.messageCounter;
    }

    public Operator getOperator() {
        return operator;
    }

    public GridPane getGridPane() {

        return controller.getGridPane();
    }

    public void setSubscriptionName(String subscriptionName) {
        operator.setSubscriptionName(subscriptionName);
    }

    protected void createSession(){
        operator.create();
        setSessionCreated(true);
    }

    public ChatController getController() {
        return controller;
    }

    protected BlockingQueue<ChatMessage> getChatMessagess() {
        return operatorController.chatMessagess;
    }

    private Topic getTopic() {
        return operatorController.operator.getTopic();
    }

    public Vector<String> getMessageProduceID() {
        return operatorController.messageProduceID;
    }

    public void setMessageProduceID(Vector<String> messageProduceID) {
        this.messageProduceID = messageProduceID;
    }

    public void closeConnection()  {

        try{
            setSessionCreated(false);

            operatorController.operator.closeConnection();
            messageConsumer = null;
            setSesssion(null);
            setClosedAlready(false);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setFirstime(boolean firstime) {
        isFirstime = firstime;
    }

    public void setChatMessagess(BlockingQueue<ChatMessage> chatMessagess) {
        this.chatMessagess = chatMessagess;
    }

    public boolean isFirstime() {
        return isFirstime;
    }

    public Queue<ChatMessage> getCachedMessages() {
        return cachedMessages;
    }

    private void setOperatorController(OperatorController operatorController) {
        this.operatorController = operatorController;
    }

    public String getSubscriptionName() {
        return operator.getSubscriptionName();
    }

    protected Session getSesssion() {
        return operator.getSession();
    }
    protected void setSesssion(Session session) {
                operator.setSession(null);
    }

    public static String getDefaultOperator() {
        return "NIFRAS";
    }

    private MessageProducer getMessageProducer() {
        return operator.getMessageProducer();
    }

    private MessageConsumer getMessageConsumer() {
        return messageConsumer;
    }
    protected void setMessageCounter(int messageCounter) {
        this.messageCounter = messageCounter;
    }

    public synchronized int getIDtracker() {
        IDtracker = IDtracker+1;
        System.out.println("ID:  "+IDtracker);
        return IDtracker;

    }

    public MessageDistributionHandler getMessageDistributionHandler() {
        return messageDistributionHandler;
    }

    public boolean isSessionCreated() {
        return isSessionCreated;
    }

    public void setSessionCreated(boolean sessionCreated) {
        isSessionCreated = sessionCreated;
    }

    public void setIDtracker(int IDtracker) {
        this.IDtracker = IDtracker;
    }

    public boolean isClosedAlready() {
        return isClosedAlready;
    }

    public void setClosedAlready(boolean closedAlready) {
        isClosedAlready = closedAlready;
    }

    public Timer getTimer() {
        return timer;
    }

    public NetworkDownHandler getNetworkHandler() {
        return networkHandler;
    }

    public OfflineNetworkDownHandler getOfflineNetworkDownHandler() {
        return offlineNetworkDownHandler;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    class OfflineNetworkDownHandler extends Thread{


        Thread thread = this;
//        String ID = Constant.getRandomString();

        public void run() {
            //super.run();
        //thread = Thread.currentThread();
        System.out.println("isOnline:    "+isOnline);

        synchronized (cachedMessages) {

            if (isOnline ) {
                try {

                    System.out.println("contained:   " + cachedMessages.size());

                    while (!cachedMessages.isEmpty()) {

                       ChatMessage chatMessage = cachedMessages.remove();


                    }
                //    stop();
                    stopThread();



                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        }

        }

        public  void stopThread(){
//            thread.stop();

            thread = null;

        }

    }


    class NetworkDownHandler extends Thread{

        Thread thread = this;
        volatile boolean isRunning = true;
        String ID = Constant.getRandomString();
        public void run() {
            thread = Thread.currentThread();
            isRunning = true;

           //Constant.getRandomString();

            while(isRunning ){
             //   System.out.println("network thread   "+isRunning);
                //System.out.println("Im running");


            try {

                Operator operator = new Operator(ID, ID);
                operator.create();
                boolean isConnected = operator.isConnected();
                operator.closeConnection();
                //         System.out.println("inside:  " + isOnline);
//                    Thread.sleep(100);

                if (isConnected) {

                    isOnline = true;


                } else {

                    isOnline = false;
                    System.out.println("Resolving connection...");

                }
                operator = null;
            } catch (IllegalStateException e) {
              //  isOnline = false;
                try {
                    thread.sleep(100);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
              //  controller.statusImageView.setImage(image_offline);
            } catch (JMSException e) {
              //  isOnline = false;
                try {

                    thread.sleep(100);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
             //   controller.statusImageView.setImage(image_offline);
            }
                catch (Exception e){
                    isOnline =false;
               //     System.out.println("NULL Operator");
                    try {

                        thread.sleep(100);

                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }


                }
                try {

                    thread.sleep(200);
                } catch (InterruptedException e1) {
                   // e1.printStackTrace();
                }
        }

//            stopThread();
        }

        public  void stopThread(){

            isRunning = false;
            //Thread t = thread;
            thread = null;
            //t.interrupt();
        }
    }



    class MessageDistributionHandler extends Thread{
        boolean  isRunning= true;
        Thread thread = this;

        public void run() {

            thread = Thread.currentThread();
            isRunning = true;

            while(isRunning){
                try {
                  //  System.out.println("Distributing:     "+    chatMessagess.size());
                    if(!chatMessagess.isEmpty()){
                        Platform.runLater(() -> {
                            try {
                                if(isReceived)
                                    routeChat(); //routeMessagetoThread();
                            } catch (JMSException e) {
                                e.printStackTrace();
                            }
                            catch (Exception e) {
//                                e.printStackTrace();
                            }

                        });

                    }

                }
                catch (Exception e){
                    e.printStackTrace();
                }
                try {

                    thread.sleep(100);
                } catch (InterruptedException e1) {
                    // e1.printStackTrace();
                }
            }

//            stopThread();
        }

        public  void stopThread(){
            isRunning = false;
            Thread t = thread;
            thread.stop();
            thread = null;
            t.interrupt();
        }
    }



}
