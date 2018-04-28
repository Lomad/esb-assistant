package com.winning.esb.model.biz;

/**
 * 集成平台数据协议1.1的应答消息
 *
 * @author xuehao
 * @date 2017/11/22
 */
public class EsbDataProtocalResponse_1_1 {
    private EsbDataProtocalResponseHead_1_1 head;
    private Object body;

    public EsbDataProtocalResponseHead_1_1 getHead() {
        return head;
    }

    public void setHead(EsbDataProtocalResponseHead_1_1 head) {
        this.head = head;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}