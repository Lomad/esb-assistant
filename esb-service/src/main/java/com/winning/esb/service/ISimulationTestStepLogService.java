package com.winning.esb.service;

import com.winning.esb.model.SimulationTestStepLogModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.SimpleObject;

import java.util.List;
import java.util.Map;

/**
 *
 * @author xuehao
 * @date 2017/8/21
 */
public interface ISimulationTestStepLogService {

    /**
     * 保存步骤明细
     *
     * @return item1 - 错误信息，item2 - 返回主键ID
     */
    SimpleObject save(SimulationTestStepLogModel obj);

    SimulationTestStepLogModel getByID(Integer id);

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
    List<SimulationTestStepLogModel> getLatestByTID(Integer tid);

    /**
     * 根据测试主ID获取每个明细测试步骤的最新测试
     *
     * @param tidList 测试主ID
     */
    List<SimulationTestStepLogModel> getLatestByTID(List<Integer> tidList);

    /**
     * 根据测试主ID获取最近一次的测试步骤对应的服务ID
     *
     * @param tid 测试主ID
     */
    Integer getLatestSidByTID(Integer tid);

    CommonObject query(Map<String, Object> map);

    /**
     * 获取已进行单元测试的服务ID列表
     */
    Map<Integer, Integer> queryUnitTestedSidList();

    /**
     * 将list转为map：key - 服务ID， value - 明细日志对象【不适用同一步骤存在多个日志对象的情况】
     */
    Map<Integer, SimulationTestStepLogModel> mapSidObj(List<SimulationTestStepLogModel> list);

    /**
     * 将list转为map：key - 服务ID， value - 明细日志对象列表
     */
    Map<Integer, List<SimulationTestStepLogModel>> mapSidList(List<SimulationTestStepLogModel> list);
}