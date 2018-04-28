package com.winning.esb.service;

import com.winning.esb.model.SimulationFlowSvcModel;

import java.util.List;
import java.util.Map;

/**
 * @author xuehao
 * @date 2017/8/21
 */
public interface ISimulationFlowSvcService {

    /**
     * 新增集成测试的场景
     */
    String insert(Integer fid, Integer aid, List<Integer> sidList);

    /**
     * 新增集成测试场景中的服务
     */
    String insert(List<SimulationFlowSvcModel> objList);

    /**
     * 更新步骤排序（key - 步骤ID， value - 排序）
     */
    String updateOrder(List<SimulationFlowSvcModel> flowSvcModelList);

    String delete(Integer id);

    String delete(List<Integer> idList);

    List<SimulationFlowSvcModel> query(Map<String, Object> map);

    /**
     * 根据流程ID获取所有步骤
     */
    List<SimulationFlowSvcModel> queryByFlowID(Integer fid);

    /**
     * 根据服务ID和消费方系统ID获取所有步骤ID
     */
    List<Integer> querySidByFidAid(Integer fid, Integer aid);

}