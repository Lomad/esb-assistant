package com.winning.esb.model;

/**
 * Created by xuehao on 2017/9/16.
 */
public class ValueListModel {
    private Integer id;
    private Integer ssid;
    private Integer type;
    private String value;
    private String desp;

    public ValueListModel() {
    }
    public ValueListModel(Integer ssid, Integer type, String value) {
        this.ssid = ssid;
        this.type = type;
        this.value = value;
    }
    public ValueListModel(Integer ssid, Integer type, String value, String desp) {
        this.ssid = ssid;
        this.type = type;
        this.value = value;
        this.desp = desp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSsid() {
        return ssid;
    }

    public void setSsid(Integer ssid) {
        this.ssid = ssid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }
}