package com.winning.monitor.agent.collector.api.factory;

import com.winning.monitor.agent.config.collector.CollectorConfig;
import com.winning.monitor.agent.collector.api.executor.DataCollectExecutor;

/**
 * Created by nicholasyan on 16/9/5.
 */
public interface IDataCollectExecutorCreator {

    DataCollectExecutor createDataCollector(CollectorConfig collectorConfig);

}
