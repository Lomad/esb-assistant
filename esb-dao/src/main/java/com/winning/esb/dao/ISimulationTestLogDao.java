package com.winning.esb.dao;

import com.winning.esb.model.SimulationTestLogModel;

import java.util.List;

/**
 * @author xuehao
 * @date 2017/8/21
 */
public interface ISimulationTestLogDao {

    SimulationTestLogModel getByID(Integer id);

    /**
     * 获取每个测试流程的最近一次测试
     *
     * @param fidList 如果为空，则获取全部测试流程，否则根据流程ID列表获取
     */
    List<SimulationTestLogModel> getLatestTest(List<Integer> fidList);

    /**
     * 开始模拟测试，并返回主键ID
     */
    Integer createTestLog(SimulationTestLogModel obj);

    /**
     * 完成模拟测试
     */
    void finishTestLog(SimulationTestLogModel obj);

    Integer testResult(Integer aid);

}