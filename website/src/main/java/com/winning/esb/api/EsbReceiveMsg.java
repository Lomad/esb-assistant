package com.winning.esb.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.winning.esb.model.enums.SvcStructureEnum;
import com.winning.esb.service.ISimulationTestBusinessService;
import com.winning.esb.utils.Base64Utils;
import com.winning.esb.utils.JsonUtils;
import com.winning.esb.utils.StringUtils;
import com.winning.webapp.framework.core.api.response.message.AjaxDataResponseMessage;
import com.winning.webapp.framework.core.api.response.message.AjaxResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @author xuehao
 * @date 2017/10/26
 */
@Controller
public class EsbReceiveMsg {
    @Autowired
    ISimulationTestBusinessService businessService;

    /**
     * 接收单元测试的请求消息
     */
    public String receiveFromTestUnitReq(String msg) throws Exception {
        String result = businessService.fillMsgFromTestUnit(msg);
        return result;
    }

    /**
     * 接收集成测试的请求消息
     */
    public String receiveFromTestFlowReq(String msg) {
        String result = businessService.fillMsgFromTestFlow(msg, SvcStructureEnum.DirectionEnum.In);
        return result;
    }

    /**
     * 接收集成测试的应答消息
     */
    public String receiveFromTestFlowAck(String msg) {
        String result = businessService.fillMsgFromTestFlow(msg, SvcStructureEnum.DirectionEnum.Ack);
        return result;
    }
}