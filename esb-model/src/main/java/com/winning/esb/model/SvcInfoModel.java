package com.winning.esb.model;

import com.winning.esb.model.enums.SvcInfoEnum;

import java.util.Date;

/**
 * Created by xuehao on 2017/8/9.
 */
public class SvcInfoModel implements Cloneable {
    private Integer id;
    private String code;
    private String name;
    private String version;
    private Integer aid;
    private Integer groupId;
    private Integer urlId;
    private String url;
    private Integer urlAgentId;
    private String msgType;
    private Integer dataProtocal;
    private String rawIn;
    private String rawAck;
    private Integer otherMark;
    private String otherInfo;
    private String desp;
    private Integer status;
    private Date ctime;
    private Date mtime;

    @Override
    public SvcInfoModel clone() {
        try {
            return (SvcInfoModel) super.clone();
        } catch (Exception ex) {
            return null;
        }
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

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

    public Integer getAid() {
        return aid;
    }

    public void setAid(Integer aid) {
        this.aid = aid;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getUrlId() {
        return urlId;
    }

    public void setUrlId(Integer urlId) {
        this.urlId = urlId;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public Integer getDataProtocal() {
        return dataProtocal;
    }

    public void setDataProtocal(Integer dataProtocal) {
        this.dataProtocal = dataProtocal;
    }

    public String getRawIn() {
        return rawIn;
    }

    public void setRawIn(String rawIn) {
        this.rawIn = rawIn;
    }

    public String getRawAck() {
        return rawAck;
    }

    public void setRawAck(String rawAck) {
        this.rawAck = rawAck;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
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

    public Integer getUrlAgentId() {
        return urlAgentId;
    }

    public void setUrlAgentId(Integer urlAgentId) {
        this.urlAgentId = urlAgentId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOtherMark() {
        return otherMark;
    }

    public void setOtherMark(Integer otherMark) {
        this.otherMark = otherMark;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}