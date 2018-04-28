package com.winning.esb.controller.integration;

import com.winning.esb.controller.common.CommonOperation;
import com.winning.esb.model.SimulationTestStepLogModel;
import com.winning.esb.model.SvcInfoModel;
import com.winning.esb.model.SvcUrlModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.SimpleObject;
import com.winning.esb.service.ISvcInfoService;
import com.winning.esb.service.ISvcUnitTestService;
import com.winning.esb.service.ISvcUrlService;
import com.winning.esb.utils.JsonUtils;
import com.winning.webapp.framework.core.api.response.message.AjaxDataResponseMessage;
import com.winning.webapp.framework.core.api.response.message.AjaxTableResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.Map;

/**
 * Created by xuehao on 2017/9/25.
 */
@Controller
@RequestMapping(value = {"/view/integrationManage/svcUnitTest/", "/view/integrationManage/svcUnitTest",
        "/ajax/integrationManage/svcUnitTest/"})
public class SvcUnitTestController {
    @Autowired
    private ISvcUnitTestService service;

    @RequestMapping(value = {""})
    public ModelAndView mdelAndView() {
        return new ModelAndView("/integrationManage/svcUnitTest");
    }

//    @RequestMapping("send")
//    @ResponseBody
//    public AjaxDataResponseMessage send(Integer sid, Integer urlAgentId, String msg) {
//        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
//        try {
//            SvcUrlModel obj;
//            if (urlAgentId != null) {
//                obj = urlService.getByID(urlAgentId);
//                SimulationTestStepLogModel result = service.send(obj, msg, sid);
//                if (result != null) {
//                    result.setOut_msg(msg);
//                    responseMessage.setData(result);
//                    responseMessage.setSuccess(true);
//                }
//            } else {
//                String err = "未设置ESB代理地址";
//                CommonOperation.resultError(responseMessage, err);
//            }
//        } catch (Exception e) {
//            CommonOperation.resultException(responseMessage, e);
//        }
//
//        return responseMessage;
//    }

    @RequestMapping("start")
    @ResponseBody
    public AjaxDataResponseMessage start(Integer sid, Integer port) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            SimulationTestStepLogModel result = service.startService(sid, port, null);
            if (result != null) {
                result.setOut_msg(result.getOut_msg());
                responseMessage.setData(result);
                responseMessage.setSuccess(true);
            }
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping("receive")
    @ResponseBody
    public AjaxDataResponseMessage receive() {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            SimulationTestStepLogModel result = service.receive();
            if (result != null) {
                result.setOut_msg(result.getOut_msg());
                responseMessage.setData(result);
                responseMessage.setSuccess(true);
            }
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping("testLog")
    @ResponseBody
    public AjaxTableResponseMessage testLog(String datas) {
        AjaxTableResponseMessage responseMessage = new AjaxTableResponseMessage();
        try {
            Map<String, Object> map = JsonUtils.jsonToMap(datas);
            CommonObject list = service.testLog(map);
            CommonOperation.resultCommonObject(responseMessage, list);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }



    @RequestMapping("downloadAck")
    @ResponseBody
    public AjaxDataResponseMessage downloadAck(String ackMsg) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            String url = service.downloadAck(ackMsg);
            responseMessage.setData(url);
            responseMessage.setSuccess(true);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

}