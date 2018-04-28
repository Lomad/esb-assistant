package com.winning.monitor.data.api.enums;

/**
 * Created by SuperUser on 2017/4/20.
 */
public enum Time_Type {
    CURRENTHOUR("currentHour"),SPECIFIEDHOUR("specifiedHour"),
    TODAY("today"), YESTERDAY("yesterday"),LAST_WEEK("lastweek");

    private String type;

    Time_Type(String type){
        this.type = type;
    }

    public String getType(){
        return this.type;
    }
}