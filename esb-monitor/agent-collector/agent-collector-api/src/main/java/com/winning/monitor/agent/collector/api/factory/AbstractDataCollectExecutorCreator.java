package com.winning.monitor.agent.collector.api.factory;

import com.winning.monitor.agent.config.collector.CollectorConfig;
import com.winning.monitor.agent.collector.api.executor.DataCollectExecutor;

/**
 * Created by nicholasyan on 16/9/5.
 */
public abstract class AbstractDataCollectExecutorCreator
        implements IDataCollectExecutorCreator {

    @Override
    public DataCollectExecutor createDataCollector(CollectorConfig collectorConfig) {
        return this.doCreateDataCollector(collectorConfig);
    }

    protected abstract DataCollectExecutor doCreateDataCollector(CollectorConfig collectorConfig);

}