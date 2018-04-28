package com.winning.esb.controller.inspection;

import com.winning.esb.controller.common.CommonOperation;
import com.winning.esb.model.InspectionIndexModel;
import com.winning.esb.service.IInspectionIndexService;
import com.winning.webapp.framework.core.api.response.message.AjaxTableResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by xuehao on 2017/8/23.
 */
@Controller
@RequestMapping(value = {"/ajax/inspection/index/"})
public class InspectionIndexController {
    @Autowired
    private IInspectionIndexService service;

    @RequestMapping(value = {"list"})
    @ResponseBody
    public AjaxTableResponseMessage list() {
        AjaxTableResponseMessage responseMessage = new AjaxTableResponseMessage();
        try {
            List<InspectionIndexModel> list = service.list();
            responseMessage.setDatas(list);
            responseMessage.setSuccess(true);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }


}