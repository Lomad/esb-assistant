package com.winning.monitor.supervisor.consumer.website.entity;

import javax.xml.bind.annotation.XmlElement;

/**
 * @Author Lemod
 * @Version 2016/11/26
 */
public class Data {

    @XmlElement(name = "value")
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @XmlElement(name = "key")
    public String getKey() {

        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    private String key;

    private Object value;

}
