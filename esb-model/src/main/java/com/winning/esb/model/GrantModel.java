package com.winning.esb.model;

import java.util.Date;

public class GrantModel implements Cloneable {
    private Integer id;
    private Integer aid;
    private Integer sid;
    private String lic_key;
    private String secret_key;
    private Date ctime;
    private Date mtime;
    private Date apply_time;
    private Date approve_time;
    private Integer approve_state;

    @Override
    public GrantModel clone() {
        try {
            return (GrantModel) super.clone();
        } catch(Exception ex) {
            return null;
        }
    }

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

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public String getLic_key() {
        return lic_key;
    }

    public void setLic_key(String lic_key) {
        this.lic_key = lic_key;
    }

    public String getSecret_key() {
        return secret_key;
    }

    public void setSecret_key(String secret_key) {
        this.secret_key = secret_key;
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

    public Date getApply_time() {
        return apply_time;
    }

    public void setApply_time(Date apply_time) {
        this.apply_time = apply_time;
    }

    public Date getApprove_time() {
        return approve_time;
    }

    public void setApprove_time(Date approve_time) {
        this.approve_time = approve_time;
    }

    public Integer getApprove_state() {
        return approve_state;
    }

    public void setApprove_state(Integer approve_state) {
        this.approve_state = approve_state;
    }
}
