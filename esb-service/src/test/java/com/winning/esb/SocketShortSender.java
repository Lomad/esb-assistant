package com.winning.esb;

import java.io.*;
import java.net.Socket;

/**
 * Created by xuehao on 2017/9/4.
 */
public class SocketShortSender {
    private static final String SERVER_IP = "127.0.0.1";
    private static final Integer SERVER_PORT = 18111;

    public static void main(String[] args) {
        send();
    }

    /**
     * 短连接发送（发送结束后，必须关闭socket，服务端方可收到信息）
     */
    public static void send() {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            //根据输入输出流和服务端连接
            OutputStream outputStream = socket.getOutputStream();//获取一个输出流，向服务端发送信息
            PrintWriter printWriter = new PrintWriter(outputStream);//将输出流包装成打印流
            printWriter.print((char)11 + "MSH|^~\\&|LIS|RuiSi|HIS|WinningSoft|20140113155322||ORU^R01|123456789|P|2.5.1|||||CHN\n" +
                    "PID|1||PatID~病历号~卡号||患者姓名||19880311112318|M\n" +
                    "PV1|1|I|^^床位号^^^^病区编码^病区名称||||||||||||||||首页序号或挂号序号\n" +
                    "ORC|NW|||^^申请单序号\n" +
                    "OBR|1|明细序号|报告单号(第三方)|^^^^^医技||20140113155811|||||||||&样本|申请医生ID^申请医生姓名||申请科室代码|申请科室名称|执行科室代码|执行科室名称|20140113170123||LIS|F|||||||||检查医生ID&检查医生姓名||20140113171111\n" +
                    "\n"+ (char)28 + (char)13);
            printWriter.flush();
            socket.shutdownOutput();//关闭输出流

            InputStream inputStream = socket.getInputStream();//获取一个输入流，接收服务端的信息
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);//包装成字符流，提高效率
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);//缓冲区
            String info = "";
            String temp = null;//临时变量
            while ((temp = bufferedReader.readLine()) != null) {
                info += temp;
                System.out.println("客户端接收服务端发送信息：" + info);
            }

            //关闭相对应的资源
            bufferedReader.close();
            inputStream.close();
            printWriter.close();
            outputStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}