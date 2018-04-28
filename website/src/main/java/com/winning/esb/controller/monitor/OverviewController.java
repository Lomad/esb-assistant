package com.winning.esb.controller.monitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.winning.esb.controller.monitor.utils.CommonOperation;
import com.winning.monitor.data.api.IOverViewQueryService;
import com.winning.monitor.data.api.base.DayCountWithServers;
import com.winning.monitor.data.api.base.ServerCountWithType;
import com.winning.monitor.data.api.base.ServiceDurationStatisticVO;
import com.winning.monitor.data.api.transaction.domain.TransactionMessageList;
import com.winning.webapp.framework.core.api.response.message.AjaxDataResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuehao on 2017/07/24.
 */
@Controller
@RequestMapping(value = {"/view", "/ajax/overview"})
public class OverviewController {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private IOverViewQueryService overViewQueryService;

    @RequestMapping(value = {"/overview", "/indexServiceMonitor"})
    public ModelAndView overview() {
        return new ModelAndView("monitor/overview/overview");
    }

    /**
     * 获取初始化数据
     */
    @RequestMapping(value = {"init"})
    @ResponseBody
    public AjaxDataResponseMessage init() {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            Map<String, Object> result = overViewQueryService.init();
            responseMessage.setSuccess(true);
            responseMessage.setData(result);
        } catch (Exception ex) {
            responseMessage.setSuccess(false);
            responseMessage.setData(ex.getMessage());
        }
        return responseMessage;
    }

    /**
     * 获取概览首页的概要指标信息
     *
     * @return totalCount - 今日调用
     * failCount - 今日异常
     * appSize - 接入系统
     * serviceSize - 接入服务
     * runTime - 运行时间（如：2017-12-04 11:34:05）
     * runDay - 平台运行天数
     * historyTotalCount - 历史调用
     */
    @RequestMapping(value = {"countByOverView"})
    @ResponseBody
    public AjaxDataResponseMessage countByOverView() {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            Map<String, Object> result = overViewQueryService.countByOverView();
            responseMessage.setSuccess(true);
            responseMessage.setData(result);
        } catch (Exception ex) {
            responseMessage.setSuccess(false);
            responseMessage.setData(ex.getMessage());
        }
        return responseMessage;
    }

    /**
     * 按系统与日期查询调用与消费趋势
     *
     * @param dateType LAST1H(11, "最近一小时"),LAST3H(12, "最近三小时"),LAST6H(13, "最近六小时"),
     *                 TODAY(21, "今日"), YESTODAY(22, "昨日"),CURRENTWEEK(23, "本周"),CURRENTMONTH(24, "本月"),
     */
    @RequestMapping(value = {"queryTrendBySysDate"})
    @ResponseBody
    public AjaxDataResponseMessage queryTrendBySysDate(String appId, Integer dateType) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            List result = overViewQueryService.queryTrendChartData(appId, dateType);
            responseMessage.setSuccess(true);
            responseMessage.setData(result);
        } catch (Exception ex) {
            responseMessage.setSuccess(false);
            responseMessage.setData(ex.getMessage());
        }
        return responseMessage;
    }

    @RequestMapping(value = {"queryTrendChartData"})
    @ResponseBody
    public AjaxDataResponseMessage queryTrendChartData(String data) {
        Map<String, Object> map = readParments(data);
        int type = Integer.parseInt(String.valueOf(map.get("type")));
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        List result = overViewQueryService.indexProect_queryTrendChartData(type);
        responseMessage.setData(result);
        return responseMessage;
    }

    @RequestMapping(value = {"queryClientTypeChartData"})
    @ResponseBody
    public AjaxDataResponseMessage queryClientTypeChartData(String data) {
        Map<String, Object> map = readParments(data);
        int type = Integer.parseInt(String.valueOf(map.get("type")));
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        List result = overViewQueryService.queryClientTypeChartData(type);
        responseMessage.setData(result);
        return responseMessage;
    }

    @RequestMapping(value = {"queryGroupByAppInfo"})
    @ResponseBody
    public AjaxDataResponseMessage queryGroupByAppInfo(@RequestBody Map datas) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        List result = overViewQueryService.queryGroupByAppInfo(datas);
        responseMessage.setSuccess(true);
        responseMessage.setData(result);
        return responseMessage;
    }

    @RequestMapping(value = {"queryDetailsByAppInfo"})
    @ResponseBody
    public AjaxDataResponseMessage queryDetailsByAppInfo(String data) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            Map<String, Object> map = readParments(data);
            String appId = String.valueOf(map.get("appId"));
            boolean showAllProvidedSvc = Boolean.parseBoolean(map.get("showAllProvidedSvc").toString());
            Map result = overViewQueryService.queryDetailsByAppInfo(appId, showAllProvidedSvc);
            responseMessage.setSuccess(true);
            responseMessage.setData(result);
        } catch (Exception ex) {
            responseMessage.setSuccess(false);
            responseMessage.setData(ex.getMessage());
        }
        return responseMessage;
    }

    @RequestMapping(value = "queryErrorConsumers")
    @ResponseBody
    public AjaxDataResponseMessage queryErrorConsumers() {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            List<ServerCountWithType> serverList =
                    overViewQueryService.queryErrorConsumers();
            CommonOperation.convertAppNameWithServerCount(serverList);

            responseMessage.setData(serverList);
            responseMessage.setSuccess(true);
        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage.setData(e.getMessage());
        }
        return responseMessage;
    }

    @RequestMapping(value = "queryTop5Services")
    @ResponseBody
    public AjaxDataResponseMessage queryServiceDuration() {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            List<ServiceDurationStatisticVO> durationList =
                    overViewQueryService.queryServiceDuration();
            CommonOperation.convertServiceName(durationList);

            responseMessage.setData(durationList);
            responseMessage.setSuccess(true);
        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage.setData(e.getMessage());
        }
        return responseMessage;
    }

    @RequestMapping(value = "queryTop5Details")
    @ResponseBody
    public AjaxDataResponseMessage queryDetailsDuration(){
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            TransactionMessageList messageList =
                    overViewQueryService.queryDetailsDuration();

            responseMessage.setData(messageList.getTransactionMessages());
            responseMessage.setSuccess(true);
        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage.setData(e.getMessage());
        }
        return responseMessage;
    }

    @RequestMapping(value = "historyCountStatistic")
    @ResponseBody
    public AjaxDataResponseMessage queryHistoryCountStatistic(@RequestBody Map queryParams) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            String targetDate = (String) queryParams.get("targetDate");
            DayCountWithServers dayCountWithServers = overViewQueryService.queryHistoryCountStatistic(targetDate);

            responseMessage.setData(dayCountWithServers);
            responseMessage.setSuccess(true);
        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage.setData(e.getMessage());
        }
        return responseMessage;
    }

    private Map<String, Object> readParments(String data) {
        Map<String, Object> map = null;
        try {
            map = this.objectMapper.readValue(data, Map.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (map == null) {
            map = new HashMap<String, Object>();
        }
        return map;
    }
}

