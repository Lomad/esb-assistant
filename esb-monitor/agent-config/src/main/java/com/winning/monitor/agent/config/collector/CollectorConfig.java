package com.winning.monitor.agent.config.collector;

import java.util.LinkedHashMap;

/**
 * Created by nicholasyan on 16/9/5.
 */
public class CollectorConfig extends LinkedHashMap<String, Object> {

//    private String collectorName;

//    private Long interval;

    public String getCollectorName() {
        return (String) this.get("collectorName");
    }

    public Long getInterval() {
        if (!this.containsKey("interval")) {
            return null;
        }
        return Long.parseLong(this.get("interval").toString());
    }

}
