package com.winning.monitor.agent.logging.sender;

import com.winning.monitor.agent.logging.message.MessageTree;

/**
 * Created by nicholasyan on 16/9/8.
 */
public interface IMessageTransport {

    void initialize();

    void sendMessage(MessageTree messageTree);

    void shutdown();

    boolean sendDataAvailable();
}
