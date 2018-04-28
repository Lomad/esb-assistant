package com.winning.esb;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by xuehao on 2017/9/4.
 */
public class SocketShortReceiver3 {
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

            while (true) {
                Socket socket = serverSocket.accept();// 侦听并接受到此套接字的连接,返回一个Socket对象
                SocketShortReceiverHandler socketThread = new SocketShortReceiverHandler(socket);
                socketThread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}