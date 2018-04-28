package com.winning.esb.service;

import com.winning.esb.model.Commissioning;

//联调测试
public interface ICommissioning {
    //socket发送测试
    String socektSender(Commissioning obj);
    //socket接收测试
    String socektReceiver(Commissioning obj);
    //消息发送
    String messageSender(Commissioning obj);
    //医技增加医嘱
    String addOrder(Commissioning obj);
    //医技确认
    String orderConfirm(Commissioning obj);
    //报告发布
    String addReport(Commissioning obj);
    //报告状态修改
    String modifyReport(Commissioning obj);
}
