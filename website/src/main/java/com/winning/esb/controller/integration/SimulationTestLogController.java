package com.winning.esb.controller.integration;

import com.winning.esb.controller.common.CommonOperation;
import com.winning.esb.model.SimulationTestLogModel;
import com.winning.esb.model.common.SimpleObject;
import com.winning.esb.service.ISimulationTestLogService;
import com.winning.webapp.framework.core.api.response.message.AjaxDataResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author xuehao
 * @date 2017/8/15
 */
@Controller
@RequestMapping(value = {"/view/integrationManage/simulationTestLog/", "/view/integrationManage/simulationTestLog",
        "/ajax/integrationManage/simulationTestLog/"})
public class SimulationTestLogController {
    @Autowired
    private ISimulationTestLogService service;

    /**
     * 生成模拟测试主日志
     */
    @RequestMapping(value = {"createTestLog"})
    @ResponseBody
    public AjaxDataResponseMessage createTestLog(SimulationTestLogModel testLogModel) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            service.createTestLog(testLogModel);
            responseMessage.setData(testLogModel);
            responseMessage.setSuccess(true);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    /**
     * 结束模拟测试主日志
     */
    @RequestMapping(value = {"finishTestLog"})
    @ResponseBody
    public AjaxDataResponseMessage finishTestLog(SimulationTestLogModel testLogModel) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            SimulationTestLogModel result = service.finishTestLog(testLogModel);
            responseMessage.setData(result);
            responseMessage.setSuccess(true);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    /**
     * 根据系统ID查询是否联调完成
     */
    @RequestMapping(value = {"testResult"})
    @ResponseBody
    public AjaxDataResponseMessage testResult(Integer aid) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            Integer result = service.testResult(aid);
            responseMessage.setData(result);
            responseMessage.setSuccess(true);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }


}