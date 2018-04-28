package com.winning.monitor.agent.logging.message;

import com.winning.monitor.message.Message;

/**
 * Created by nicholasyan on 16/9/8.
 */
public interface MessageTree extends Message {

    public static final String MESSAGE_TYPE = "MessageTree";

    public Caller getCaller();

    public void setCaller(Caller caller);

    public MessageTree copy();

    public String getDomain();

    public void setDomain(String domain);

    public String getGroup();

    public void setGroup(String group);

    @Override
    public String getHostName();

    public void setHostName(String hostName);

    @Override
    public String getIpAddress();

    public void setIpAddress(String ipAddress);

    public LogMessage getMessage();

    public void setMessage(LogMessage message);

    public String getMessageId();

    public void setMessageId(String messageId);

    public String getParentMessageId();

    public void setParentMessageId(String parentMessageId);

    public String getRootMessageId();

    public void setRootMessageId(String rootMessageId);

    public String getSessionToken();

    public void setSessionToken(String sessionToken);

    public String getThreadGroupName();

    public void setThreadGroupName(String name);

    public String getThreadId();

    public void setThreadId(String threadId);

    public String getThreadName();

    public void setThreadName(String id);

    public boolean isSample();

    public void setSample(boolean sample);

}
