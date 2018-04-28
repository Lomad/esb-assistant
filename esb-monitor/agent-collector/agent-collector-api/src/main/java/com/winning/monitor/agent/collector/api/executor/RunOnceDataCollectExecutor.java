package com.winning.monitor.agent.collector.api.executor;

import com.winning.monitor.agent.collector.api.core.IDataCollector;
import com.winning.monitor.agent.collector.api.entity.CollectData;


/**
 * Created by nicholasyan on 16/9/5.
 */
public class RunOnceDataCollectExecutor extends AbstractDataCollectExecutor {

    private boolean complete = false;

    public RunOnceDataCollectExecutor(String name, IDataCollector collector) {
        super(name, collector);
    }

    @Override
    public CollectData collect() {
        CollectData collectData = this.doCollect();
        this.complete = true;
        return collectData;
    }

    /**
     * 获取动态数据收集器的间隔时间
     *
     * @return 下次收集时间
     */
    @Override
    public long getNextCollectTime() {
        if (this.complete) {
            return Long.MAX_VALUE;
        } else {
            return 0;
        }
    }
}
