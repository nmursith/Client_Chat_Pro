package View;

import java.awt.*;

/**
 * Created by mmursith on 2/16/2016.
 */
public class Mouse  {
    MouseHandler mouseHandler = new MouseHandler();
    public static void main(String[]args){
        //while (true) {
    new Mouse.MouseHandler().start();

        //}
    }

    static class MouseHandler extends Thread{

        Thread thread = this;
        volatile boolean isRunning = true;
        double X;
        public void run() {
            thread = Thread.currentThread();
            isRunning = true;

            //Constant.getRandomString();

            while(isRunning ){
                //   System.out.println("network thread   "+isRunning);
                //System.out.println("Im running");
                X = MouseInfo.getPointerInfo().getLocation().getX();
                try {
                    thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(X);


//            stopThread();
            }
        }

        public  void stopThread(){

            isRunning = false;
            //Thread t = thread;
            thread = null;
            //t.interrupt();
        }
    }

}
