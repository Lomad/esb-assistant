package com.winning.esb.controller.svc;

import com.winning.esb.controller.common.CommonOperation;
import com.winning.esb.model.SvcInfoModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.SimpleObject;
import com.winning.esb.model.common.SimpleObjectMap;
import com.winning.esb.model.enums.SvcInfoEnum;
import com.winning.esb.model.enums.SvcStructureEnum;
import com.winning.esb.model.enums.SvcUrlEnum;
import com.winning.esb.service.IAppInfoService;
import com.winning.esb.service.ISvcGroupService;
import com.winning.esb.service.ISvcInfoService;
import com.winning.esb.service.ISvcUrlService;
import com.winning.esb.utils.JsonUtils;
import com.winning.esb.utils.StringUtils;
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
 * @author xuehao
 */
@Controller
@RequestMapping(value = {"/view/serviceManage/svcInfo", "/view/serviceManage/svcInfo/", "/ajax/serviceManage/svcInfo/"
        , "/ajax_pub/serviceManage/svcInfo/"})
public class SvcInfoController {
    @Autowired
    private ISvcInfoService service;
    @Autowired
    private IAppInfoService appInfoService;
    @Autowired
    private ISvcUrlService svcUrlService;
    @Autowired
    private ISvcGroupService svcGroupService;

    @RequestMapping(value = {""})
    public ModelAndView loadPage() {
        return new ModelAndView("serviceManage/svcInfo");
    }

    /**
     * 获取基础信息（aid、urlId、……）
     */
    @RequestMapping(value = {"getBaseInfo"})
    @ResponseBody
    public SimpleObjectMap getBaseInfo() {
        SimpleObjectMap simpleObjectMap = new SimpleObjectMap();
        Map<String, List<SimpleObject>> map = new HashMap<>();
        map.put("aid", appInfoService.listIdName());
        map.put("aidAppId", appInfoService.listIdAppId());
        map.put("groupId", svcGroupService.listIdName());
        map.put("urlId", svcUrlService.listIdName(SvcUrlEnum.EsbAgentEnum.No.getCode()));
        map.put("urlAgentId", svcUrlService.listIdName(SvcUrlEnum.EsbAgentEnum.Yes.getCode()));
        map.put("msgType", SvcInfoEnum.MsgTypeEnum.getSimpleList());
        map.put("dataProtocal", SvcInfoEnum.DataProtocalEnum.getSimpleList());
        map.put("direction", SvcStructureEnum.DirectionEnum.getSimpleList());
        map.put("otherMark", SvcInfoEnum.OtherMarkEnum.getSimpleList());
        map.put("status", SvcInfoEnum.StatusEnum.getSimpleList());
        simpleObjectMap.setMap(map);
        return simpleObjectMap;
    }

    @RequestMapping(value = {"query"})
    @ResponseBody
    public AjaxTableResponseMessage query(String datas) {
        AjaxTableResponseMessage responseMessage = new AjaxTableResponseMessage();
        try {
            Map<String, Object> map = JsonUtils.jsonToMap(datas);
            CommonObject commonObject = service.queryExt(map);
            CommonOperation.resultCommonObject(responseMessage, commonObject);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"save"})
    @ResponseBody
    public AjaxDataResponseMessage save(SvcInfoModel obj) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            SimpleObject simpleObject = service.save(obj);
            if (StringUtils.isEmpty(simpleObject.getItem2())) {
                responseMessage.setSuccess(true);
                responseMessage.setData(simpleObject.getItem1());
            } else {
                responseMessage.setSuccess(false);
                responseMessage.setErrorMsg(simpleObject.getItem2());
            }
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
     * 发布服务
     */
    @RequestMapping(value = {"publish"})
    @ResponseBody
    public AjaxDataResponseMessage publish(String strIdList) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            List<Integer> idList = JsonUtils.jsonToList(strIdList, Integer.class);
            String err = service.publish(idList);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    /**
     * 回收（下架）服务
     */
    @RequestMapping(value = {"rollback"})
    @ResponseBody
    public AjaxDataResponseMessage rollback(String strIdList) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            List<Integer> idList = JsonUtils.jsonToList(strIdList, Integer.class);
            String err = service.rollback(idList);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    /**
     * 复制服务
     */
    @RequestMapping(value = {"copy"})
    @ResponseBody
    public AjaxDataResponseMessage copy(String targetAidList, String strIdList) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            String err = "";
            List<Integer> idList = JsonUtils.jsonToList(strIdList, Integer.class);
            List<Integer> aidList = JsonUtils.jsonToList(targetAidList, Integer.class);
            err = service.copy(idList, aidList);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    /**
     * 从服务代码里找出aid
     */
    @RequestMapping(value = {"aidListFromSidList"})
    @ResponseBody
    public AjaxDataResponseMessage aidListFromSidList(String strIdList) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            List<Integer> idList = JsonUtils.jsonToList(strIdList, Integer.class);
            List<Integer> aidList = service.aidListFromSidList(idList);
            responseMessage.setSuccess(true);
            responseMessage.setData(aidList);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }


}
