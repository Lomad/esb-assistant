package com.winning.esb.model;

import java.util.Date;

/**
 * Created by xuehao on 2017/8/21.
 */
public class SimulationTestStepLogModel {
    private Integer id;
    private Integer tid;
    private Integer sid;
    private Integer result;
    private String desp;
    private String out_msg;
    private String ack_msg;
    private Integer user_id;
    private Integer time_len;
    private Date btime;
    private Date etime;
    private Date ctime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTid() {
        return tid;
    }

    public void setTid(Integer tid) {
        this.tid = tid;
    }

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
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

    public String getOut_msg() {
        return out_msg;
    }

    public void setOut_msg(String out_msg) {
        this.out_msg = out_msg;
    }

    public String getAck_msg() {
        return ack_msg;
    }

    public void setAck_msg(String ack_msg) {
        this.ack_msg = ack_msg;
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
}