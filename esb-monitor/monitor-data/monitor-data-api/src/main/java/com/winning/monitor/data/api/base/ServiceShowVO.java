package com.winning.monitor.data.api.base;

import com.winning.monitor.agent.logging.message.MessageTree;

/**
 * @Author Lemod
 * @Version 2017/7/7
 */
public class ServiceShowVO {

    private String domain;

    private int index;

    private String startTime;

    private String endTime;

    private MessageTree messageTree;

    public ServiceShowVO(){

    }

    public ServiceShowVO(String domain,String startTime,String endTime,int index){
        this.domain = domain;
        this.startTime = startTime;
        this.endTime = endTime;
        this.index = index;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public MessageTree getMessageTree() {
        return messageTree;
    }

    public void setMessageTree(MessageTree messageTree) {
        this.messageTree = messageTree;
    }
}
