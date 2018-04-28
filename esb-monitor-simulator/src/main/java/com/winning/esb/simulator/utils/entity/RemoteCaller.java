package com.winning.esb.simulator.utils.entity;

import com.winning.monitor.agent.logging.message.Caller;

import javax.xml.bind.annotation.XmlElement;

/**
 * @Author Lemod
 * @Version 2016/11/26
 */
public class RemoteCaller {

    private String name;

    private String ip;

    private String type;

    @XmlElement
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlElement
    public String getIp() {

        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Caller toCaller(){
        Caller caller = new Caller();
        caller.setIp(this.ip);
        caller.setName(this.name);
        caller.setType(this.type);

        return caller;
    }
}
