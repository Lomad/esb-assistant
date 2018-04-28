package com.winning.esb.controller.inspection;

import com.alibaba.fastjson.JSON;
import com.winning.esb.controller.common.CommonOperation;
import com.winning.esb.model.InspectionDetailModel;
import com.winning.esb.model.InspectionModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.SimpleObject;
import com.winning.esb.model.common.SimpleObjectMap;
import com.winning.esb.model.ext.InspectionExtModel;
import com.winning.esb.service.IInspectionService;
import com.winning.esb.utils.JsonUtils;
import com.winning.webapp.framework.core.api.response.message.AjaxDataResponseMessage;
import com.winning.webapp.framework.core.api.response.message.AjaxTableResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuehao on 2017/8/23.
 */
@Controller
@RequestMapping(value = {"/view/inspection/main/", "/view/inspection/main", "/ajax/inspection/main/"})
public class InspectionController {
    @Autowired
    private IInspectionService service;

    @RequestMapping(value = {""})
    public ModelAndView modelAndView() {
        return new ModelAndView("/inspection/main");
    }

    /**
     * 获取基础信息
     */
    @RequestMapping(value = {"getBaseInfo"})
    @ResponseBody
    public SimpleObjectMap getSvcTypeList() {
        SimpleObjectMap simpleObjectMap = new SimpleObjectMap();
        Map<String, List<SimpleObject>> map = new HashMap<>();
        map.put("result", service.getResultList());
        simpleObjectMap.setMap(map);
        return simpleObjectMap;
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

    @RequestMapping(value = {"queryByID"})
    @ResponseBody
    public AjaxDataResponseMessage queryByID(Integer id) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            InspectionExtModel model = service.queryByID(id);
            responseMessage.setData(model);
            responseMessage.setSuccess(true);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"save"})
    @ResponseBody
    public AjaxDataResponseMessage save(String mainObject, String childrenObject) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            InspectionModel mainModel = JSON.parseObject(mainObject, InspectionModel.class);
            List<InspectionDetailModel> childrenModel = JSON.parseArray(childrenObject, InspectionDetailModel.class);
            String err = service.insert(mainModel, childrenModel);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"saveResult"})
    @ResponseBody
    public AjaxDataResponseMessage saveResult(InspectionModel obj) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            String err = service.updateResult(obj);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }
}