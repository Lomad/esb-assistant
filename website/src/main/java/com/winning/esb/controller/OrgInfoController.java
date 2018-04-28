package com.winning.esb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.winning.esb.controller.common.CommonOperation;
import com.winning.esb.model.OrgInfoModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.service.IOrgInfoService;
import com.winning.esb.utils.JsonUtils;
import com.winning.webapp.framework.core.api.response.message.AjaxDataResponseMessage;
import com.winning.webapp.framework.core.api.response.message.AjaxTableResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

/**
 * Created by xuehao on 2017/8/9.
 */
@Controller
@RequestMapping(value = {"/view/baseManage/orgInfo", "/view/baseManage/orgInfo/", "/ajax/baseManage/orgInfo/"
        , "/ajax_pub/baseManage/orgInfo/"})
public class OrgInfoController {
    @Autowired
    private IOrgInfoService service;

    @RequestMapping(value = {""})
    public ModelAndView loadPage(){return new ModelAndView("baseManage/orgInfo");}

    @RequestMapping(value = {"query"})
    @ResponseBody
    public AjaxTableResponseMessage query(String datas) {
        AjaxTableResponseMessage responseMessage = new AjaxTableResponseMessage();
        try {
            Map<String, Object> map = JsonUtils.jsonToMap(datas);
            CommonObject commonObject = service.query(map);
            CommonOperation.resultCommonObject(responseMessage, commonObject);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"save"})
    @ResponseBody
    public AjaxDataResponseMessage save(OrgInfoModel obj) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            String err = service.save(obj);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"delete"})
    @ResponseBody
    public AjaxDataResponseMessage delete(String strIdList) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            List<Integer> idList = JsonUtils.jsonToList(strIdList, Integer.class);
            String err = service.delete(idList);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

}