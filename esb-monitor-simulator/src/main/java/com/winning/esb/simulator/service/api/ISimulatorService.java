package com.winning.esb.simulator.service.api;

import com.winning.esb.simulator.utils.entity.LoggingEntity;

import java.util.Map;

/**
 * @Author Lemod
 * @Version 2018/4/3
 */
public interface ISimulatorService {

    void startSimulator(Map params);

    boolean checkStatus();

    Map<String, Object> getParams();

    /**
     * 生成模拟消息，主要用于无样本模拟数据生成
     */
    LoggingEntity createSimulateMsg();
}
