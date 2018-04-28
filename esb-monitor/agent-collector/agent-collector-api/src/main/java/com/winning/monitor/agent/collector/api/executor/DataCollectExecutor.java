package com.winning.monitor.agent.collector.api.executor;


import com.winning.monitor.agent.collector.api.entity.CollectData;

/**
 * 动态数据收集器,根据CollectInterval设置取样间隔时间
 */
public interface DataCollectExecutor {

    /**
     * 获取数据收集器名称
     *
     * @return 数据收集器名称
     */
    String getCollectName();

    /**
     * 收集动态数据,处理数据的收集核心
     *
     * @return 收集到的采样数据
     */
    CollectData collect();

    /**
     * 获取动态数据收集器的间隔时间
     *
     * @return 下次收集时间
     */
    long getNextCollectTime();

}