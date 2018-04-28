package com.winning.esb.controller.svc;

import com.winning.esb.controller.common.CommonOperation;
import com.winning.esb.model.ext.ValueListExtModel;
import com.winning.esb.service.IValueListService;
import com.winning.webapp.framework.core.api.response.message.AjaxDataResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by xuehao on 2017/9/17.
 */
@Controller
@RequestMapping(value = {"/ajax/serviceManage/valueList/"})
public class ValueListController {
    @Autowired
    private IValueListService service;

    @RequestMapping(value = {"insertAfterDelete"})
    @ResponseBody
    public AjaxDataResponseMessage insertAfterDelete(Integer ssid, Integer resultMark, String strValueList, String strValueListFailure) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            String err = service.insertAfterDelete(ssid, resultMark, strValueList, strValueListFailure);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"getExtBySsid"})
    @ResponseBody
    public AjaxDataResponseMessage getExtBySsid(Integer ssid) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            ValueListExtModel extModel = service.getExtBySsid(ssid);
            if(extModel!=null) {
                extModel.setValueList(null);
                extModel.setValueListFailure(null);
            }
            responseMessage.setSuccess(true);
            responseMessage.setData(extModel);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

}