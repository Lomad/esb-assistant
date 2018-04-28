package com.winning.esb.model;

import java.util.Date;

public class InspectionSysModel {
    Integer id;
    Integer aid;
    Integer check_type;
    Date check_time;
    String check_desp;
    Integer result_type;
    Integer result_uid;
    Date result_time;
    String result_desp;
    Date ctime;
    Date mtime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAid() {
        return aid;
    }

    public void setAid(Integer aid) {
        this.aid = aid;
    }

    public Integer getCheck_type() {
        return check_type;
    }

    public void setCheck_type(Integer check_type) {
        this.check_type = check_type;
    }

    public Date getCheck_time() {
        return check_time;
    }

    public void setCheck_time(Date check_time) {
        this.check_time = check_time;
    }

    public String getCheck_desp() {
        return check_desp;
    }

    public void setCheck_desp(String check_desp) {
        this.check_desp = check_desp;
    }

    public Integer getResult_type() {
        return result_type;
    }

    public void setResult_type(Integer result_type) {
        this.result_type = result_type;
    }

    public Integer getResult_uid() {
        return result_uid;
    }

    public void setResult_uid(Integer result_uid) {
        this.result_uid = result_uid;
    }

    public String getResult_desp() {
        return result_desp;
    }

    public void setResult_desp(String result_desp) {
        this.result_desp = result_desp;
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

    public Date getResult_time() {
        return result_time;
    }

    public void setResult_time(Date result_time) {
        this.result_time = result_time;
    }
}
