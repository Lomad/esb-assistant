package com.winning.esb;

import com.winning.esb.utils.DateUtils;

import java.io.*;
import java.net.Socket;

/**
 * Socket多线程处理类 用来处理服务端接收到的客户端请求（处理Socket对象）
 */
public class SocketShortReceiverHandler3 extends Thread {
    private Socket socket;

    public SocketShortReceiverHandler3(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        run2();
    }

    private void run1() {
        // 根据输入输出流和客户端连接
        try {
            InputStream inputStream = socket.getInputStream();
            // 得到一个输入流，接收客户端传递的信息
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);// 提高效率，将自己字节流转为字符流
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);// 加入缓冲区
            String temp = null;
            String info = "";
            while ((temp = bufferedReader.readLine()) != null) {
                info += temp;
                System.out.println("已接收到客户端连接");
                System.out.println("服务端接收到客户端信息：" + info + ",当前客户端ip为："
                        + socket.getInetAddress().getHostAddress());
            }

            OutputStream outputStream = socket.getOutputStream();// 获取一个输出流，向服务端发送信息
            PrintWriter printWriter = new PrintWriter(outputStream);// 将输出流包装成打印流
            printWriter.print("你好，服务端已接收到您的信息");
            printWriter.flush();
            socket.shutdownOutput();// 关闭输出流

            // 关闭相对应的资源
            bufferedReader.close();
            inputStream.close();
            printWriter.close();
            outputStream.close();
        } catch (IOException e) {
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
            socket.setSoTimeout(5000);  //设置超时时间
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
                        receiveMsg = outSteam.toString("UTF-8").trim();
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
                // 关闭输出流
                if (socket != null) {
                    socket.shutdownOutput();
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
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}