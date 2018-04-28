package com.winning.monitor.data.api.base;

import java.util.List;

/**
 * @Author Lemod
 * @Version 2017/5/25
 */
public class RunningStatusUnPTVO {

    private String server;
    private String client;
    private String router;
    private List<String> services;
    private Long count;
    private Long failCount;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getRouter() {
        return router;
    }

    public void setRouter(String router) {
        this.router = router;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getFailCount() {
        return failCount;
    }

    public void setFailCount(Long failCount) {
        this.failCount = failCount;
    }
}
