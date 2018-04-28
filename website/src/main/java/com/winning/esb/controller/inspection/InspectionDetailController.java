package com.winning.esb.controller.inspection;

import com.winning.esb.controller.common.CommonOperation;
import com.winning.esb.model.InspectionDetailModel;
import com.winning.esb.service.IInspectionDetailService;
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
@RequestMapping(value = {"/ajax_pub/inspection/detail/"})
public class InspectionDetailController {
    @Autowired
    private IInspectionDetailService service;

    /**
     * 保存巡检结果
     */
    @RequestMapping(value = {"save"})
    @ResponseBody
    public AjaxTableResponseMessage save(InspectionDetailModel obj) {
        AjaxTableResponseMessage responseMessage = new AjaxTableResponseMessage();
        try {
            String err = service.save(obj);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"queryByInsID"})
    @ResponseBody
    public AjaxTableResponseMessage queryByInsID(Integer insID) {
        AjaxTableResponseMessage responseMessage = new AjaxTableResponseMessage();
        try {
            List<InspectionDetailModel> list = service.queryByInsID(insID);
            responseMessage.setDatas(list);
            responseMessage.setSuccess(true);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }


}