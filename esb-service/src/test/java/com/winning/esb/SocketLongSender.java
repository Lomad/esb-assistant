package com.winning.esb;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by xuehao on 2017/9/4.
 */
public class SocketLongSender {
    private static final String SERVER_IP = "192.168.33.238";
    private static final Integer SERVER_PORT = 36001;
    private static Socket socket;

    public static void main(String[] args) {
//        start();
//
//        for (int i = 0; i < 3; i++) {
//            send2(String.valueOf(i));
//        }
//
//        stop();

        send();
    }

    public static void start() {
        //创建Socket对象
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void send2(String msg) {
        try {
            System.out.println("客户端开始连接");
            //包体
            String msgTarget = "testtest123 : " + msg;
            byte[] content = msgTarget.getBytes();
            //包头,固定4个字节,包含包体长度信息
            byte[] head = SocketLongReceiverHandler.intToByteArray1(content.length);
            BufferedOutputStream bis = new BufferedOutputStream(socket.getOutputStream());
            bis.write(head);
            bis.flush();
            bis.write(content);
            bis.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void send() {
        Socket socket = null;

        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("客户端开始连接");
            int i=0;
            while (true) {
                //包体
                String msgTarget = "testtest123 : " + i;
                byte[] content = msgTarget.getBytes();
                //包头,固定4个字节,包含包体长度信息
                byte[] head = SocketLongReceiverHandler.intToByteArray1(content.length);
                BufferedOutputStream bis = new BufferedOutputStream(socket.getOutputStream());
                bis.write(head);
                bis.flush();
                bis.write(content);
                bis.flush();

                i++;
                if(i>100)
                    break;

//                Thread.sleep(2000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}