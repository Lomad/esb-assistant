package com.winning.monitor.agent.sender;

import com.winning.monitor.message.DataEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by nicholasyan on 16/9/6.
 */
public abstract class AbstractDataStorage<T extends DataEntity> {

    protected final BlockingQueue<T> dataEntityQueue;
    protected volatile boolean active = true;

    public AbstractDataStorage(int maxQueueSize) {
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
            // TODO: 16/9/6 使用监控日志记录错误
            e.printStackTrace();
        }
        return false;
    }

    public List<T> poll(int size) {
        int maxSize = size;
        List<T> dataEntities = new ArrayList<>();

        while (dataEntityQueue.size() > 0 && maxSize > 0) {
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

    public void shutdown() {
        this.active = false;
    }

}
