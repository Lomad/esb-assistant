package com.winning.esb.model.biz;

/**
 * 集成平台数据协议1.1的应答消息头
 *
 * @author xuehao
 * @date 2017/11/22
 */
public class EsbDataProtocalResponseHead_1_1 {
    /**
     * 版本号
     */
    private String version;
    /**
     * 结果代码
     */
    private String ackCode;
    /**
     * 描述信息或错误信息
     */
    private String ackMessage;
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

    public String getAckCode() {
        return ackCode;
    }

    public void setAckCode(String ackCode) {
        this.ackCode = ackCode;
    }

    public String getAckMessage() {
        return ackMessage;
    }

    public void setAckMessage(String ackMessage) {
        this.ackMessage = ackMessage;
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