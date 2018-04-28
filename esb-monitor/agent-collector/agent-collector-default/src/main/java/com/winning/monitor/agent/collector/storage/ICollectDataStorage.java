package com.winning.monitor.agent.collector.storage;


import com.winning.monitor.agent.collector.api.entity.CollectData;

import java.util.List;

/**
 * Created by nicholasyan on 16/9/6.
 */
public interface ICollectDataStorage {

    void initialize();

    boolean put(CollectData data);

    List<CollectData> get(int size);

    int remainSize();

    void shutdown();


}
