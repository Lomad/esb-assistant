package com.winning.esb.controller;

import com.winning.esb.controller.common.CommonOperation;
import com.winning.esb.model.ConfigsModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.service.IConfigsService;
import com.winning.esb.utils.JsonUtils;
import com.winning.webapp.framework.core.api.response.message.AjaxDataResponseMessage;
import com.winning.webapp.framework.core.api.response.message.AjaxTableResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Map;

@Controller
@RequestMapping(value = {"/view/baseManage/configs", "/view/baseManage/configs/", "/ajax/baseManage/configs/"})
public class ConfigsController {
    @Autowired
    private IConfigsService service;

    @RequestMapping(value = {""})
    public ModelAndView loadPage() {
        return new ModelAndView("baseManage/configs");
    }

//    /**
//     * 获取基础信息（appType、status）
//     */
//    @RequestMapping(value = {"getBaseInfo"})
//    @ResponseBody
//    public SimpleObjectMap getBaseInfo() {
//        SimpleObjectMap simpleObjectMap = new SimpleObjectMap();
//        Map<String, List<SimpleObject>> map = new HashMap<>();
//        map.put("dbType", getDbTypeList());
//        simpleObjectMap.setMap(map);
//        return simpleObjectMap;
//    }

    @RequestMapping(value = {"query"})
    @ResponseBody
    public AjaxTableResponseMessage query(String datas) {
        AjaxTableResponseMessage responseMessage = new AjaxTableResponseMessage();
        try {
            Map<String, Object> map = JsonUtils.jsonToMap(datas);
            map.put("type", Arrays.asList(0, 1, 2, 3));
            map.put("visible", 1);
            CommonObject commonObject = service.query(map);
            CommonOperation.resultCommonObject(responseMessage, commonObject);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }


    @RequestMapping(value = {"edit"})
    @ResponseBody
    public AjaxDataResponseMessage edit(ConfigsModel obj) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            String err = service.editValue(obj);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

//    @RequestMapping(value = {"delete"})
//    @ResponseBody
//    public AjaxDataResponseMessage delete(String code) {
//        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
//        try {
//            service.delete(code);
//            //CommonOperation.resultError(responseMessage, err);
//        } catch (Exception e) {
//            CommonOperation.resultException(responseMessage, e);
//        }
//        return responseMessage;
//    }

//    @RequestMapping(value = {"testConnect"})
//    @ResponseBody
//    public AjaxTableResponseMessage testConnect(DataSourceModel dataSourceModel) {
//        AjaxTableResponseMessage responseMessage = new AjaxTableResponseMessage();
//        try {
//            String err = DataSourceUtils.testConnectDB(dataSourceModel);
//            CommonOperation.resultError(responseMessage, err);
//        } catch (Exception e) {
//            CommonOperation.resultException(responseMessage, e);
//        }
//        return responseMessage;
//    }

//    /**
//     * 获取数据库类型列表
//     */
//    private List<SimpleObject> getDbTypeList() {
//        DataSourceUtils.DataBaseEnum[] items = DataSourceUtils.DataBaseEnum.values();
//        List<SimpleObject> simpleObjects = new ArrayList<>();
//        SimpleObject simpleObject;
//        for (DataSourceUtils.DataBaseEnum item : items) {
//            simpleObject = new SimpleObject();
//            simpleObject.setItem1(item.getCode());
//            simpleObject.setItem2(item.getCode());
//            simpleObjects.add(simpleObject);
//        }
//        return simpleObjects;
//    }

}
