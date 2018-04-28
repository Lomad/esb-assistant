package com.winning.esb.model;

import java.util.Date;

/**
 * Created by xuehao on 2017/8/9.
 */
public class OrgInfoModel {
    private Integer id;
    private String code;
    private String name;
    private Integer order_num;
    private String logo;
    private String desp;
    private Date ctime;
    private Date mtime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
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

    public Integer getOrder_num() {
        return order_num;
    }

    public void setOrder_num(Integer order_num) {
        this.order_num = order_num;
    }

}