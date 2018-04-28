package com.winning.esb.model.biz;

/**
 * 集成平台数据协议1.1的请求消息
 *
 * @author xuehao
 * @date 2017/11/22
 */
public class EsbDataProtocalRequest_1_1 {
    private EsbDataProtocalRequestHead_1_1 head;
    private Object body;

    public EsbDataProtocalRequestHead_1_1 getHead() {
        return head;
    }

    public void setHead(EsbDataProtocalRequestHead_1_1 head) {
        this.head = head;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}