package Model;

import java.util.Random;

/**
 * Created by mmursith on 12/13/2015.
 */
public class Constant {

    public static final String AIML_URL = "http://cmterainsight/aiml/conversation_start.php";
    public static final String AI_URL = "http://cmterainsight/aibot1";
    public static final String DIRDEVELOPERSTART = "DIRDEVELOPERSTART";
    public static String correalationID = "60e232e2a20efb61";
    public static final String JMSmessageID = "60e232e2a20tef54";

    

    public static final String exitMessage ="DIRROUTETOBOT";
    public static final String exitUserMessage ="DIRDISCONNECT";
    public static final String  exitBubbleMessage = "Chat closed by Operator";
    public static final String  exitUserBubbleMessage = "Chat closed by User";
    public static final String  HISTORY_TAG = "VARHISTORY";
    public static final String  BOT_TAG = "BOT";
    public static final String  DO_NOT_TRAIN_TAG = "DIRDONOTTRAIN ";
    public static final String topicPrefix = "chat.";
    public static final String operatorhistoryID =  "60e232e2a20tafwe";
    public static final String ConnectedMessage = "Operator Connected";
    public static final String DisConnectedMessage = "Operator Disconnected";
    public static final String Annonymus = "USER";
    public static final String ID_O = "0";
    public static final String ID_U = "1";


    public static String [] usernames = {"Operator","Pubudu", "Shannon", "Nimashi","Damith","Mursith","Januka","Prabudhika","Thuan","Sameera"};

    public static String getRandomString() {

        Random random = new Random(System.currentTimeMillis());
        long randomLong = random.nextLong();

        return Long.toHexString(randomLong);
    }

  }
