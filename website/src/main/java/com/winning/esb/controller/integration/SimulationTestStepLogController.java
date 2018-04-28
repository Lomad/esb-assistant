package com.winning.esb.controller.integration;

import com.winning.esb.controller.common.CommonOperation;
import com.winning.esb.model.SimulationTestStepLogModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.SimpleObject;
import com.winning.esb.service.ISimulationTestStepLogService;
import com.winning.esb.utils.JsonUtils;
import com.winning.esb.utils.StringUtils;
import com.winning.webapp.framework.core.api.response.message.AjaxDataResponseMessage;
import com.winning.webapp.framework.core.api.response.message.AjaxTableResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author xuehao
 * @date 2017/8/15
 */
@Controller
@RequestMapping(value = {"/view/integrationManage/simulationTestStepLog/",
        "/view/integrationManage/simulationTestStepLog",
        "/ajax/integrationManage/simulationTestStepLog/"})
public class SimulationTestStepLogController {
    @Autowired
    private ISimulationTestStepLogService service;

    @RequestMapping("query")
    @ResponseBody
    public AjaxTableResponseMessage query(String datas) {
        AjaxTableResponseMessage responseMessage = new AjaxTableResponseMessage();
        try {
            Map<String, Object> map = JsonUtils.jsonToMap(datas);
            CommonObject commonObject = service.query(map);
            CommonOperation.resultCommonObject(responseMessage, commonObject);
            responseMessage.setSuccess(true);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    /**
     * 保存明细步骤
     */
    @RequestMapping(value = {"save"})
    @ResponseBody
    public AjaxDataResponseMessage save(SimulationTestStepLogModel obj) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            SimpleObject result = service.save(obj);
            String err = result.getItem1();
            if (StringUtils.isEmpty(err)) {
                SimulationTestStepLogModel objTemp = service.getByID(Integer.parseInt(result.getItem2()));
                responseMessage.setData(objTemp);
                responseMessage.setSuccess(true);
            } else {
                CommonOperation.resultError(responseMessage, err);
            }
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"getByTID"})
    @ResponseBody
    public AjaxTableResponseMessage getByTID(Integer tid) {
        AjaxTableResponseMessage responseMessage = new AjaxTableResponseMessage();
        try {
            List<SimulationTestStepLogModel> list = service.getByTID(tid);
            responseMessage.setDatas(list);
            responseMessage.setSuccess(true);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping("getLatestSidByTID")
    @ResponseBody
    public AjaxDataResponseMessage getLatestSidByTID(Integer tid) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            responseMessage.setData(service.getLatestSidByTID(tid));
            responseMessage.setSuccess(true);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

}