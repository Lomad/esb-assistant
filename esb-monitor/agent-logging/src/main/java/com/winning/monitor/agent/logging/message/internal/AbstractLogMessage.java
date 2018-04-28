package com.winning.monitor.agent.logging.message.internal;

import com.winning.monitor.agent.logging.message.LogMessage;
import com.winning.monitor.agent.logging.utils.MilliSecondTimer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nicholasyan on 16/9/8.
 */
public abstract class AbstractLogMessage implements LogMessage {
    protected LinkedHashMap<String, Object> data = new LinkedHashMap<>();
    private String type;
    private String name;
    private String status = "unset";
    private long timestampInMillis;
    private boolean completed;

    /**
     * version:17.10.11
     * 新增数组用于保存所有父子MessageID
     */
    private List<Object> headerIdList;

    public AbstractLogMessage(String type, String name) {
        this.type = String.valueOf(type);
        this.name = String.valueOf(name);
        this.timestampInMillis = MilliSecondTimer.currentTimeMillis();
    }

//    @Override
//    public void addData(String keyValuePairs) {
//        if (this.data == null) {
//            this.data = keyValuePairs;
//        } else if (this.data instanceof StringBuilder) {
//            ((StringBuilder) this.data).append('&').append(keyValuePairs);
//        } else {
//            StringBuilder sb = new StringBuilder(this.data.length() + keyValuePairs.length() + 16);
//
//            sb.append(this.data).append('&');
//            sb.append(keyValuePairs);
//            this.data = sb;
//        }
//    }

    @Override
    public void addData(String key, Object value) {
        this.data.put(key, value);
        if ("MessageID".equals(key) || "ParentMessageID".equals(key)){
            if (this.headerIdList == null) {
                this.headerIdList = new ArrayList<>();
            }

            this.headerIdList.add(value);
        }
//        if (this.data instanceof StringBuilder) {
//            ((StringBuilder) this.data).append('&').append(key).append('=').append(value);
//        } else {
//            String str = String.valueOf(value);
//            int old = this.data == null ? 0 : this.data.length();
//            StringBuilder sb = new StringBuilder(old + key.length() + str.length() + 16);
//
//            if (this.data != null) {
//                sb.append(this.data).append('&');
//            }
//
//            sb.append(key).append('=').append(str);
//            this.data = sb;
//        }
    }

    @Override
    public Map<String, Object> getData() {
        return this.data;
//        if (this.data == null) {
//            return "";
//        } else {
//            return this.data.t
//
//            //return this.data;
//        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public void setStatus(Throwable e) {
        this.status = e.getClass().getName();
    }

    @Override
    public long getTimestamp() {
        return this.timestampInMillis;
    }

    public void setTimestamp(long timestamp) {
        this.timestampInMillis = timestamp;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean isCompleted() {
        return this.completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public boolean isSuccess() {
        return LogMessage.SUCCESS.equals(this.status);
    }

    public List<Object> getHeaderIdList() {
        return headerIdList;
    }

    public void setHeaderIdList(List<Object> headerIdList) {
        this.headerIdList = headerIdList;
    }

}