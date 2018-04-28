package com.winning.monitor.webservice.request_entity;

import org.xblink.annotation.XBlinkAlias;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Lemod
 * @Version 2016/11/26
 */
@XBlinkAlias(value="TransactionCopy")
public class TransactionCopy {

    private String type;

    private String name;

    private List<Data> dataList = new ArrayList<>();

    private List<TransactionCopy> children ;

    private String startTime;

    private String endTime;

    private String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<TransactionCopy> getChildren() {

        return children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setChildren(List<TransactionCopy> children) {
        this.children = children;
    }

    public List<Data> getDataList() {

        return dataList;
    }

    public void setDataList(List<Data> dataList) {
        this.dataList = dataList;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
