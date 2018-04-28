package com.winning.esb.service.communication.socket;

import com.winning.esb.service.communication.IPiper;
import com.winning.esb.utils.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("piperSocket")
public class Piper implements IPiper {
    private Logger logger = LoggerFactory.getLogger(Piper.class);

    /**
     * 初始开启一个监听端口
     */
//    @PostConstruct
    private void init() {
        int serverPort = -1;
        try {
            if (!NetUtils.isPort(serverPort)) {
                serverPort = NetUtils.getIdlePort();
            }
            if (NetUtils.isPort(serverPort)) {
//                //保存端口号
//                EsbReceiver.setSocketPort(serverPort);
                //启动监听服务
                startServer();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("启动Socket[端口号: " + serverPort + "]接收端发生异常错误！" + e.getMessage());
        }
    }

    @Override
    public String startServer() {
        String err;
        int serverPort = -1;
        try {
            if (NetUtils.isPort(serverPort)) {
                //ServerSocket serverSocket = new ServerSocket(EsbReceiver.getSocketPort());
//                //设置服务地址
//                EsbReceiver.setUrlSocket(NetUtils.getHostIP() + ":" + serverPort);
                //启动Socket服务端
                //SocketReceiverHandler socketThread = new SocketReceiverHandler(serverSocket);
                //socketThread.start();
            }
            err = null;
        } catch (Exception e) {
            e.printStackTrace();
            err = "启动Socket[端口号: " + serverPort + "]接收端发生异常错误！" + e.getMessage();
//            EsbReceiver.setSocketPort(0);
            logger.error(err);
        }
        return err;
    }

    @Override
    public String receive() {
        return null;
    }

    @Override
    public String send(String msg) {
        return null;
    }

}