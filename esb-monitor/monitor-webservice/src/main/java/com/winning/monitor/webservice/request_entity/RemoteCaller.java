package com.winning.monitor.webservice.request_entity;

import org.xblink.annotation.XBlinkAlias;

/**
 * @Author Lemod
 * @Version 2016/11/26
 */
@XBlinkAlias(value="RemoteCaller")
public class RemoteCaller {

    private String name;

    private String ip;

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIp() {

        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
