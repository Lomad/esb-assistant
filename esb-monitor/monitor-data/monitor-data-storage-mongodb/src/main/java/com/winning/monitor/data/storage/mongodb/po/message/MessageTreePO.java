package com.winning.monitor.data.storage.mongodb.po.message;

import com.winning.monitor.agent.logging.message.MessageTree;

/**
 * Created by nicholasyan on 16/9/30.
 */
public class MessageTreePO {

    private String time;
    private String ipAddress;
    private String domain;
    private String group;
    private String messageId;

    private MessageTree messageTree;

    public MessageTreePO() {

    }

    public MessageTreePO(MessageTree messageTree) {
        this.messageTree = messageTree;
        this.domain = messageTree.getDomain();
        this.ipAddress = messageTree.getIpAddress();
        this.messageId = messageTree.getMessageId();
        this.group = messageTree.getGroup();
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public MessageTree getMessageTree() {
        return messageTree;
    }

    public void setMessageTree(MessageTree messageTree) {
        this.messageTree = messageTree;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
