package com.winning.monitor.agent.sender;

import com.winning.monitor.agent.sender.transport.IMessageTransport;

/**
 * Created by nicholasyan on 16/9/6.
 */
public interface IDataEntitySenderTask {

    void initialize(IMessageTransport messageTransport);

    void start();

    void shutdown();
}
