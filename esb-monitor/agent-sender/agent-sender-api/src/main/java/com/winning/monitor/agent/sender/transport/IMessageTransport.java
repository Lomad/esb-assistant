package com.winning.monitor.agent.sender.transport;

import com.winning.monitor.message.MessagePackage;

/**
 * Created by nicholasyan on 16/9/6.
 */
public interface IMessageTransport {

    void initialize();

    void sendMessage(MessagePackage messagePackage);

    void shutdown();
}