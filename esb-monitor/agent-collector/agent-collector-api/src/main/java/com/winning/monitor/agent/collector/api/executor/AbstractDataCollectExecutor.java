package com.winning.monitor.agent.collector.api.executor;

import com.winning.monitor.agent.collector.api.core.IDataCollector;
import com.winning.monitor.agent.collector.api.entity.CollectData;

/**
 *
 */
public abstract class AbstractDataCollectExecutor implements DataCollectExecutor {

    protected final IDataCollector collector;
    protected final String name;

    public AbstractDataCollectExecutor(String name,
                                       IDataCollector collector) {

        if (name == null || name.trim().length() == 0) {
            String className = collect().getClass().toString();
            String[] names = className.split(".");
            this.name = names[names.length - 1];
        } else {
            this.name = name;
        }

        this.collector = collector;
    }

    /**
     * 设置数据收集器名称
     *
     * @return 数据收集器名称
     */
    @Override
    public String getCollectName() {
        return this.name;
    }


    /**
     * 收集动态数据,处理数据的收集核心
     *
     * @return 收集到的采样数据
     */
    @Override
    public CollectData collect() {
        return this.doCollect();
    }


    /**
     * 处理实际的数据采集
     *
     * @return 收集到的数据
     */
    protected CollectData doCollect() {
        return collector.collect();
    }


}
