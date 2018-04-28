package com.winning.esb.dao;

import com.winning.esb.model.SimulationTestStepLogModel;
import com.winning.esb.model.common.CommonObject;

import java.util.List;
import java.util.Map;

/**
 *
 * @author xuehao
 * @date 2017/8/21
 */
public interface ISimulationTestStepLogDao {

    Integer insert(SimulationTestStepLogModel obj);

    List<SimulationTestStepLogModel> getByID(List<Integer> idList);

    /**
     * 根据测试主ID获取明细的测试步骤
     *
     * @param tid 测试主ID
     */
    List<SimulationTestStepLogModel> getByTID(Integer tid);

    /**
     * 根据测试主ID获取每个明细测试步骤的最新测试
     *
     * @param tid 测试主ID
     */
    List<SimulationTestStepLogModel> getLatestByTID(Object tid);

    /**
     * 根据测试主ID获取最近一次的测试步骤对应的服务ID
     *
     * @param tid 测试主ID
     */
    Integer getLatestSidByTID(Integer tid);

    /**
     * 根据业务系统ID统计明细的概要信息，返回的列名：sid - 测试服务ID， testCount-测试次数， result-最近一次的结果标志
     *
     * @param tid 测试日志主ID
     */
    List<Map<String, Object>> countByTID(Integer tid);

    /**
     * 根据条件获取明细的信息
     *
     */
    CommonObject query(Map<String, Object> map);

    /**
     * 获取已进行单元测试的服务ID列表（key - 服务id， value - 测试结果）
     */
    Map<Integer, Integer> queryUnitTestedSidList();
}