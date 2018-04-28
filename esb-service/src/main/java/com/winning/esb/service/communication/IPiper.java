package com.winning.esb.service.communication;

/**
 * 信息通道，主要用于接收、发送信息
 */
public interface IPiper {
    /**
     * 启动服务接收端
     */
    String startServer();

    /**
     * 接收从客户端发来的信息
     */
    String receive();

    /**
     * 发送信息到远端的服务端
     */
    String send(String msg);
}