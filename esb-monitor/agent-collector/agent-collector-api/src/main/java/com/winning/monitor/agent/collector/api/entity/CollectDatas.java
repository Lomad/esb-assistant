package com.winning.monitor.agent.collector.api.entity;

import com.winning.monitor.message.Message;

import java.util.List;

/**
 * Created by nicholasyan on 16/9/8.
 */
public class CollectDatas implements Message {

    public static final String MESSAGE_TYPE = "CollectDatas";

    private String hostName;
    private String ipAddress;
    private List<CollectData> datas;

    public List<CollectData> getDatas() {
        return datas;
    }

    public void setDatas(List<CollectData> datas) {
        this.datas = datas;
    }


    @Override
    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {

        this.hostName = hostName;
    }

    @Override
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String getMessageType() {
        return MESSAGE_TYPE;
    }
}
