package com.winning.monitor.agent.collector.sender;


import com.winning.monitor.agent.collector.sender.io.NettyCollectDataMessageTransport;
import com.winning.monitor.agent.config.sender.ISenderConfigFactory;
import com.winning.monitor.agent.config.sender.SenderConfig;
import com.winning.monitor.agent.sender.IDataEntityStorage;
import com.winning.monitor.agent.sender.transport.factory.MessageTransportFactory;

/**
 * Created by nicholasyan on 16/9/7.
 */
public class CollectDataSenderContext {

    private final ISenderConfigFactory senderConfigFactory;
    private final MessageTransportFactory messageTransportFactory;
    //private IMessageTransport messageTransport;
    private ICollectDataMessageTransport collectDataMessageTransport;
    private IDataEntityStorage dataEntityStorage;

    private boolean initialed = false;

    public CollectDataSenderContext(ISenderConfigFactory senderConfigFactory,
                                    MessageTransportFactory messageTransportFactory) {
        this.senderConfigFactory = senderConfigFactory;
        this.messageTransportFactory = messageTransportFactory;
    }

    public void initialize() {
        SenderConfig senderConfig = this.senderConfigFactory.getSenderConfig();
        this.collectDataMessageTransport = new
                NettyCollectDataMessageTransport(senderConfig.getServers(), dataEntityStorage);
//        this.collectDataMessageTransport =
//        this.messageTransport = this.messageTransportFactory.createMessageTransport(senderConfig);
//        this.collectDataMessageTransport

    }

    public ICollectDataMessageTransport getMessageTransport() {
        return this.collectDataMessageTransport;
    }


}
