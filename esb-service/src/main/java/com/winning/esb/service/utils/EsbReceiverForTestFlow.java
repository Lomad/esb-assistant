package com.winning.esb.service.utils;

import com.winning.esb.model.SimulationTestStepLogModel;
import com.winning.esb.model.SvcInfoModel;

/**
 * 用于保存接收来自ESB的测试消息，供前端与ESB交互使用
 * @author xuehao
 * @date 2017/11/21
 */
public class EsbReceiverForTestFlow {
    private static SimulationTestStepLogModel logModel = null;
    private static SvcInfoModel svcModel = null;

    /**
     * 初始化
     */
    public static void init() {
        EsbReceiverForTestFlow.logModel = new SimulationTestStepLogModel();
        EsbReceiverForTestFlow.svcModel = new SvcInfoModel();
    }

    /**
     * 初始化，并开启接收模式
     */
    public static void init(SimulationTestStepLogModel logModel, SvcInfoModel svcModel) {
        EsbReceiverForTestFlow.logModel = logModel;
        EsbReceiverForTestFlow.svcModel = svcModel;
    }

    /**
     * 重置，并关闭接收模式
     */
    public static void reset() {
        logModel = null;
        svcModel = null;
    }

    public static SimulationTestStepLogModel getLogModel() {
        return logModel;
    }

    public static void setLogModel(SimulationTestStepLogModel logModel) {
        EsbReceiverForTestFlow.logModel = logModel;
    }

    public static SvcInfoModel getSvcModel() {
        return svcModel;
    }

    public static void setSvcModel(SvcInfoModel svcModel) {
        EsbReceiverForTestFlow.svcModel = svcModel;
    }
}