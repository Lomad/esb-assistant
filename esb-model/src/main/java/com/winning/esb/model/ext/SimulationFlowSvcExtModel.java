package com.winning.esb.model.ext;

import com.winning.esb.model.AppInfoModel;
import com.winning.esb.model.SimulationFlowSvcModel;
import com.winning.esb.model.SimulationTestStepLogModel;
import com.winning.esb.model.SvcInfoModel;

import java.util.List;

/**
 * @author xuehao
 * @date 2017/8/29
 */
public class SimulationFlowSvcExtModel {
    private SimulationFlowSvcModel obj;
    /**
     * 该步骤服务的消费方
     */
    private AppInfoModel consumer;
    /**
     * 该步骤服务的提供方
     */
    private AppInfoModel provider;
    private SvcInfoModel svc;
    /**
     * 最近测试日志对象
     */
    private SimulationTestStepLogModel log;
    /**
     * 最近测试日志对象
     */
    private List<SimulationTestStepLogModel> logList;
    /**
     * 最近测试结果
     */
    private Integer latestTestResult;

    public SimulationFlowSvcExtModel() {
    }

    public SimulationFlowSvcExtModel(SimulationFlowSvcModel obj, AppInfoModel consumer, AppInfoModel provider,
                                     SvcInfoModel svc, SimulationTestStepLogModel log) {
        this.obj = obj;
        this.consumer = consumer;
        this.provider = provider;
        this.svc = svc;
        this.log = log;
    }

    public SimulationFlowSvcModel getObj() {
        return obj;
    }

    public void setObj(SimulationFlowSvcModel obj) {
        this.obj = obj;
    }

    public AppInfoModel getConsumer() {
        return consumer;
    }

    public void setConsumer(AppInfoModel consumer) {
        this.consumer = consumer;
    }

    public AppInfoModel getProvider() {
        return provider;
    }

    public void setProvider(AppInfoModel provider) {
        this.provider = provider;
    }

    public SvcInfoModel getSvc() {
        return svc;
    }

    public void setSvc(SvcInfoModel svc) {
        this.svc = svc;
    }

    public SimulationTestStepLogModel getLog() {
        return log;
    }

    public void setLog(SimulationTestStepLogModel log) {
        this.log = log;
    }

    public List<SimulationTestStepLogModel> getLogList() {
        return logList;
    }

    public void setLogList(List<SimulationTestStepLogModel> logList) {
        this.logList = logList;
    }

    public Integer getLatestTestResult() {
        return latestTestResult;
    }

    public void setLatestTestResult(Integer latestTestResult) {
        this.latestTestResult = latestTestResult;
    }
}