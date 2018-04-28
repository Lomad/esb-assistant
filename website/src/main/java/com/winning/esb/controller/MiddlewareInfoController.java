package com.winning.esb.controller;

import com.winning.esb.controller.common.CommonOperation;
import com.winning.esb.service.IConfigsService;
import com.winning.esb.utils.JsonUtils;
import com.winning.webapp.framework.core.api.response.message.AjaxDataResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping(value = {"/view/baseManage/middlewareInfo", "/view/baseManage/middlewareInfo/", "/ajax/baseManage/middlewareInfo/"
        , "/ajax_pub/baseManage/middlewareInfo/"})
public class MiddlewareInfoController {
    @Autowired
    private IConfigsService service;

    @RequestMapping(value = {""})
    public ModelAndView loadPage() {
        return new ModelAndView("baseManage/middlewareInfo");
    }

    @RequestMapping(value = {"save"})
    @ResponseBody
    public AjaxDataResponseMessage save(String datas) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            Map<String, Object> map = JsonUtils.jsonToMap(datas);
            String err = service.save(map);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"query"})
    @ResponseBody
    public AjaxDataResponseMessage query() {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            String json = service.getMiddlewareInfo();
            responseMessage.setData(json);
            responseMessage.setSuccess(true);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

}
