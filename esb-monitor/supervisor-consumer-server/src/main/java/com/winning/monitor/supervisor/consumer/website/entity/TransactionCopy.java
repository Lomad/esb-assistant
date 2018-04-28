package com.winning.monitor.supervisor.consumer.website.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Lemod
 * @Version 2016/11/26
 */
public class TransactionCopy {

    private String type;

    private String name;

    private List<Data> dataList = new ArrayList<>();

    private List<TransactionCopy> children ;

    private String startTime;

    private String endTime;

    private String status;

    @XmlElement
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @XmlElement
    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @XmlElement
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @XmlElementWrapper(name = "children")
    @XmlElement(name = "child",type = TransactionCopy.class)
    public List<TransactionCopy> getChildren() {
        return children;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setChildren(List<TransactionCopy> children) {
        this.children = children;
    }

    @XmlElementWrapper(name = "dataList")
    @XmlElement(name = "data")
    public List<Data> getDataList() {

        return dataList;
    }

    public void setDataList(List<Data> dataList) {
        this.dataList = dataList;
    }

    @XmlElement
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
