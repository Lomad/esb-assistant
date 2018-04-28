package com.winning.esb.dao;

import com.winning.esb.model.SimulationFlowSvcModel;

import java.util.List;
import java.util.Map;

/**
 * @author xuehao
 * @date 2017/8/21
 */
public interface ISimulationFlowSvcDao {
    void insert(List<SimulationFlowSvcModel> objList);

    void updateOrder(List<SimulationFlowSvcModel> flowSvcModelList);

    void delete(List<Integer> idList);

    /**
     * 获取步骤
     *
     * @param map 如果aidList与sidList同时出现，筛选时采用“或”的关系
     */
    List<SimulationFlowSvcModel> query(Map<String, Object> map);
}