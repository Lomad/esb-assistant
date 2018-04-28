package com.winning.esb.model;

import java.util.Date;

/**
 * Created by xuehao on 2017/8/21.
 */
public class ReceiveTestModel {
    private Integer id;
    private String svcCode;
    private String out_msg;
    private String ack_msg;
    private Date ctime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSvcCode() {
        return svcCode;
    }

    public void setSvcCode(String svcCode) {
        this.svcCode = svcCode;
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

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }
}