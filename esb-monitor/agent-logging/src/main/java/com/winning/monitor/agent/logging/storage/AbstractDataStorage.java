package com.winning.monitor.agent.logging.storage;

import com.winning.monitor.agent.logging.message.MessageTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by nicholasyan on 16/9/6.
 */
public abstract class AbstractDataStorage<T extends MessageTree> {

    private static Logger logger = LoggerFactory.getLogger(AbstractDataStorage.class);

    protected final BlockingQueue<T> dataEntityQueue;
    private final int maxSize;
    protected volatile boolean active = true;

    public AbstractDataStorage(int maxQueueSize) {
        this.maxSize = maxQueueSize;
        this.dataEntityQueue = new ArrayBlockingQueue<>(maxQueueSize);
    }

    public boolean offer(T dataEntity) {
        if (!this.active) {
            return false;
        }

        boolean result = true;
        try {
            result = this.dataEntityQueue.offer(dataEntity, 5, TimeUnit.MILLISECONDS);
            return result;
        } catch (Exception e) {
            //xuehao 2017-05-25：记录异常时的队列长度
            logger.error("队列异常，队列长度：{}", maxSize);

            // TODO: 16/9/6 使用监控日志记录错误
            e.printStackTrace();
        }
        return false;
    }

    public List<T> poll(int size) {
        int maxSize = size;
        List<T> dataEntities = new ArrayList<>();

        //xuehao 2017-05-25：判断队列是否为空，如果不为空，则进入循环
//        while (dataEntityQueue.size() > 0 && maxSize > 0) {
        while (!dataEntityQueue.isEmpty() && maxSize > 0) {
            T entity = null;
            try {
                entity = dataEntityQueue.poll(5, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                // TODO: 16/9/7 处理错误
                e.printStackTrace();
                break;
            }
            dataEntities.add(entity);
            maxSize--;
        }

        return dataEntities;
    }

    /**
     * xuehao 2017-05-25：判断队列是否为空
     */
    public boolean isQueueEmpty() {
        return dataEntityQueue.isEmpty();
    }

    public void shutdown() {
        this.active = false;
    }

    public int maxSize() {
        return maxSize;
    }

}
