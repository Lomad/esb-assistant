package com.winning.monitor.agent.collector.storage;

import com.winning.monitor.agent.collector.api.entity.CollectData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by nicholasyan on 16/9/6.
 */
public class DefaultCollectDataStorage extends AbstractDataStorage
        implements ICollectDataStorage {

    private static Logger logger = LoggerFactory.getLogger(DefaultCollectDataStorage.class);

    public DefaultCollectDataStorage(int maxQueueSize) {
        super(maxQueueSize);
    }

    @Override
    public void initialize() {
        logger.info("正在初始化并启动 CollectDataStorage");
    }

    @Override
    public boolean put(CollectData data) {
        return this.offer(data);
    }

    @Override
    public List<CollectData> get(int size) {
        return super.poll(size);
    }

    @Override
    public int remainSize() {
        return dataEntityQueue.size();
    }

    @Override
    public void shutdown() {
        logger.info("正在关闭 CollectDataStorage");
        super.shutdown();
    }


}
