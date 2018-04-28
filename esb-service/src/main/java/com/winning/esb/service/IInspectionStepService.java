package com.winning.esb.service;

import java.util.Map;

public interface IInspectionStepService {
    /**
     * 门诊HIS数据
     */
    String checkMzHis(Map<String, Object> resultMap);

    /**
     * 住院HIS数据
     */
    String checkZyHis(Map<String, Object> resultMap);

    /**
     * HL7Engine日志分析
     */
    String checkHL7Engine(Map<String, Object> resultMap);

    /**
     * 终端运行情况
     */
    String checkEndpoint(Map<String, Object> resultMap);

    /**
     * 路由运行情况
     */
    String checkRoute(Map<String, Object> resultMap);

    /**
     * 服务器硬件运行情况
     */
    String checkHardware(Map<String, Object> resultMap);

    /**
     * 操作系统运行情况
     */
    String checkOs(Map<String, Object> resultMap);

    /**
     * 中间件错误列表
     */
    String checkErrorList(Map<String, Object> resultMap);
}
