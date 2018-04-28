package com.winning.monitor.agent.collector.machine.factory;

import com.winning.monitor.agent.config.collector.CollectorConfig;
import com.winning.monitor.agent.collector.api.executor.DataCollectExecutor;
import com.winning.monitor.agent.collector.api.executor.RepeatableDataCollectExecutor;
import com.winning.monitor.agent.collector.api.factory.AbstractDataCollectExecutorCreator;
import com.winning.monitor.agent.collector.machine.core.SystemDynamicCollector;

/**
 * Created by nicholasyan on 16/9/5.
 */
public class SystemDynamicICollectExcutorCreator extends AbstractDataCollectExecutorCreator {

    @Override
    public DataCollectExecutor doCreateDataCollector(CollectorConfig collectorConfig) {

        SystemDynamicCollector systemDynamicCollector = new SystemDynamicCollector();

        //默认10秒收集一次
        long interval = 10000;
        if (collectorConfig.getInterval() != null) {
            interval = collectorConfig.getInterval().longValue();
        }

        //创建可重复的执行器
        RepeatableDataCollectExecutor repeatableDataCollectExecutor
                = new RepeatableDataCollectExecutor(collectorConfig.getCollectorName(),
                systemDynamicCollector, interval);

        return repeatableDataCollectExecutor;
    }

}
