package com.winning.esb.controller.monitor.model;

/**
 * @Author Lemod
 * @Version 2017/4/17
 */
public class CountOfSystem {

    private String name;

    private long count;

    private long failCount;

    public CountOfSystem(){

    }

    public CountOfSystem(String name, long count, long failCount){
        this.name = name;
        this.count = count;
        this.failCount = failCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getFailCount() {
        return failCount;
    }

    public void setFailCount(long failCount) {
        this.failCount = failCount;
    }
}
