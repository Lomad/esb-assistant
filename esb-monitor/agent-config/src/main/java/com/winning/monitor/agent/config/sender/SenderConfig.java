package com.winning.monitor.agent.config.sender;

import java.util.Map;

/**
 * Created by nicholasyan on 16/9/7.
 */
public class SenderConfig {

    private String sender;

    private String servers;
    private Map<String, Object> properties;

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getServers() {
        return servers;
    }

    public void setServers(String servers) {
        this.servers = servers;
    }
}
