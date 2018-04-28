package com.winning.esb.model;

import java.util.Date;

/**
 * Created by xuehao on 2017/07/27.
 */
public class ConfigsModel {
    private String code;
    private String name;
    private String value;
    private String desp;
    private Integer type;
    private String regex;
    private Integer visible;
    private String candidate_values;

    //创建时间（yyyy-MM-dd HH:mm:ss）
    private Date ctime;
    //创建时间（yyyy-MM-dd HH:mm:ss）
    private Date mtime;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public Integer getVisible() {
        return visible;
    }

    public void setVisible(Integer visible) {
        this.visible = visible;
    }

    public String getCandidate_values() {
        return candidate_values;
    }

    public void setCandidate_values(String candidate_values) {
        this.candidate_values = candidate_values;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public Date getMtime() {
        return mtime;
    }

    public void setMtime(Date mtime) {
        this.mtime = mtime;
    }
}