package com.winning.esb.model;

import java.util.Date;

/**
 * Created by xuehao on 2017/8/23.
 */
public class InspectionDetailModel {
    private Integer id;
    private Integer ins_id;
    private Integer index_id;
    private Integer result;
    private String desp;
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

    public Integer getIns_id() {
        return ins_id;
    }

    public void setIns_id(Integer ins_id) {
        this.ins_id = ins_id;
    }

    public Integer getIndex_id() {
        return index_id;
    }

    public void setIndex_id(Integer index_id) {
        this.index_id = index_id;
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