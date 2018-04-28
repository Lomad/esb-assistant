package com.winning.esb.controller.integration;

import com.winning.esb.controller.common.CommonOperation;
import com.winning.esb.model.GrantModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.SimpleObject;
import com.winning.esb.model.enums.GrantEnum;
import com.winning.esb.service.IGrantService;
import com.winning.esb.service.ISvcInfoService;
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

@Controller
@RequestMapping(value = {"/view/integrationManage/", "/view/integrationManage/", "/ajax/integrationManage/grant/"})
public class GrantController {
    @Autowired
    private IGrantService service;
    @Autowired
    private ISvcInfoService svcInfoService;

    @RequestMapping(value = {"grant", "grant/"})
    public ModelAndView grant() {
        return new ModelAndView("integrationManage/grant");
    }

    @RequestMapping(value = {"grantApply", "grantApply/"})
    public ModelAndView grantApply() {
        ModelAndView mv = new ModelAndView("integrationManage/grantApply");
        mv.addObject("pageType", "1");
        return mv;
    }

    @RequestMapping(value = {"getBaseInfo"})
    @ResponseBody
    public AjaxDataResponseMessage getBaseInfo(Integer aid) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            Map<String, List<SimpleObject>> map = new HashMap<>();
            map.put("sid", svcInfoService.listIdNameByAidNotIn(aid));
            map.put("approve_state", GrantEnum.ApproveStateEnum.getStatusList());
            responseMessage.setData(map);
            responseMessage.setSuccess(true);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    /**
     * 主要用于系统直接添加的授权
     */
    @RequestMapping(value = {"save"})
    @ResponseBody
    public AjaxDataResponseMessage save(String strModel, String strSsidList) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            String err = service.save(strModel, strSsidList);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"apply"})
    @ResponseBody
    public AjaxDataResponseMessage apply(String userid, String strModel, String strSidList) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            String err = service.apply(userid, strModel, strSidList);
            CommonOperation.resultError(responseMessage, err);
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
            CommonObject commonObject = service.queryExt(map);
            CommonOperation.resultCommonObject(responseMessage, commonObject);
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
            List<Integer> idList = JsonUtils.jsonToObject(strIdList, List.class);
            String err = service.delete(idList);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"approveState"})
    @ResponseBody
    public AjaxDataResponseMessage approveState(GrantModel obj) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            String err = service.approveState(obj);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

}