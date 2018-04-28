package com.winning.monitor.agent.collector.sender;


/**
 * Created by nicholasyan on 16/9/6.
 */
public interface ICollectDataSenderTask {

    void initialize(ICollectDataMessageTransport messageTransport);

    void start();

    void shutdown();
}
