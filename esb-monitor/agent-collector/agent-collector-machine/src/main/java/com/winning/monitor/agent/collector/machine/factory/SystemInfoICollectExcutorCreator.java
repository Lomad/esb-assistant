package com.winning.monitor.agent.collector.machine.factory;

import com.winning.monitor.agent.config.collector.CollectorConfig;
import com.winning.monitor.agent.collector.api.executor.DataCollectExecutor;
import com.winning.monitor.agent.collector.api.executor.RunOnceDataCollectExecutor;
import com.winning.monitor.agent.collector.api.factory.AbstractDataCollectExecutorCreator;
import com.winning.monitor.agent.collector.machine.core.SystemInfoCollector;

/**
 * Created by nicholasyan on 16/9/5.
 */
public class SystemInfoICollectExcutorCreator extends AbstractDataCollectExecutorCreator {

    @Override
    public DataCollectExecutor doCreateDataCollector(CollectorConfig collectorConfig) {

        SystemInfoCollector systemInfoCollector = new SystemInfoCollector();

        //创建只执行一次的执行器
        RunOnceDataCollectExecutor executor
                = new RunOnceDataCollectExecutor(collectorConfig.getCollectorName(),
                systemInfoCollector);

        return executor;
    }



}
