package com.winning.esb.service;

import com.winning.esb.model.SimulationTestStepLogModel;
import com.winning.esb.model.SvcInfoModel;
import com.winning.esb.model.SvcUrlModel;
import com.winning.esb.model.enums.SvcStructureEnum;

/**
 * Created by xuehao on 2017/8/29.
 */
public interface ISimulationTestBusinessService {
    /**
     * 开启模拟的ESB服务
     */
    void startEsbService(SvcUrlModel obj);

    /**
     * 发送消息
     */
    SimulationTestStepLogModel send(Integer sid, String msg, String esbTestUrl) throws Exception;

    /**
     * 重置初始化公共变量，开启消息接收模式
     */
    void receiveStart(Integer tid, SvcInfoModel svcInfoModel);

    /**
     * 接收消息
     */
    SimulationTestStepLogModel receive();

    /**
     * 填充单元测试的请求消息
     */
    String fillMsgFromTestUnit(String msg) throws Exception;

    /**
     * 填充集成测试的请求或应答消息
     */
    String fillMsgFromTestFlow(String msg, SvcStructureEnum.DirectionEnum directionEnum);

}