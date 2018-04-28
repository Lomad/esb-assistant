package com.winning.esb.service;

import com.winning.esb.model.SimulationFlowModel;
import com.winning.esb.model.SimulationTestLogModel;
import com.winning.esb.model.SimulationTestStepLogModel;
import com.winning.esb.model.SvcInfoModel;
import com.winning.esb.model.common.ResultObject;
import com.winning.esb.model.common.TreeModel;

import java.util.List;
import java.util.Map;

/**
 * @author xuehao
 * @date 2017/8/21
 */
public interface ISimulationFlowService {

    String save(SimulationFlowModel obj);

    String delete(Integer id);

    SimulationFlowModel getByID(Integer id);

    List<SimulationFlowModel> query(Map<String, Object> map);

    /**
     * 创建流程测试的树结构
     */
    List<TreeModel> getTree(Map<String, Object> map) throws Exception;

    /**
     * 开始模拟测试，并返回主键ID
     */
    void receiveStart(Integer tid, SvcInfoModel svcInfoModel);

    /**
     * 接收消息，并返回给前端
     */
    SimulationTestStepLogModel receive();

    /**
     * 完成模拟测试
     */
    void receiveStop(SimulationTestLogModel obj);

    /**
     * 生成日志PDF
     */
    ResultObject downLogPdf(Integer fid, Integer tid);
}