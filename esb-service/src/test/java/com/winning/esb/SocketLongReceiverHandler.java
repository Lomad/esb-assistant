package com.winning.esb;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.DefaultXMLParser;
import ca.uhn.hl7v2.parser.PipeParser;
import com.winning.esb.model.SvcStructureModel;
import com.winning.esb.model.ValueListModel;
import com.winning.esb.model.common.TreeModel;
import com.winning.esb.model.enums.SimulationTestLogEnum;
import com.winning.esb.model.enums.SvcStructureEnum;
import com.winning.esb.service.msg.IParser;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.RegexUtils;
import com.winning.esb.utils.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuehao on 2017/10/13.
 */
public class SocketLongReceiverHandler implements Runnable {
    public static int count = 0;
    Socket socket = null;

    public SocketLongReceiverHandler(Socket socket) {
        count++;
        this.socket = socket;
        System.out.println("用户" + count + "接入");
    }

    @Override
    public void run() {
        run2();
    }

    /**
     * 方式1
     */
    private void run1() {
        BufferedInputStream bis = null;
        try {
//            bis = new BufferedInputStream(socket.getInputStream());
//            while (true) {
//                byte[] head = new byte[4];
//                bis.read(head);
//                byte[] data = new byte[byteArrayToInt(head)];
//                bis.read(data);
//                System.out.println(new String(data).trim());
//            }

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

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bis.close();
//                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 方式2
     */
    private void run2() {
        try {
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();
            String responseMsg;
            String receMsg;
            byte[] data = new byte[2048];
            // 读取客户端数据
            int len = 0;
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            while ((len = is.read(data)) != -1) {
                outSteam.write(data, 0, len);
                byte[] str = outSteam.toByteArray();
                for (int i = 0; i < str.length; i++) {
                    if (str[i] == (char) 28 && str[i + 1] == (char) 13) {
                        receMsg = outSteam.toString().trim();
                        System.out.println("已接收到客户端连接");
                        System.out.println("服务端接收到客户端信息：" + receMsg + ",当前客户端ip为："
                                + socket.getInetAddress().getHostAddress());

                        // 向客户端回复信息
                        responseMsg = "";
                        String respMsg = (char) 11 + responseMsg + (char) 28 + (char) 13;
                        os.write(respMsg.getBytes());

                        System.out.println("已返回应答信息：" + respMsg);
                    }
                }
            }

            socket.shutdownOutput();// 关闭输出流
            outSteam.close();
            os.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //int 转字节数组
    public static byte[] intToByteArray1(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    public static byte[] intToByteArray2(int i) throws Exception {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buf);
        out.writeInt(i);
        byte[] b = buf.toByteArray();
        out.close();
        buf.close();
        return b;
    }

    //字节数组转int
    public static int byteArrayToInt(byte[] b) {
        int intValue = 0;
        for (int i = 0; i < b.length; i++) {
            intValue += (b[i] & 0xFF) << (8 * (3 - i));
        }
        return intValue;
    }

}