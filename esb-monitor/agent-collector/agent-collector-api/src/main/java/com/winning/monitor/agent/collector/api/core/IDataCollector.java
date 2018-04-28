package com.winning.monitor.agent.collector.api.core;


import com.winning.monitor.agent.collector.api.entity.CollectData;

/**
 * Created by nicholasyan on 16/9/5.
 */
public interface  IDataCollector {

    /**
     * 收集数据,处理数据收集核心
     *
     * @return 收集到的采样数据
     */
    CollectData collect();


}
