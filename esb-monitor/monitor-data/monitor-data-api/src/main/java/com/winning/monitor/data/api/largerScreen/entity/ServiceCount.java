package com.winning.monitor.data.api.largerScreen.entity;

/**
 * @Author Lemod
 * @Version 2018/2/2
 */
public class ServiceCount {

    private String serverName;

    private String serviceName;

    private Long count;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
