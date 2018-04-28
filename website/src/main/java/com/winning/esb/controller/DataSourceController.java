package com.winning.esb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.winning.esb.controller.common.CommonOperation;
import com.winning.esb.model.ConfigsModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.SimpleObject;
import com.winning.esb.model.common.SimpleObjectMap;
import com.winning.esb.model.db.DataSourceModel;
import com.winning.esb.service.IConfigsService;
import com.winning.esb.service.db.impl.DataSourceUtils;
import com.winning.webapp.framework.core.api.response.message.AjaxDataResponseMessage;
import com.winning.webapp.framework.core.api.response.message.AjaxTableResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
@RequestMapping({"/view/baseManage/dataSource", "/view/baseManage/dataSource/", "/ajax/baseManage/dataSource/"})
public class DataSourceController {
    @Autowired
    private IConfigsService service;
    private ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping(value = {""})
    public ModelAndView loadPage(){return new ModelAndView("baseManage/dataSource");}

    @RequestMapping(value = {"getBaseInfo"})
    @ResponseBody
    public SimpleObjectMap getBaseInfo() {
        SimpleObjectMap simpleObjectMap = new SimpleObjectMap();
        Map<String, List<SimpleObject>> map = new HashMap<>();
        map.put("dbType", getDbTypeList());
        map.put("type", getTypeList());
        SimpleObject simpleObject1 = new SimpleObject();
        simpleObject1.setItem1("");
        simpleObject1.setItem2("全部");
        SimpleObject simpleObject2 = new SimpleObject();
        simpleObject2.setItem1("9");
        simpleObject2.setItem2("数据库");
        List list = new ArrayList();
        list.add(simpleObject1);
        list.add(simpleObject2);
        for(SimpleObject simpleObject:getTypeList()) {
            list.add(simpleObject);
        }
        map.put("typeList", list);
        simpleObjectMap.setMap(map);
        return simpleObjectMap;
    }

    @RequestMapping(value = {"query"})
    @ResponseBody
    public AjaxTableResponseMessage query(String datas) {
        AjaxTableResponseMessage responseMessage = new AjaxTableResponseMessage();
        try {
            Map<String, Object> map = null;
            if (datas != null) {
                map = this.objectMapper.readValue(datas, Map.class);
            }
            if(map.get("type") == null || map.get("type") == "") {
                map.put("type", Arrays.asList(9, 10, 11, 12));
            }
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

    @RequestMapping(value = {"delete"})
    @ResponseBody
    public AjaxDataResponseMessage delete(String code) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            service.delete(code);
            //CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"testConnect"})
    @ResponseBody
    public AjaxTableResponseMessage testConnect(DataSourceModel dataSourceModel) {
        AjaxTableResponseMessage responseMessage = new AjaxTableResponseMessage();
        try {
            String err = DataSourceUtils.testConnectDB(dataSourceModel);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    /**
     * 获取数据库类型列表
     */
    private List<SimpleObject> getDbTypeList() {
        DataSourceUtils.DataBaseEnum[] items = DataSourceUtils.DataBaseEnum.values();
        List<SimpleObject> simpleObjects = new ArrayList<>();
        SimpleObject simpleObject;
        for (DataSourceUtils.DataBaseEnum item : items) {
            simpleObject = new SimpleObject();
            simpleObject.setItem1(item.getCode());
            simpleObject.setItem2(item.getCode());
            simpleObjects.add(simpleObject);
        }
        return simpleObjects;
    }

    /**
     * 获取web类型列表
     */
    private List<SimpleObject> getTypeList() {
        DataSourceUtils.WebSourceEnum[] items = DataSourceUtils.WebSourceEnum.values();
        List<SimpleObject> simpleObjects = new ArrayList<>();
        SimpleObject simpleObject;
        for (DataSourceUtils.WebSourceEnum item : items) {
            simpleObject = new SimpleObject();
            simpleObject.setItem1(String.valueOf(item.getCode()));
            simpleObject.setItem2(item.getValue());
            simpleObjects.add(simpleObject);
        }
        return simpleObjects;
    }

}
