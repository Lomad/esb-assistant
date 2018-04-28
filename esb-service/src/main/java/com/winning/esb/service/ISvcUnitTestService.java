package com.winning.esb.service;

import com.winning.esb.model.SimulationTestStepLogModel;
import com.winning.esb.model.SvcUrlModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.SimpleObject;

import java.util.Date;
import java.util.Map;

public interface ISvcUnitTestService {
//    /**
//     * 发送消息
//     */
//    SimulationTestStepLogModel send(SvcUrlModel obj, String msg, Integer sid);

    SimulationTestStepLogModel startService(Integer sid, Integer port, Integer time);

    CommonObject testLog(Map<String, Object> map);

    SimulationTestStepLogModel receive();


    /**
     * 下载应答消息内容
     */
    String downloadAck(String ackMsg);

}