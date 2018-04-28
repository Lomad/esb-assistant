package com.winning.monitor.agent.sender.transport.factory;

import com.winning.monitor.agent.config.sender.SenderConfig;
import com.winning.monitor.agent.sender.transport.IMessageTransport;

/**
 * Created by nicholasyan on 16/9/7.
 */
public interface IMessageTransportCreator {

    IMessageTransport createMessageTransport(SenderConfig senderConfig);

}
