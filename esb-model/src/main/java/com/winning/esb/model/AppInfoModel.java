package com.winning.esb.model;

import java.util.Date;

/**
 * Created by xuehao on 2017/8/21.
 */
public class AppInfoModel implements Cloneable {
    private Integer id;
    private String appId;
    private String appName;
    private Integer orgId;
    private Integer appType;
    private Integer direction;
    private Integer status;
    private Integer appIdCurrent;
    private String desp;
    private Date ctime;
    private Date mtime;
    private Integer order_num;

    @Override
    public AppInfoModel clone() {
        try {
            return (AppInfoModel) super.clone();
        } catch (Exception ex) {
            return null;
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getAppIdCurrent() {
        return appIdCurrent;
    }

    public void setAppIdCurrent(Integer appIdCurrent) {
        this.appIdCurrent = appIdCurrent;
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
