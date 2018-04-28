package com.winning.monitor.data.api.largerScreen.entity;

/**
 * Created by xuehao on 2017/3/28.
 */
public class DatabaseInfoVO {
    private String dbName;

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    private String ip;
    /**
     * 数据库类型
     */
    private String dbType;
    /**
     * 抽取的数据量(对于ODR是指指标数量)
     */
    private String dataNumber;
    /**
     * 抽取数据的成功数量
     */
    private String dataNumberSuccess;
    /**
     * 抽取数据的失败数量
     */
    private String dataNumberFailure;
    /**
     * CPU使用率
     */
    private String cpuPercent;
    /**
     * 内存大小
     */
    private String memorySize;
    /**
     * 内存使用率
     */
    private String memoryPercent;
    /**
     * 磁盘大小
     */
    private String diskSize;
    /**
     * 磁盘所占空间
     */
    private String diskUsed;
    /**
     * 数据库大小
     */
    private String dbSize;
    /**
     * 数据库使用率
     */
    private String dbSizePercent;

    public String getDbSizePercent() {
        return dbSizePercent;
    }

    public void setDbSizePercent(String dbSizePercent) {
        this.dbSizePercent = dbSizePercent;
    }

    public String getDiskSizePercent() {
        return diskSizePercent;
    }

    public void setDiskSizePercent(String diskSizePercent) {
        this.diskSizePercent = diskSizePercent;
    }

    /**
     * 硬盘使用率
     */
    private String diskSizePercent;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getDataNumber() {
        return dataNumber;
    }

    public void setDataNumber(String dataNumber) {
        this.dataNumber = dataNumber;
    }

    public String getDataNumberSuccess() {
        return dataNumberSuccess;
    }

    public void setDataNumberSuccess(String dataNumberSuccess) {
        this.dataNumberSuccess = dataNumberSuccess;
    }

    public String getDataNumberFailure() {
        return dataNumberFailure;
    }

    public void setDataNumberFailure(String dataNumberFailure) {
        this.dataNumberFailure = dataNumberFailure;
    }

    public String getCpuPercent() {
        return cpuPercent;
    }

    public void setCpuPercent(String cpuPercent) {
        this.cpuPercent = cpuPercent;
    }

    public String getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(String memorySize) {
        this.memorySize = memorySize;
    }

    public String getMemoryPercent() {
        return memoryPercent;
    }

    public void setMemoryPercent(String memoryPercent) {
        this.memoryPercent = memoryPercent;
    }

    public String getDiskSize() {
        return diskSize;
    }

    public void setDiskSize(String diskSize) {
        this.diskSize = diskSize;
    }

    public String getDiskUsed() {
        return diskUsed;
    }

    public void setDiskUsed(String diskUsed) {
        this.diskUsed = diskUsed;
    }

    public String getDbSize() {
        return dbSize;
    }

    public void setDbSize(String dbSize) {
        this.dbSize = dbSize;
    }
}