package com.winning.esb.controller.integration;

import com.winning.esb.controller.common.CommonOperation;
import com.winning.esb.model.SimulationTestStepLogModel;
import com.winning.esb.model.SvcInfoModel;
import com.winning.esb.model.SvcUrlModel;
import com.winning.esb.model.enums.SimulationTestStepLogEnum;
import com.winning.esb.service.ISimulationTestBusinessService;
import com.winning.esb.service.ISvcUrlService;
import com.winning.esb.service.utils.EsbReceiverForTestUnit;
import com.winning.esb.utils.JsonUtils;
import com.winning.esb.utils.StringUtils;
import com.winning.webapp.framework.core.api.response.message.AjaxDataResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 主要用于模拟测试业务相关的接口
 * @author xuehao
 * @date 2017/8/15
 */
@Controller
@RequestMapping(value = {"/ajax/integrationManage/simulationBusinessTest/"})
public class SimulationBusinessController {
    @Autowired
    private ISimulationTestBusinessService service;
    @Autowired
    private ISvcUrlService urlService;

    /**
     * 开启模拟的ESB服务
     *
     * @param urlId 服务地址id
     */
    @RequestMapping(value = {"startEsbService"})
    @ResponseBody
    public AjaxDataResponseMessage startEsbService(Integer urlId) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            SvcUrlModel obj = urlService.getByID(urlId);
            service.startEsbService(obj);
            responseMessage.setSuccess(true);
            responseMessage.setData(obj);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    /**
     * 发送消息
     *
     * @param sid 服务ID
     * @param msg 待发送的消息
     */
    @RequestMapping(value = {"send"})
    @ResponseBody
    public AjaxDataResponseMessage send(Integer sid, String msg, String esbTestUrl) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            SimulationTestStepLogModel result = service.send(sid, msg, esbTestUrl);
            if (result != null) {
                if (result.getResult() == null) {
                    result.setResult(StringUtils.isEmpty(result.getAck_msg()) ? SimulationTestStepLogEnum.ResultEnum.Failure.getCode()
                            : SimulationTestStepLogEnum.ResultEnum.Success.getCode());
                }
                result.setOut_msg(msg);
                responseMessage.setData(result);
                responseMessage.setSuccess(true);
            }
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

}