package com.winning.esb.model;

import java.util.Date;

/**
 * Created by xuehao on 2017/8/21.
 */
public class SimulationTestLogModel {
    private Integer id;
    private Integer fid;
    private Integer result;
    private String desp;
    private Integer user_id;
    private Integer time_len;
    private Date btime;
    private Date etime;
    private Date ctime;
    private Date mtime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFid() {
        return fid;
    }

    public void setFid(Integer fid) {
        this.fid = fid;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
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