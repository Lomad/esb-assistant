package com.winning.monitor.supervisor.core.task;


import java.util.Date;

public class Task {

    private String id;

    private String producer;

    private String consumer;

    private int failureCount;

    private String reportName;

    private String reportDomain;

    private String reportGroup;

    private java.util.Date reportPeriod;

    private int status;

    private int taskType;

    private java.util.Date creationDate;

    private java.util.Date startDate;

    private java.util.Date endDate;

    private int keyId;

    private int count;

    private int startLimit;

    private int endLimit;

    private String bgsj;
    private String cjsj;
    private String group;
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getConsumer() {
        return consumer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportDomain() {
        return reportDomain;
    }

    public void setReportDomain(String reportDomain) {
        this.reportDomain = reportDomain;
    }

    public Date getReportPeriod() {
        return reportPeriod;
    }

    public void setReportPeriod(Date reportPeriod) {
        this.reportPeriod = reportPeriod;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getKeyId() {
        return keyId;
    }

    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getStartLimit() {
        return startLimit;
    }

    public void setStartLimit(int startLimit) {
        this.startLimit = startLimit;
    }

    public int getEndLimit() {
        return endLimit;
    }

    public void setEndLimit(int endLimit) {
        this.endLimit = endLimit;
    }

    public String getBgsj() {
        return bgsj;
    }

    public void setBgsj(String bgsj) {
        this.bgsj = bgsj;
    }

    public String getCjsj() {
        return cjsj;
    }

    public void setCjsj(String cjsj) {
        this.cjsj = cjsj;
    }

    public String getReportGroup() {
        return reportGroup;
    }

    public void setReportGroup(String reportGroup) {
        this.reportGroup = reportGroup;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(1024);

        sb.append("Task[");
        sb.append("consumer: ").append(consumer);
        sb.append(", count: ").append(count);
        sb.append(", creation-date: ").append(creationDate);
        sb.append(", end-date: ").append(endDate);
        sb.append(", end-limit: ").append(endLimit);
        sb.append(", failure-count: ").append(failureCount);
        sb.append(", id: ").append(id);
        sb.append(", key-id: ").append(keyId);
        sb.append(", producer: ").append(producer);
        sb.append(", report-domain: ").append(reportDomain);
        sb.append(", report-name: ").append(reportName);
        sb.append(", report-period: ").append(reportPeriod);
        sb.append(", start-date: ").append(startDate);
        sb.append(", start-limit: ").append(startLimit);
        sb.append(", status: ").append(status);
        sb.append(", task-type: ").append(taskType);
        sb.append("]");
        return sb.toString();
    }

}
