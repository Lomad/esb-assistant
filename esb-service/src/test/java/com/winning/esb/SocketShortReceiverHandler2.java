package com.winning.esb;

import com.winning.esb.utils.DateUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Socket多线程处理类 用来处理服务端接收到的客户端请求（处理Socket对象）
 */
public class SocketShortReceiverHandler2 extends Thread {
    private ServerSocket serverSocket;
    private Socket socket;

    public SocketShortReceiverHandler2(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void run() {
        accept();
    }

    /**
     * 短连接接收（客户端发送结束后，必须关闭socket，服务端方可收到信息）
     */
    public void accept() {
        try {
            while (true) {
                socket = serverSocket.accept();// 侦听并接受到此套接字的连接,返回一个Socket对象
                run2();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 方式2
     */
    private void run2() {
        InputStream is = null;
        OutputStream os = null;
        ByteArrayOutputStream outSteam = null;
        String responseMsg;
        try {
//            socket.setSoTimeout(5000);  //设置超时时间
            is = socket.getInputStream();
            os = socket.getOutputStream();
            outSteam = new ByteArrayOutputStream();
            String receiveMsg;
            byte[] data = new byte[2048];
            // 读取客户端数据
            int len = 0;
            boolean endMark = false;
            while ((len = is.read(data)) != -1) {
                outSteam.write(data, 0, len);
                byte[] str = outSteam.toByteArray();
                for (int i = 0; i < str.length - 1; i++) {
                    if (str[i] == (char) 28 && str[i + 1] == (char) 13) {
                        receiveMsg = outSteam.toString().trim();
                        System.out.println("已接收到客户端连接");
                        System.out.println("服务端接收到客户端信息：" + receiveMsg + ",当前客户端ip为："
                                + socket.getInetAddress().getHostAddress());

                        // 向客户端回复信息
                        responseMsg = "当前时间：" + DateUtils.getCurrentDatetimeString();
                        responseMsg = (char) 11 + responseMsg + (char) 28 + (char) 13;
                        os.write(responseMsg.getBytes());

                        System.out.println("已返回应答信息：" + responseMsg);

                        endMark = true;
                    }
                }

                if(endMark) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                responseMsg = "当前时间：" + DateUtils.getCurrentDatetimeString() + "，未检测到结束标志，已超时。";
                responseMsg = (char) 11 + responseMsg + (char) 28 + (char) 13;
                os.write(responseMsg.getBytes());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (socket != null) {
                    socket.shutdownOutput();// 关闭输出流
                    socket.close();
                }
                if(outSteam != null) {
                    outSteam.close();
                }
                if(os != null) {
                    os.close();
                }
                if(is != null) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}