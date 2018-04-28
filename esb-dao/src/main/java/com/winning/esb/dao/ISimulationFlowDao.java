package com.winning.esb.dao;

import com.winning.esb.model.SimulationFlowModel;

import java.util.List;
import java.util.Map;

/**
 * @author xuehao
 * @date 2017/8/21
 */
public interface ISimulationFlowDao {
    Integer insert(SimulationFlowModel obj);

    void update(SimulationFlowModel obj);

    /**
     * 删除测试流程、步骤以及所有的日志
     *
     * @param id 流程ID
     */
    void delete(Integer id);

    SimulationFlowModel getByID(Integer id);

    List<SimulationFlowModel> query(Map<String, Object> map);
}