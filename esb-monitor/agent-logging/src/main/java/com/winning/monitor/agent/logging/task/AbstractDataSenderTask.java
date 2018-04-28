package com.winning.monitor.agent.logging.task;

import com.winning.monitor.agent.logging.message.MessageTree;
import com.winning.monitor.agent.sender.IDataEntityStorage;

import java.util.List;

/**
 * Created by nicholasyan on 16/9/6.
 */
public abstract class AbstractDataSenderTask<T extends MessageTree>
        implements Runnable {

    private final int MAX_ENTITIES;
    private final IDataEntityStorage collectDataStorage;

    private Thread currentThread;
    private volatile boolean active = true;

    public AbstractDataSenderTask(IDataEntityStorage collectDataStorage, int maxEntities) {
        this.collectDataStorage = collectDataStorage;
        this.MAX_ENTITIES = maxEntities;
    }

    public void start() {
        if (currentThread != null) {
            return;
        }

        this.active = true;
        this.currentThread = new Thread(this);
        this.currentThread.setDaemon(true);
        this.currentThread.start();
    }

    @Override
    public void run() {
        while (true) {
            //如果还没有处理完,等待所有队列消息处理完成
            if (active == false && this.collectDataStorage.remainSize() == 0) {
                return;
            }
            try {
                int maxSize = MAX_ENTITIES;
                if (sendDataAvailable()) {

                    List<T> dataEntities = this.collectDataStorage.get(maxSize);

                    if (!dataEntities.isEmpty()) {
                        // TODO: 16/9/6 加入监控运行时间
                        boolean success = false;

                        try {
                            //组装数据并发送
                            success = doSendDataEntities(dataEntities);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                        }

                        if (!success) {

                        }
                    }
                    //xuehao 2017-05-25：如果队列为空，则休眠1秒
                    else {
                        Thread.sleep(1000);
                    }
                }

                Thread.sleep(5);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public abstract boolean doSendDataEntities(List<T> dataEntities);

    public abstract boolean sendDataAvailable();


    public void stop() {
        this.active = false;
        try {
            this.currentThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.currentThread = null;
    }

}
