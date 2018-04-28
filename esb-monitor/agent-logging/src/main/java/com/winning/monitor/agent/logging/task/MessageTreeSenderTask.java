package com.winning.monitor.agent.logging.task;

import com.winning.monitor.agent.logging.message.MessageTree;
import com.winning.monitor.agent.logging.sender.IMessageTransport;
import com.winning.monitor.agent.logging.storage.MessageTreeStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by nicholasyan on 16/9/6.
 */
public class MessageTreeSenderTask extends AbstractDataSenderTask<MessageTree> {

    private static Logger logger = LoggerFactory.getLogger(MessageTreeSenderTask.class);

    private IMessageTransport messageTransport;

    public MessageTreeSenderTask(MessageTreeStorage messageTreeStorage,
                                 int maxEntities) {
        super(messageTreeStorage, maxEntities);
    }


    public void initialize(IMessageTransport messageTransport) {
        logger.info("正在初始化并启动 MessageTreeSenderTask");
        this.messageTransport = messageTransport;
    }

    @Override
    public boolean doSendDataEntities(List<MessageTree> dataEntities) {

        for (int i = 0; i < dataEntities.size(); i++) {
            this.messageTransport.sendMessage(dataEntities.get(i));
        }

        return true;
    }

    @Override
    public boolean sendDataAvailable() {
        return this.messageTransport.sendDataAvailable();
    }


    public void shutdown() {
        logger.info("正在关闭CollectDataSender");
        super.stop();
    }
}
