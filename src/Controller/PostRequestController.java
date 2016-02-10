package Controller;

import Model.Constant;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mmursith on 2/10/2016.
 */
public class PostRequestController {
    public static int state;

    public static void main(String[]args){
        try {

            System.out.println("RESOASDFAD:   "+SendMessageAI("what is insightlive?"));
            System.out.println("RESOASDFAD:   "+SendMessageAIML("can you take me to the beacon?"));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public static String SendMessageAI(String message) throws IOException {

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
    //    System.out.println(response.toString());

        return evaluateResponse(response.toString());

    }

    public static String SendMessageAIML(String message) throws IOException {
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
        //System.out.println(response.toString());
        String AIML = JSONFormatController.AIMLreadJSON(response.toString());
        return evaluateResponse(AIML);

    }

    public static String evaluateResponse(String msg){

        if (msg.indexOf("DIRROUTETOBOT") > -1) {

           System.out.println("[BOTConsole] evaluateResponse: State change to 0(BOT)");
            if (state == 2) {
                //Change state
                state = 0;

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

            System.out.println("[BOTConsole] evaluateResponse: State change to 1(AI)");
            //Change state
            state = 1;

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

            //Fire event to open introduced violations


            //Remove directive
            return msg.replace("DIROPENBEACON", "");

        } else if (msg.indexOf("DIRDONOTHING") > -1) {

            //Do nothing
            return msg.replace("DIRDONOTHING", "");

        } else { //No directives

            return msg;
        }
    }








}
