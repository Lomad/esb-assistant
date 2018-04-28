package com.winning.monitor.agent.collector.sender;


import com.winning.monitor.agent.collector.api.entity.CollectData;

import java.util.List;

/**
 * Created by nicholasyan on 16/9/6.
 */
public interface ICollectDataMessageTransport {

    void initialize();

    void sendMessage(List<CollectData> collectData);

    void shutdown();
}