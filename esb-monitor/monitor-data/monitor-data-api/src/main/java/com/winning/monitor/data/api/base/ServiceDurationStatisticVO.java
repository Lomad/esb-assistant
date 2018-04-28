package com.winning.monitor.data.api.base;

/**
 * @Author Lemod
 * @Version 2018/2/6
 */
public class ServiceDurationStatisticVO {

    private String serverId;

    private String serverName;

    private String consumerId;

    private String consumerName;

    private String serviceId;

    private String serviceName;

    //耗时用String类型映射
    private String duration;

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
