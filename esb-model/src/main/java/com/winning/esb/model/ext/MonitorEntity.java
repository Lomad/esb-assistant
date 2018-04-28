package com.winning.esb.model.ext;

public class MonitorEntity {
    private String type;
    private String mbean;
    private String attribute;
    private String group;
    private String path;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMbean() {
        return mbean;
    }

    public void setMbean(String mbean) {
        this.mbean = mbean;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
