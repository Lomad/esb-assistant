package com.winning.esb.controller.integration;

import com.winning.esb.controller.common.CommonOperation;
import com.winning.esb.model.SimulationFlowModel;
import com.winning.esb.model.SimulationFlowSvcModel;
import com.winning.esb.model.SimulationTestStepLogModel;
import com.winning.esb.model.SvcInfoModel;
import com.winning.esb.model.common.ResultObject;
import com.winning.esb.model.common.TreeModel;
import com.winning.esb.service.ISimulationFlowService;
import com.winning.esb.service.ISimulationFlowSvcService;
import com.winning.esb.service.utils.EsbReceiverForTestUnit;
import com.winning.esb.utils.JsonUtils;
import com.winning.esb.utils.StringUtils;
import com.winning.webapp.framework.core.api.response.message.AjaxDataResponseMessage;
import com.winning.webapp.framework.core.api.response.message.AjaxTableResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

/**
 * @author xuehao
 * @date 2017/9/25
 */
@Controller
@RequestMapping(value = {"/view/integrationManage/svcFlowTest/", "/view/integrationManage/svcFlowTest",
        "/ajax/integrationManage/svcFlowTest/"})
public class SvcFlowTestController {
    @Autowired
    private ISimulationFlowService service;
    @Autowired
    private ISimulationFlowSvcService flowSvcService;

    @RequestMapping(value = {""})
    public ModelAndView mdelAndView() {
        return new ModelAndView("/integrationManage/svcFlowTest");
    }

    @RequestMapping("getTree")
    @ResponseBody
    public AjaxTableResponseMessage getTree(String datas) {
        AjaxTableResponseMessage responseMessage = new AjaxTableResponseMessage();
        try {
            Map<String, Object> map = JsonUtils.jsonToMap(datas);
            List<TreeModel> list = service.getTree(map);
            responseMessage.setDatas(list);
            responseMessage.setSuccess(true);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    /**
     * 保存测试流程概要信息
     */
    @RequestMapping(value = {"saveFlow"})
    @ResponseBody
    public AjaxDataResponseMessage saveFlow(SimulationFlowModel obj) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            String err = service.save(obj);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    /**
     * 保存测试流程，以及相关的所有步骤、日志等
     */
    @RequestMapping(value = {"deleteFlow"})
    @ResponseBody
    public AjaxDataResponseMessage deleteFlow(Integer id) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            String err = service.delete(id);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"querySidByFidAid"})
    @ResponseBody
    public AjaxTableResponseMessage querySidByFidAid(Integer fid, Integer aid) {
        AjaxTableResponseMessage responseMessage = new AjaxTableResponseMessage();
        try {
            List<Integer> sidList = flowSvcService.querySidByFidAid(fid, aid);
            CommonOperation.result(responseMessage, sidList);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    /**
     * 保存测试流程步骤
     */
    @RequestMapping(value = {"saveFlowSvc"})
    @ResponseBody
    public AjaxDataResponseMessage saveFlowSvc(Integer fid, Integer aid, String strSidList) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            List<Integer> sidList = JsonUtils.jsonToList(strSidList, Integer.class);
            String err = flowSvcService.insert(fid, aid, sidList);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    /**
     * 更新步骤排序（key - 步骤ID， value - 排序）
     */
    @RequestMapping(value = {"updateFlowSvcOrder"})
    @ResponseBody
    public AjaxDataResponseMessage updateFlowSvcOrder(String objs) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            List<SimulationFlowSvcModel> flowSvcModelList = JsonUtils.jsonToList(objs, SimulationFlowSvcModel.class);
            String err = flowSvcService.updateOrder(flowSvcModelList);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    /**
     * 删除测试流程步骤
     */
    @RequestMapping(value = {"deleteFlowSvc"})
    @ResponseBody
    public AjaxDataResponseMessage deleteFlowSvc(Integer id) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            String err = flowSvcService.delete(id);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping("receiveStart")
    @ResponseBody
    public AjaxDataResponseMessage receiveStart(String datas) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            Map<String, Object> map = JsonUtils.jsonToMap(datas);
            SvcInfoModel svcInfoModel = JsonUtils.jsonToObject(JsonUtils.toJson(map.get("svc")), SvcInfoModel.class);
            Integer tid = StringUtils.isEmpty(map.get("tid")) ? 0 : Integer.parseInt(map.get("tid").toString());
            service.receiveStart(tid, svcInfoModel);
            responseMessage.setSuccess(true);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    /**
     * 接收消息
     */
    @RequestMapping(value = {"receive"})
    @ResponseBody
    public AjaxDataResponseMessage receive() {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            SimulationTestStepLogModel result = service.receive();
            responseMessage.setData(result);
            responseMessage.setSuccess(true);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping("receiveStop")
    @ResponseBody
    public AjaxDataResponseMessage receiveStop() {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            EsbReceiverForTestUnit.reset();
            responseMessage.setSuccess(true);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping("downLogPdf")
    @ResponseBody
    public AjaxDataResponseMessage downLogPdf(Integer fid, Integer tid) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            ResultObject resultObject = service.downLogPdf(fid, tid);
            responseMessage.setSuccess(resultObject.isSuccess());
            responseMessage.setErrorMsg(resultObject.getErrorMsg());
            responseMessage.setData(resultObject.getObj());
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }
}