package com.winning.esb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.winning.esb.controller.common.CommonOperation;
import com.winning.esb.model.UserModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.SimpleObject;
import com.winning.esb.model.common.SimpleObjectMap;
import com.winning.esb.model.enums.UserEnum;
import com.winning.esb.service.IUserService;
import com.winning.esb.stable.NormalConst;
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
@RequestMapping(value = {"/view/baseManage/user", "/view/baseManage/user/", "/ajax/baseManage/user/"
        , "/ajax_pub/baseManage/user/"})
public class UserController {
    @Autowired
    private IUserService service;
    private ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping(value = {""})
    public ModelAndView loadPage() {
        return new ModelAndView("baseManage/user");
    }

    /**
     * 获取基础信息
     */
    @RequestMapping(value = {"getBaseInfo"})
    @ResponseBody
    public SimpleObjectMap getBaseInfo() {
        SimpleObjectMap simpleObjectMap = new SimpleObjectMap();
        Map<String, List<SimpleObject>> map = new HashMap<>();
        map.put("role", UserEnum.RoleEnum.getSimpleList());
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
            CommonObject commonObject = service.query(map);
            CommonOperation.resultCommonObject(responseMessage, commonObject);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"login"})
    @ResponseBody
    public AjaxDataResponseMessage login(String username, String password) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            UserModel userModelIn = new UserModel();
            userModelIn.setUsername(username);
            userModelIn.setPassword(password);
            UserModel userModel = service.login(userModelIn);
            if(userModel!=null) {
                responseMessage.setSuccess(true);
                responseMessage.setData(userModel);
            } else {
                responseMessage.setSuccess(false);
                responseMessage.setErrorMsg("用户名或密码错误！");
            }
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"save"})
    @ResponseBody
    public AjaxDataResponseMessage save(String strUser, String strAidList) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            UserModel obj = JsonUtils.jsonToObject(strUser, UserModel.class);
            List<Integer> aidList = JsonUtils.jsonToList(strAidList, Integer.class);
            boolean backPwd = obj.getId() == null ? true : false;
            String err = service.save(obj, aidList);
            CommonOperation.resultError(responseMessage, err);
            if (responseMessage.isSuccess() && backPwd) {
                responseMessage.setErrorMsg(NormalConst.PWD_DEFFAULT);
            }
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"changePwd"})
    @ResponseBody
    public AjaxDataResponseMessage changePwd(UserModel obj) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            String error = service.changePwd(obj);
            CommonOperation.resultError(responseMessage,error);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"resetPwd"})
    @ResponseBody
    public AjaxDataResponseMessage resetPwd(Integer id) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            service.resetPwd(id);
            responseMessage.setSuccess(true);
            responseMessage.setErrorMsg(NormalConst.PWD_DEFFAULT);
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
