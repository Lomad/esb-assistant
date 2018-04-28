package com.winning.esb.controller.inspection;

import com.winning.esb.controller.common.CommonOperation;
import com.winning.esb.service.IInspectionStepService;
import com.winning.webapp.framework.core.api.response.message.AjaxDataResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xuehao on 2017/8/23.
 */
@Controller
@RequestMapping(value = {"/ajax/inspectionStep/"})
public class InspectionStepController {
    @Autowired
    private IInspectionStepService service;

    private final String ACTION_NAME = "action";

    /**
     * 检测门诊HIS
     */
    @RequestMapping(value = {"checkMzHis"})
    @ResponseBody
    public AjaxDataResponseMessage checkMzHis() {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(ACTION_NAME, "checkMzHis");
        try {
            String err = service.checkMzHis(resultMap);
            CommonOperation.resultError(responseMessage, err, resultMap);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e, resultMap);
        }
        return responseMessage;
    }

    /**
     * 检测住院HIS
     */
    @RequestMapping(value = {"checkZyHis"})
    @ResponseBody
    public AjaxDataResponseMessage checkZyHis() {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(ACTION_NAME, "checkZyHis");
        try {
            String err = service.checkZyHis(resultMap);
            CommonOperation.resultError(responseMessage, err, resultMap);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e, resultMap);
        }
        return responseMessage;
    }

    /**
     * HL7Engine日志分析
     */
    @RequestMapping(value = {"checkHL7Engine"})
    @ResponseBody
    public AjaxDataResponseMessage checkHL7Engine() {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(ACTION_NAME, "checkHL7Engine");
        try {
            String err = service.checkHL7Engine(resultMap);
            CommonOperation.resultError(responseMessage, err, resultMap);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e, resultMap);
        }
        return responseMessage;
    }

    /**
     * 终端
     */
    @RequestMapping(value = {"checkEndpoint"})
    @ResponseBody
    public AjaxDataResponseMessage checkEndpoint() {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(ACTION_NAME, "checkEndpoint");
        try {
            String err = service.checkEndpoint(resultMap);
            CommonOperation.resultError(responseMessage, err, resultMap);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e, resultMap);
        }
        return responseMessage;
    }

    /**
     * 路由
     */
    @RequestMapping(value = {"checkRoute"})
    @ResponseBody
    public AjaxDataResponseMessage checkRoute() {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(ACTION_NAME, "checkRoute");
        try {
            String err = service.checkRoute(resultMap);
            CommonOperation.resultError(responseMessage, err, resultMap);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e, resultMap);
        }
        return responseMessage;
    }

    /**
     * 服务器硬件运行情况
     */
    @RequestMapping(value = {"checkHardware"})
    @ResponseBody
    public AjaxDataResponseMessage checkHardware() {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(ACTION_NAME, "checkHardware");
        try {
            String err = service.checkHardware(resultMap);
            CommonOperation.resultError(responseMessage, err, resultMap);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e, resultMap);
        }
        return responseMessage;
    }

    /**
     * 操作系统运行情况
     */
    @RequestMapping(value = {"checkOs"})
    @ResponseBody
    public AjaxDataResponseMessage checkOs() {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(ACTION_NAME, "checkOs");
        try {
            String err = service.checkOs(resultMap);
            CommonOperation.resultError(responseMessage, err, resultMap);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e, resultMap);
        }
        return responseMessage;
    }

}