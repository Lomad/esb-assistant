package com.winning.monitor.agent.collector.sender;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by nicholasyan on 16/9/7.
 */
public class CollectDataSenderTaskManager {

    private final CollectDataSenderContext dataSenderContext;
    private final List<ICollectDataSenderTask> senderTasks = new ArrayList<>();

    public CollectDataSenderTaskManager(CollectDataSenderContext dataSenderContext,
                                        List<ICollectDataSenderTask> tasks) {
        this.dataSenderContext = dataSenderContext;
        this.senderTasks.addAll(tasks);
    }


    public void initialze() {
        for (ICollectDataSenderTask senderTask : senderTasks) {
            senderTask.initialize(dataSenderContext.getMessageTransport());
        }
    }

    public void start() {
        for (ICollectDataSenderTask senderTask : senderTasks) {
            senderTask.start();
        }
    }

    public void shutdown() {
        for (ICollectDataSenderTask senderTask : senderTasks) {
            senderTask.shutdown();
        }
    }


}
