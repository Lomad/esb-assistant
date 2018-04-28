package com.winning.monitor.data.api.base;

import java.util.List;

/**
 * @Author Lemod
 * @Version 2018/3/16
 */
public class DayCountWithServers {

    private String targetStartTime;

    private long dailyTotalCount;

    private long dailyFailCount;

    private List<String> serverList;

    private List<ServerCountWithType> appCountList;

    public String getTargetStartTime() {
        return targetStartTime;
    }

    public void setTargetStartTime(String targetStartTime) {
        this.targetStartTime = targetStartTime;
    }

    public long getDailyTotalCount() {
        return dailyTotalCount;
    }

    public void setDailyTotalCount(long dailyTotalCount) {
        this.dailyTotalCount = dailyTotalCount;
    }

    public long getDailyFailCount() {
        return dailyFailCount;
    }

    public void setDailyFailCount(long dailyFailCount) {
        this.dailyFailCount = dailyFailCount;
    }

    public List<String> getServerList() {
        return serverList;
    }

    public void setServerList(List<String> serverList) {
        this.serverList = serverList;
    }

    public List<ServerCountWithType> getAppCountList() {
        return appCountList;
    }

    public void setAppCountList(List<ServerCountWithType> appCountList) {
        this.appCountList = appCountList;
    }
}
