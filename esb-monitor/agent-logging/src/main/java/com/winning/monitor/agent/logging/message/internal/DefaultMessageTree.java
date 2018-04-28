package com.winning.monitor.agent.logging.message.internal;


import com.winning.monitor.agent.logging.message.Caller;
import com.winning.monitor.agent.logging.message.LogMessage;
import com.winning.monitor.agent.logging.message.MessageTree;

public class DefaultMessageTree implements MessageTree {

    private Caller caller;
    private String domain;
    private String group;
    private String hostName;
    private String ipAddress;
    private LogMessage message;
    private String messageId;
    private String parentMessageId;
    private String rootMessageId;
    private String sessionToken;
    private String threadGroupName;
    private String threadId;
    private String threadName;
    private boolean sample = true;

    @Override
    public MessageTree copy() {
        MessageTree tree = new DefaultMessageTree();

        tree.setDomain(domain);
        tree.setGroup(group);
        tree.setHostName(hostName);
        tree.setIpAddress(ipAddress);
        tree.setMessageId(messageId);
        tree.setParentMessageId(parentMessageId);
        tree.setRootMessageId(rootMessageId);
        tree.setSessionToken(sessionToken);
        tree.setThreadGroupName(threadGroupName);
        tree.setThreadId(threadId);
        tree.setThreadName(threadName);
        tree.setMessage(message);
        tree.setSample(sample);

        if (caller != null) {
            Caller caller = new Caller();
            caller.setIp(this.caller.getIp());
            caller.setName(this.caller.getName());
            caller.setType(this.caller.getType());
            tree.setCaller(caller);
        }

        return tree;
    }

    @Override
    public Caller getCaller() {
        return caller;
    }

    @Override
    public void setCaller(Caller caller) {
        this.caller = caller;
    }


    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String getHostName() {
        return hostName;
    }

    @Override
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    @Override
    public String getIpAddress() {
        return ipAddress;
    }

    @Override
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String getMessageType() {
        return MESSAGE_TYPE;
    }

    @Override
    public LogMessage getMessage() {
        return message;
    }

    @Override
    public void setMessage(LogMessage message) {
        this.message = message;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public void setMessageId(String messageId) {
        if (messageId != null && messageId.length() > 0) {
            this.messageId = messageId;
        }
    }

    @Override
    public String getParentMessageId() {
        return parentMessageId;
    }

    @Override
    public void setParentMessageId(String parentMessageId) {
        if (parentMessageId != null && parentMessageId.length() > 0) {
            this.parentMessageId = parentMessageId;
        }
    }

    @Override
    public String getRootMessageId() {
        return rootMessageId;
    }

    @Override
    public void setRootMessageId(String rootMessageId) {
        if (rootMessageId != null && rootMessageId.length() > 0) {
            this.rootMessageId = rootMessageId;
        }
    }

    @Override
    public String getSessionToken() {
        return sessionToken;
    }

    @Override
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    @Override
    public String getThreadGroupName() {
        return threadGroupName;
    }

    @Override
    public void setThreadGroupName(String threadGroupName) {
        this.threadGroupName = threadGroupName;
    }

    @Override
    public String getThreadId() {
        return threadId;
    }

    @Override
    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    @Override
    public String getThreadName() {
        return threadName;
    }

    @Override
    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public boolean isSample() {
        return sample;
    }

    @Override
    public void setSample(boolean sample) {
        this.sample = sample;
    }


}
