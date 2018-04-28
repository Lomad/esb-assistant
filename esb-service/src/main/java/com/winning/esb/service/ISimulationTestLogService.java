package com.winning.esb.service;

import com.winning.esb.model.SimulationTestLogModel;
import com.winning.esb.model.common.SimpleObject;

import java.util.List;

/**
 *
 * @author xuehao
 * @date 2017/8/21
 */
public interface ISimulationTestLogService {

    SimulationTestLogModel getByID(Integer id);

    /**
     * 获取每个测试流程的最近一次测试
     * @param fidList   如果为空，则获取全部测试流程，否则根据流程ID列表获取
     */
    List<SimulationTestLogModel> getLatestTest(List<Integer> fidList);

    /**
     * 开始模拟测试，并返回主键ID
     */
    void createTestLog(SimulationTestLogModel obj);

    /**
     * 完成模拟测试
     */
    SimulationTestLogModel finishTestLog(SimulationTestLogModel obj);

    /**
     * 获取结果枚举值列表
     */
    List<SimpleObject> getResultEnum();

    Integer testResult(Integer aid);


}