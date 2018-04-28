package com.winning.esb.model;

import java.util.Date;

/**
 * Created by xuehao on 2017/8/23.
 */
public class InspectionModel {
    private Integer id;
    private Integer result;
    private Integer check_uid;
    private String check_desp;
    private Integer time_len;
    private Date btime;
    private Date etime;
    private Integer result_uid;
    private Date result_time;
    private String result_desp;
    private Date ctime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public Integer getCheck_uid() {
        return check_uid;
    }

    public void setCheck_uid(Integer check_uid) {
        this.check_uid = check_uid;
    }

    public String getCheck_desp() {
        return check_desp;
    }

    public void setCheck_desp(String check_desp) {
        this.check_desp = check_desp;
    }

    public Integer getTime_len() {
        return time_len;
    }

    public void setTime_len(Integer time_len) {
        this.time_len = time_len;
    }

    public Date getBtime() {
        return btime;
    }

    public void setBtime(Date btime) {
        this.btime = btime;
    }

    public Date getEtime() {
        return etime;
    }

    public void setEtime(Date etime) {
        this.etime = etime;
    }

    public Integer getResult_uid() {
        return result_uid;
    }

    public void setResult_uid(Integer result_uid) {
        this.result_uid = result_uid;
    }

    public Date getResult_time() {
        return result_time;
    }

    public void setResult_time(Date result_time) {
        this.result_time = result_time;
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
}