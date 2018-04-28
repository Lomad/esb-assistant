package com.winning.monitor.agent.logging.task;

import com.winning.monitor.agent.logging.entity.ConfigManager;
import com.winning.monitor.agent.logging.sender.IMessageTransport;
import com.winning.monitor.agent.logging.sender.netty.NettyMessageTreeTransport;
import com.winning.monitor.agent.logging.storage.MessageTreeStorage;

/**
 * Created by nicholasyan on 16/9/8.
 */
public class MessageTreeSenderTaskManager {

    private final ConfigManager configManager;
    private final MessageTreeSenderTask messageTreeSenderTask;
    private final MessageTreeStorage messageTreeStorage;
    private IMessageTransport messageTransport;

    public MessageTreeSenderTaskManager(ConfigManager configManager,
                                        MessageTreeStorage messageTreeStorage) {
        this.configManager = configManager;
        this.messageTreeStorage = messageTreeStorage;
        this.messageTreeSenderTask = new MessageTreeSenderTask(this.messageTreeStorage, 20);
    }

    public void initialize() {
        String servers = configManager.getSenderConfig().getServers();
        this.messageTransport = new NettyMessageTreeTransport(servers, this.messageTreeStorage);
        this.messageTransport.initialize();
        this.messageTreeSenderTask.initialize(this.messageTransport);
    }


    public void start() {
        this.messageTreeSenderTask.start();
    }


    public void shutdown() {
        this.messageTreeSenderTask.shutdown();
        this.messageTransport.shutdown();
    }


}
