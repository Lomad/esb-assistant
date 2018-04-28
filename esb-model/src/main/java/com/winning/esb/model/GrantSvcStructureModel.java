package com.winning.esb.model;

public class GrantSvcStructureModel implements Cloneable {
    private Integer id;
    private Integer gid;
    private Integer ssid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

    public Integer getSsid() {
        return ssid;
    }

    public void setSsid(Integer ssid) {
        this.ssid = ssid;
    }
}