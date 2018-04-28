package com.winning.esb;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by xuehao on 2017/9/4.
 */
public class SocketShortReceiver2 {
//    private static final String SERVER_IP = "127.0.0.1";
    private static final Integer SERVER_PORT = 36001;

    public static void main(String[] args) {
        accept();
    }

    /**
     * 短连接接收（客户端发送结束后，必须关闭socket，服务端方可收到信息）
     */
    public static void accept() {
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("服务端已启动，等待客户端连接..");

//            while (true) {
//                SocketShortReceiverHandler2 socketThread = new SocketShortReceiverHandler2(serverSocket);
//                socketThread.start();
//            }

            SocketShortReceiverHandler2 socketThread = new SocketShortReceiverHandler2(serverSocket);
            socketThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}