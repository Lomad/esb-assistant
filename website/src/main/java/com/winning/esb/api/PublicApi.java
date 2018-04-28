package com.winning.esb.api;

import com.winning.esb.utils.AppCtxUtils;
import com.winning.esb.utils.StringUtils;
import com.winning.webapp.framework.core.api.response.message.AjaxResponseMessage;
import com.winning.webapp.framework.core.api.response.message.AjaxTableResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;

/**
 * 公共服务接口
 *
 * @author xuehao
 * @date 2017/10/26
 */
@Controller
@RequestMapping(value = {"/api", "/api/"})
public class PublicApi {
    @Autowired
    private EsbReceiveMsg esbReceiveMsg;

    /**
     * 入口路由【content-type最好设置为“text/plain;Charset=UTF-8”】
     * 消息模版：
     * {
     * action : "业务代码",
     * msg : "正文：可以是普通字符串，也可以是JSON对象"
     * }
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResponseMessage entry(@RequestBody LinkedHashMap<String, Object> map) {
        AjaxResponseMessage responseMessage = new AjaxTableResponseMessage();

        try {
            String err = "";
            Object action = map.get("action");
            if (StringUtils.isEmpty(action)) {
                err += "action不能为空";
            }
            if (StringUtils.isEmpty(err)) {
                IPublicApiBiz publicApiBiz = AppCtxUtils.getBean("publicApi" + action);
                return publicApiBiz.handle(map.get("msg"));
            }

            if (StringUtils.isEmpty(err)) {
                responseMessage.setSuccess(true);
            } else {
                responseMessage.setSuccess(false);
                responseMessage.setErrorMsg(err);
            }
        } catch (Exception ex) {
            responseMessage.setSuccess(false);
            responseMessage.setErrorMsg("发生异常错误！" + ex.getMessage());
        }

        return responseMessage;
    }

    /**
     * 接收单元测试的请求消息【content-type最好设置为“text/plain;Charset=UTF-8”】
     */
    @RequestMapping(value = "testUnitReq", method = RequestMethod.POST)
    @ResponseBody
    public String receiveFromTestUnitReq(@RequestBody String msg) {
        String result;
        try {
            result = esbReceiveMsg.receiveFromTestUnitReq(msg);
        } catch (Exception ex) {
            result = "发生异常错误！" + ex.getMessage();
        }

        return result;
    }

    /**
     * 接收集成测试的请求消息【content-type最好设置为“text/plain;Charset=UTF-8”】
     */
    @RequestMapping(value = "testFlowReq", method = RequestMethod.POST)
    @ResponseBody
    public String receiveFromTestFlowReq(@RequestBody String msg) {
        String result;
        try {
            result = esbReceiveMsg.receiveFromTestFlowReq(msg);
        } catch (Exception ex) {
            result = "发生异常错误！" + ex.getMessage();
        }

        return result;
    }

    /**
     * 接收集成测试的应答消息【content-type最好设置为“text/plain;Charset=UTF-8”】
     */
    @RequestMapping(value = "testFlowAck", method = RequestMethod.POST)
    @ResponseBody
    public String receiveFromTestFlowAck(@RequestBody String msg) {
        String result;
        try {
            result = esbReceiveMsg.receiveFromTestFlowAck(msg);
        } catch (Exception ex) {
            result = "发生异常错误！" + ex.getMessage();
        }

        return result;
    }

}