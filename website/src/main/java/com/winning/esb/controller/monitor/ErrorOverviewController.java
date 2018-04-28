package com.winning.esb.controller.monitor;

import com.winning.esb.controller.common.CommonOperation;
import com.winning.esb.utils.JsonUtils;
import com.winning.esb.utils.MapUtils;
import com.winning.monitor.data.api.IErrorOverViewService;
import com.winning.monitor.data.api.transaction.domain.TransactionMessageList;
import com.winning.webapp.framework.core.api.response.message.AjaxDataResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

/**
 * @author xuehao
 * @date 2017/12/14
 */
@Controller
@RequestMapping(value = {"/view", "/ajax/error"})
public class ErrorOverviewController {
    @Autowired
    private IErrorOverViewService service;

    @RequestMapping(value = {"/errorOverview"})
    public ModelAndView indexServiceMonitor() {
        return new ModelAndView("monitor/error/errorOverview");
    }

    @RequestMapping(value = {"countErrorProviders"})
    @ResponseBody
    public AjaxDataResponseMessage countErrorProviders(@RequestBody Map<String, Object> map) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            List<Integer> result = service.countErrorProviders(map);
            CommonOperation.resultObject(responseMessage, result);
        } catch (Exception ex) {
            CommonOperation.resultException(responseMessage, ex);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"countErrorList"})
    @ResponseBody
    public AjaxDataResponseMessage countErrorList(String datas) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            Map<String, Object> map = JsonUtils.jsonToMap(datas);
            List<Map<String, Object>> result = service.countErrorList(map);
            CommonOperation.resultObject(responseMessage, result);
        } catch (Exception ex) {
            CommonOperation.resultException(responseMessage, ex);
        }
        return responseMessage;
    }

    @RequestMapping(value = "queryTodayErrorTransactionMessageList")
    @ResponseBody
    public TransactionMessageList queryTodayErrorMessageList(String datas) {
        try {
            Map<String, Object> params = JsonUtils.jsonToMap(datas);
            String serverId = MapUtils.getValue(params, "serverAppName");
            String keyWords = MapUtils.getValue(params, "keyWords");
            int startIndex = Integer.parseInt(params.get("start").toString());
            int pageSize = Integer.parseInt(params.get("pageSize").toString());

            return service.queryTodayErrorMessageList(serverId, keyWords, startIndex, pageSize);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}