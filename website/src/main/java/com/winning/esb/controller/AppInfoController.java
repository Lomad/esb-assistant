package com.winning.esb.controller;

import com.winning.esb.controller.common.CommonOperation;
import com.winning.esb.model.AppInfoModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.SimpleObject;
import com.winning.esb.model.common.SimpleObjectMap;
import com.winning.esb.model.common.TreeModel;
import com.winning.esb.model.enums.AppInfoEnum;
import com.winning.esb.model.ext.AppInfoExtModel;
import com.winning.esb.service.IAppInfoService;
import com.winning.esb.service.IOrgInfoService;
import com.winning.esb.utils.JsonUtils;
import com.winning.webapp.framework.core.api.response.message.AjaxDataResponseMessage;
import com.winning.webapp.framework.core.api.response.message.AjaxTableResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = {"/view/baseManage/appInfo", "/view/baseManage/appInfo/", "/ajax/baseManage/appInfo/"
        , "/ajax_pub/baseManage/appInfo/"})
public class AppInfoController {
    @Autowired
    private IAppInfoService service;
    @Autowired
    private IOrgInfoService orgService;

    @RequestMapping(value = {""})
    public ModelAndView loadPage() {
        return new ModelAndView("baseManage/appInfo");
    }

    /**
     * 获取基础信息（appType、status）
     */
    @RequestMapping(value = {"getBaseInfo"})
    @ResponseBody
    public SimpleObjectMap getBaseInfo() {
        SimpleObjectMap simpleObjectMap = new SimpleObjectMap();
        Map<String, List<SimpleObject>> map = new HashMap<>();
        map.put("appType", AppInfoEnum.AppTypeEnum.getSimpleList());
        map.put("direction", AppInfoEnum.DirectionEnum.getSimpleList());
        map.put("status", AppInfoEnum.StatusEnum.getSimpleList());
        map.put("org", orgService.listIdName());
        simpleObjectMap.setMap(map);
        return simpleObjectMap;
    }

    @RequestMapping(value = {"listActiveWithStatistic"})
    @ResponseBody
    public AjaxTableResponseMessage listActiveWithStatistic(@RequestBody Map datas) {
        AjaxTableResponseMessage responseMessage = new AjaxTableResponseMessage();
        try {
            List<AppInfoExtModel> list = service.listActiveWithStatistic(datas);
            responseMessage.setDatas(list);
            responseMessage.setSuccess(true);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

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
    public AjaxDataResponseMessage save(AppInfoModel vo) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            String err = service.save(vo);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"updateStatus"})
    @ResponseBody
    public AjaxDataResponseMessage updateStatus(String strIdList, Integer status) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            List<Integer> idList = JsonUtils.jsonToList(strIdList, Integer.class);
            String err = service.updateStatus(idList, status);
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
     * 生成机构与信息系统的关系树
     */
    @RequestMapping(value = {"getZTree"})
    @ResponseBody
    public AjaxTableResponseMessage getZTree(String datas) {
        AjaxTableResponseMessage ajaxTableResponseMessage = new AjaxTableResponseMessage();
        try {
            Map<String, Object> map = JsonUtils.jsonToMap(datas);
            List<TreeModel> list = service.createZTree(map);
            ajaxTableResponseMessage.setDatas(list);
            ajaxTableResponseMessage.setSuccess(true);
        } catch (Exception e) {
            CommonOperation.resultException(ajaxTableResponseMessage, e);
        }
        return ajaxTableResponseMessage;
    }

}