package com.winning.monitor.supervisor.consumer.website.entity.request_entity;

import com.winning.monitor.agent.logging.message.Caller;
import org.xblink.annotation.XBlinkAlias;

/**
 * @Author Lemod
 * @Version 2016/11/26
 */
@XBlinkAlias(value="remoteCaller")
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

    public Caller toCaller(){
        Caller caller = new Caller();
        caller.setIp(this.ip);
        caller.setName(this.name);
        caller.setType(this.type);

        return caller;
    }
}
