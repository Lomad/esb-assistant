package com.winning.esb.model.biz;

import com.winning.esb.model.ext.SvcStructureExtModel;

/**
 *
 * @author xuehao
 * @date 2017/11/22
 */
public class EsbDataProtocal {
    private SvcStructureExtModel head;
    private SvcStructureExtModel body;

    public SvcStructureExtModel getHead() {
        return head;
    }

    public void setHead(SvcStructureExtModel head) {
        this.head = head;
    }

    public SvcStructureExtModel getBody() {
        return body;
    }

    public void setBody(SvcStructureExtModel body) {
        this.body = body;
    }
}