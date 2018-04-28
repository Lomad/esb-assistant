package com.winning.esb.model;

public class Commissioning {
    private String localIP;
    private Integer localPort;
    private String remoteIP;
    private  Integer remotePort;
    private Integer waitTimeOut;

    //系统（LIS，RIS）
    private String domain;
    //服务
    private String service;

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }




    public String getLocalIP() {
        return localIP;
    }

    public void setLocalIP(String localIP) {
        this.localIP = localIP;
    }

    public Integer getLocalPort() {
        return localPort;
    }

    public void setLocalPort(Integer localPort) {
        this.localPort = localPort;
    }

    public String getRemoteIP() {
        return remoteIP;
    }

    public void setRemoteIP(String remoteIP) {
        this.remoteIP = remoteIP;
    }

    public Integer getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(Integer remotePort) {
        this.remotePort = remotePort;
    }

    public Integer getWaitTimeOut() {
        return waitTimeOut;
    }

    public void setWaitTimeOut(Integer waitTimeOut) {
        this.waitTimeOut = waitTimeOut;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }


}
