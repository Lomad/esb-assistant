package com.winning.esb.model.enums;


public enum QueryParameterKeys {
    /**
     * 开始时间
     */
    STARTTIME("startTime"),
    /**
     * 结束时间
     */
    ENDTIME("endTime"),
    /**
     * 开始序号
     */
    STARTINDEX("startIndex"),
    /**
     * 页的大小
     */
    PAGESIZE("pageSize");

    private String key;

    QueryParameterKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}

