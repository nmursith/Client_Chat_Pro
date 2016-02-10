package Controller;

import Model.Configuration;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

/**
 * Created by mmursith on 12/9/2015.
 */
 public class  ConfigurationController {
    private static Configuration configuration = null;

//    public static void main(String[] args) {
//        ConfigurationController configurationController = new ConfigurationController();
//        //configurationController.writeConfig();
//        System.out.println(configurationController.readConfig().toString());
//
//
//


    static  String filePath = "C:\\vClient\\configuration\\";
    static String fileName = filePath+"config.json";
    public static void writeConfig(Configuration configuration){
        JSONObject obj = new JSONObject();
        obj.put("operator", configuration.getOperator());
        obj.put("topic", configuration.getTopic());
        obj.put("subscription", configuration.getSubscription());
        obj.put("destination", configuration.getDestination());
        obj.put("URL",configuration.getURL());

        //new  FileReader(new FileInputStream());

//        JSONArray list = new JSONArray();
//        list.add("msg 1");
//        list.add("msg 2");
//        list.add("msg 3");
//
//        obj.put("messages", list);

        try {

            FileWriter file = new FileWriter(fileName);
            file.write(obj.toJSONString());
            file.flush();
            file.close();

        } catch (IOException e) {
    //        e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    //    System.out.println(obj);
    }



    public static Configuration readConfig(){
        JSONParser parser = new JSONParser();

        try {


                        //FileReader fileReader = new FileReader(bufferedReader);
            Object obj = parser.parse(new FileReader(fileName));

            configuration = new Configuration();
            JSONObject jsonObject = (JSONObject) obj;


            String operator = (String) jsonObject.get("operator");
            String topic = (String) jsonObject.get("topic");
            String subscription = (String) jsonObject.get("subscription");
            String destination = (String) jsonObject.get("destination");
            String URL = (String) jsonObject.get("URL");

          //  System.out.println(jsonObject);

            configuration.setOperator(operator);
            configuration.setDestination(destination);
            configuration.setSubscription(subscription);
            configuration.setTopic(topic);
            configuration.setURL(URL);

//            System.out.println("Read:       "+URL );
//            System.out.println(destination );
//            System.out.println(operator );
//            System.out.println(topic );
            // loop array
//            JSONArray msg = (JSONArray) jsonObject.get("messages");
//            Iterator<String> iterator = msg.iterator();
//            while (iterator.hasNext()) {
//                System.out.println(iterator.next());
//            }

        } catch (IOException | ParseException e) {
            setConfiguration();
        //    e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return configuration;
    }

    private static void setConfiguration(){
        configuration = new Configuration();
        configuration.setOperator("USER");
        configuration.setDestination("chat.USER");
        configuration.setSubscription("Operator");
        configuration.setTopic("chat.USER");
        configuration.setURL("tcp://cmterainsight:61616?trace=false&soTimeout=60000");
        new File(filePath).mkdir();
        writeConfig(configuration);
    }
}