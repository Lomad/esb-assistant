package com.winning.esb.controller.common;

import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.SimpleObject;
import com.winning.esb.utils.ListUtils;
import com.winning.webapp.framework.core.api.response.message.AjaxDataResponseMessage;
import com.winning.webapp.framework.core.api.response.message.AjaxTableResponseMessage;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by xuehao on 2017/8/1.
 */
public class CommonOperation {
    public static void resultObject(AjaxDataResponseMessage responseMessage, Object obj) {
        responseMessage.setData(obj);
        responseMessage.setSuccess(true);
    }

    public static void resultCommonObject(AjaxDataResponseMessage responseMessage, CommonObject commonObject) {
        responseMessage.setData(commonObject.getDatas());
        responseMessage.setSuccess(true);
    }

    public static void resultCommonObject(AjaxTableResponseMessage responseMessage, CommonObject commonObject) {
        responseMessage.setTotalSize(commonObject.getTotalSize());
        responseMessage.setDatas(commonObject.getDatas());
        responseMessage.setSuccess(true);
    }

    public static void resultError(AjaxDataResponseMessage responseMessage, String err) {
        resultError(responseMessage, err, null);
    }

    public static void resultError(AjaxDataResponseMessage responseMessage, String err, Map<String, Object> map) {
        if (StringUtils.isEmpty(err)) {
            responseMessage.setSuccess(true);
        } else {
            responseMessage.setSuccess(false);
            responseMessage.setErrorMsg(err);
        }
        if (map != null && map.size() > 0) {
            responseMessage.setData(map);
        }
    }

    public static void resultException(AjaxDataResponseMessage responseMessage, Exception ex) {
        resultException(responseMessage, ex, null);
    }

    public static void resultException(AjaxDataResponseMessage responseMessage, Exception ex, Map<String, Object> map) {
        responseMessage.setSuccess(false);
        responseMessage.setErrorMsg(ex.getMessage());
        if (map != null && map.size() > 0) {
            responseMessage.setData(map);
        }
    }

    public static void result(AjaxTableResponseMessage responseMessage, List list) {
        responseMessage.setSuccess(true);
        if (!ListUtils.isEmpty(list)) {
            responseMessage.setDatas(list);
        }
    }

    public static void resultError(AjaxTableResponseMessage responseMessage, String err) {
        resultError(responseMessage, err, null);
    }

    public static void resultError(AjaxTableResponseMessage responseMessage, String err, List<SimpleObject> list) {
        if (StringUtils.isEmpty(err)) {
            responseMessage.setSuccess(true);
        } else {
            responseMessage.setSuccess(false);
            responseMessage.setErrorMsg(err);
        }
        if (!ListUtils.isEmpty(list)) {
            responseMessage.setDatas(list);
        }
    }

    public static void resultException(AjaxTableResponseMessage responseMessage, Exception ex) {
        responseMessage.setSuccess(false);
        responseMessage.setErrorMsg(ex.getMessage());
    }
}