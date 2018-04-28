package com.winning.monitor.agent.collector.sender;

import com.winning.monitor.agent.collector.api.entity.CollectData;
import com.winning.monitor.agent.collector.storage.ICollectDataStorage;
import com.winning.monitor.agent.collector.task.AbstractDataSenderTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by nicholasyan on 16/9/6.
 */
public class DefaultCollectDataSenderTask extends AbstractDataSenderTask
        implements ICollectDataSenderTask {

    private static Logger logger = LoggerFactory.getLogger(DefaultCollectDataSenderTask.class);

    private ICollectDataMessageTransport collectDataMessageTransport;

    public DefaultCollectDataSenderTask(ICollectDataStorage collectDataStorage,
                                        int maxEntities) {
        super(collectDataStorage, maxEntities);
    }

    @Override
    public void initialize(ICollectDataMessageTransport messageTransport) {
        logger.info("正在初始化并启动CollectDataSender");
        this.collectDataMessageTransport = messageTransport;

    }

    @Override
    public boolean doSendDataEntities(List<CollectData> dataEntities) {

//        MessageHead head = new MessageHead();
//        head.setTimestamp(new Date());
//        head.setMessageType(CollectorMessage.TYPE_NAME);
//        head.setHostName(NetworkInterfaceManager.INSTANCE.getLocalHostName());
//        head.setIpAddress(NetworkInterfaceManager.INSTANCE.getLocalHostAddress());
//        head.setMessageId(UUID.randomUUID().toString());
//
//        //组装数据
//        CollectorMessage collectorMessage = new CollectorMessage();
//        collectorMessage.addCollectDatas(dataEntities);
//
//        MessagePackage messagePackage = new MessagePackage();
//        messagePackage.setHead(head);
//        messagePackage.setMessage(collectorMessage);

        this.collectDataMessageTransport.sendMessage(dataEntities);

        return true;
    }

    @Override
    public void shutdown() {
        logger.info("正在关闭CollectDataSender");
        super.stop();
    }
}
