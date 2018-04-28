package com.winning.monitor.webservice.request_entity;

import org.xblink.annotation.XBlinkAlias;

/**
 * @Author Lemod
 * @Version 2016/11/26
 */
@XBlinkAlias(value="Data")
public class Data {
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {

        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    private String key;

    private String value;

}
