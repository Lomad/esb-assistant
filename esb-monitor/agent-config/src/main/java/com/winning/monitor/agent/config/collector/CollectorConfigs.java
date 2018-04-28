package com.winning.monitor.agent.config.collector;

/**
 * Created by nicholasyan on 16/9/5.
 */
public class CollectorConfigs {

    private CollectorConfig[] collectors = new CollectorConfig[0];

    public CollectorConfig[] getCollectors() {
        return collectors;
    }

    public void setCollectors(CollectorConfig[] collectors) {
        this.collectors = collectors;
    }
}
