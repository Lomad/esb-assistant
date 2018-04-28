package com.winning.esb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.winning.esb.controller.common.CommonOperation;
import com.winning.esb.model.SvcGroupModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.TreeModel;
import com.winning.esb.service.ISvcGroupService;
import com.winning.esb.utils.JsonUtils;
import com.winning.webapp.framework.core.api.response.message.AjaxDataResponseMessage;
import com.winning.webapp.framework.core.api.response.message.AjaxTableResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;


@Controller
@RequestMapping(value = {"/view/baseManage/svcGroup", "/view/baseManage/svcGroup/", "/ajax/baseManage/svcGroup/"})
public class SvcGroupController {
    @Autowired
    private ISvcGroupService service;
    private ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping(value = {""})
    public ModelAndView loadPage(){return new ModelAndView("baseManage/svcGroup");}

    @RequestMapping(value = {"query"})
    @ResponseBody
    public AjaxTableResponseMessage query(String datas) {
        AjaxTableResponseMessage responseMessage = new AjaxTableResponseMessage();
        try {
            Map<String, Object> map = null;
            if (datas != null) {
                map = this.objectMapper.readValue(datas, Map.class);
            }
            CommonObject commonObject = service.query(map);
            CommonOperation.resultCommonObject(responseMessage, commonObject);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"save"})
    @ResponseBody
    public AjaxDataResponseMessage save(SvcGroupModel obj) {
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

    /**
     * 生成分组与服务的关系树
     */
    @RequestMapping(value = {"getZTree"})
    @ResponseBody
    public AjaxTableResponseMessage getZTree(String datas) {
        AjaxTableResponseMessage ajaxTableResponseMessage = new AjaxTableResponseMessage();
        try {
            Map<String, Object> map = JsonUtils.jsonToMap(datas);
            List<TreeModel> list = service.createTreeByGroup(map);
            ajaxTableResponseMessage.setDatas(list);
            ajaxTableResponseMessage.setSuccess(true);
        } catch (Exception e) {
            CommonOperation.resultException(ajaxTableResponseMessage, e);
        }
        return ajaxTableResponseMessage;
    }

}
