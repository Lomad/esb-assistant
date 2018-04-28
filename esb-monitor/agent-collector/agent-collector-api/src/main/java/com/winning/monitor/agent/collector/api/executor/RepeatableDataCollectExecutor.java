package com.winning.monitor.agent.collector.api.executor;

import com.winning.monitor.agent.collector.api.core.IDataCollector;
import com.winning.monitor.agent.collector.api.entity.CollectData;


/**
 * Created by nicholasyan on 16/9/5.
 */
public class RepeatableDataCollectExecutor extends AbstractDataCollectExecutor {

    private long interval;
    private long nextCollectTime;
    private long currentTimeMillis;

    public RepeatableDataCollectExecutor(String name, IDataCollector collector, long interval) {
        super(name, collector);
        this.interval = interval;
        this.nextCollectTime = System.currentTimeMillis();
    }

    @Override
    public CollectData collect() {
        this.currentTimeMillis = System.currentTimeMillis();
        this.nextCollectTime = this.currentTimeMillis + this.interval;
        CollectData collectData = this.doCollect();
        return collectData;
    }

    /**
     * 获取动态数据收集器的间隔时间
     *
     * @return 下次收集时间
     */
    @Override
    public long getNextCollectTime() {
        return this.nextCollectTime;
    }

    /**
     * 获取间隔时间,单位为毫秒
     *
     * @return 间隔时间
     */
    public long getInterval() {
        return this.interval;
    }
}
