package com.winning.monitor.data.api.enums;

/**
 * Created by SuperUser on 2017/4/20.
 */
public enum DateType {
    LAST1H(11, "最近1小时"),LAST3H(12, "最近3小时"),LAST6H(13, "最近6小时"),LAST24H(14, "最近24小时"),
    LAST7D(15, "最近7天"),LAST30D(16, "最近30天"),
    TODAY(21, "今日"), YESTODAY(22, "昨日"),CURRENTWEEK(23, "本周"),CURRENTMONTH(24, "本月"),
    HOUR(31, "时"),DAY(32, "天"),  WEEK(33, "周"), MONTH(34, "月"),HISTORY(35, "历史");

    private int key;
    private String name;

    DateType(int key, String name) {
        this.key = key;
        this.name = name;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}