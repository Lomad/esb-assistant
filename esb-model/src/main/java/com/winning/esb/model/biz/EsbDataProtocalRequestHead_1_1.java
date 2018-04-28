package com.winning.esb.model.biz;

/**
 * 集成平台数据协议1.1的请求消息头
 *
 * @author xuehao
 * @date 2017/11/22
 */
public class EsbDataProtocalRequestHead_1_1 {
    /**
     * 版本号
     */
    private String version;
    /**
     * 传输类型
     */
    private String transferType;
    /**
     * 回调地址
     */
    private String callback;
    /**
     * 服务代码（也称交易代码）
     */
    private String tranCode;
    /**
     * 授权码
     */
    private String licKey;
    /**
     * 消息类型
     */
    private String contentType;
    /**
     * 消息格式
     */
    private String contentEncoding;
    /**
     * 服务消费者所属的机构ID
     */
    private String orgId;
    /**
     * 服务消费者(即调用方)系统ID
     */
    private String appId;
    /**
     * 消息唯一ID
     */
    private String messageId;
    /**
     * 客户端类型
     */
    private String appType;
    /**
     * 加密策略
     */
    private String securityPolicy;
    /**
     * 加密策略需要的内容(如公钥)
     */
    private String securityContent;
    /**
     * 时间戳
     */
    private String timestamp;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getTranCode() {
        return tranCode;
    }

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }

    public String getLicKey() {
        return licKey;
    }

    public void setLicKey(String licKey) {
        this.licKey = licKey;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getSecurityPolicy() {
        return securityPolicy;
    }

    public void setSecurityPolicy(String securityPolicy) {
        this.securityPolicy = securityPolicy;
    }

    public String getSecurityContent() {
        return securityContent;
    }

    public void setSecurityContent(String securityContent) {
        this.securityContent = securityContent;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}