package com.winning.esb.service.communication.socket;

import com.winning.esb.model.enums.SvcStructureEnum;
import com.winning.esb.utils.DateUtils;
import com.winning.esb.utils.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Socket处理类 用来处理服务端接收到的客户端请求（处理Socket对象）
 *
 * @author xuehao
 */
public class SocketReceiverHandler extends Thread {
    private ServerSocket serverSocket;
    private Socket socket;

    public SocketReceiverHandler(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println("服务端已启动，等待客户端连接..");

                //侦听并接受到此套接字的连接,返回一个Socket对象
                socket = serverSocket.accept();
                //接收信息
                receive();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收信息
     */
    public void receive() {
        InputStream is = null;
        OutputStream os = null;
        ByteArrayOutputStream outSteam = null;
        String responseMsg;
        try {
//            //设置超时时间
//            socket.setSoTimeout(EsbReceiver.getSocketTimeout());
            is = socket.getInputStream();
            os = socket.getOutputStream();
            outSteam = new ByteArrayOutputStream();
            String requestMsg;
            int len = 0;
            //结束标志
            boolean endMark = false;
            //读取长度
            byte[] data = new byte[2048];
            // 读取客户端数据
            while ((len = is.read(data)) != -1) {
                outSteam.write(data, 0, len);
                byte[] str = outSteam.toByteArray();
                for (int i = 0; i < str.length - 1; i++) {
                    if (str[i] == (char) 28 && str[i + 1] == (char) 13) {
                        requestMsg = outSteam.toString(StringUtils.Charset_UTF_8).trim();
                        fillCommonMsg(requestMsg, SvcStructureEnum.DirectionEnum.In);

                        System.out.println("已接收到客户端连接");
                        System.out.println("服务端接收到客户端信息：" + requestMsg + ",当前客户端ip为："
                                + socket.getInetAddress().getHostAddress());

                        // 向客户端回复信息
                        responseMsg = "当前时间：" + DateUtils.getCurrentDatetimeString();
                        responseMsg = (char) 11 + responseMsg + (char) 28 + (char) 13;

                        fillCommonMsg(responseMsg, SvcStructureEnum.DirectionEnum.Ack);

//                        os.write(responseMsg.getBytes());
                        PrintWriter pw = new PrintWriter(os);
                        pw.write(responseMsg);
                        pw.flush();
//                        pw.close();

                        System.out.println("已返回应答信息：" + responseMsg);

                        endMark = true;
                    }
                }

                if (endMark) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                responseMsg = "当前时间：" + DateUtils.getCurrentDatetimeString() + "，未检测到结束标志，已超时。";
                responseMsg = (char) 11 + responseMsg + (char) 28 + (char) 13;

                fillCommonMsg(responseMsg, SvcStructureEnum.DirectionEnum.Ack);

                os.write(responseMsg.getBytes());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (outSteam != null) {
                    outSteam.close();
                }
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
                if (socket != null && !socket.isClosed()) {
                    socket.shutdownOutput();// 关闭输出流
                    socket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 填充公共变量的消息内容
     */
    private void fillCommonMsg(String msg, SvcStructureEnum.DirectionEnum directionEnum) {
//        if(EsbReceiver.getSvcType() == SvcUrlEnum.SvcTypeEnum.Socket.getCode()) {
//            //设置日志模型
//            SimulationTestStepLogModel logModel = EsbReceiver.getLogModel();
//            if (logModel == null) {
//                logModel = new SimulationTestStepLogModel();
//                //更新公共变量
//                EsbReceiver.setLogModel(logModel);
//            }
//            if(SvcStructureEnum.DirectionEnum.In.getCode() == directionEnum.getCode()) {
//                logModel.setOut_msg(msg);
//            } else {
//                logModel.setAck_msg(msg);
//            }
//        }
    }

}