package com.winning.monitor.agent.collector;

import com.winning.monitor.agent.collector.api.CollectorExecutorFactory;
import com.winning.monitor.agent.collector.api.executor.DataCollectExecutor;
import com.winning.monitor.agent.collector.storage.ICollectDataStorage;
import com.winning.monitor.agent.collector.task.CollectExecutorTask;
import com.winning.monitor.agent.config.collector.CollectorConfig;
import com.winning.monitor.agent.config.collector.ICollectorConfigFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nicholasyan on 16/9/5.
 */
public class CollectExecutorContext {

    private final ICollectorConfigFactory collectorConfigFactory;
    private final ICollectDataStorage collectDataStorage;
    private final CollectorExecutorFactory collectorExecutorFactory;

    private final Map<String, DataCollectExecutor> dataCollectExecutorMap =
            new HashMap<>();

    private List<CollectExecutorTask> executorTasks = new ArrayList<>();
    private boolean initialed = false;

    public CollectExecutorContext(ICollectorConfigFactory collectorConfigFactory,
                                  CollectorExecutorFactory collectorExecutorFactory,
                                  ICollectDataStorage collectDataStorage) {

        this.collectorConfigFactory = collectorConfigFactory;
        this.collectorExecutorFactory = collectorExecutorFactory;
        this.collectDataStorage = collectDataStorage;
    }


    /**
     * 初始化收集器上下文
     */
    public void initialize() {
        if (!this.initialed) {
            //初始化收集器
            if (this.collectorConfigFactory.getCollectorConfigs() != null) {
                for (CollectorConfig collectorConfig : this.collectorConfigFactory.getCollectorConfigs()) {
                    this.initCollectorConfig(collectorConfig);
                }
            }
        }
        this.initialed = true;
    }

    private void initCollectorConfig(CollectorConfig collectorConfig) {
        DataCollectExecutor dataCollectExecutor =
                this.collectorExecutorFactory.createDataCollectExecutor(collectorConfig);

        this.dataCollectExecutorMap.put(collectorConfig.getCollectorName(), dataCollectExecutor);

        CollectExecutorTask collectExecutorTask = new CollectExecutorTask(dataCollectExecutor,
                this.collectDataStorage);

        this.executorTasks.add(collectExecutorTask);
    }


    public List<CollectExecutorTask> getCollectExecutorTasks() {
        return this.executorTasks;
    }


}
