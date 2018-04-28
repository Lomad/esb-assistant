package com.winning.esb.controller.monitor.restful;

import com.alibaba.fastjson.JSON;
import com.winning.esb.controller.monitor.utils.CommonOperation;
import com.winning.esb.controller.monitor.utils.ScreenControllerUtil;
import com.winning.esb.controller.monitor.utils.SpecialConvertUtil;
import com.winning.monitor.agent.logging.MonitorLogger;
import com.winning.monitor.agent.logging.transaction.Transaction;
import com.winning.monitor.data.api.IOverViewQueryService;
import com.winning.monitor.data.api.ITransactionDataQueryService;
import com.winning.monitor.data.api.base.ServerCountWithType;
import com.winning.monitor.data.api.enums.QueryParameterKeys;
import com.winning.monitor.data.api.largerScreen.IScreenMonitorService;
import com.winning.monitor.data.api.largerScreen.entity.ServiceCount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.winning.esb.controller.monitor.utils.ScreenControllerUtil.MANAGER_NUMBER.殷奇隆;
import static com.winning.esb.controller.monitor.utils.ScreenControllerUtil.addManagerNumber;

/**
 * @Author Lemod
 * @Version 2017/9/22
 */
@Controller
@RequestMapping(value = "/api/bigscreen/monitor/")
public class ServiceShowScreenController {

    private static final Logger logger = LoggerFactory.getLogger(ServiceShowScreenController.class);

    @Autowired
    private IOverViewQueryService overViewQueryService;
    @Autowired
    private ITransactionDataQueryService transactionDataQueryService;
    @Autowired
    private IScreenMonitorService monitorService;


    @RequestMapping(value = "commonHandle", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String CommonHandle(HttpServletRequest request,
                               @RequestParam String action,
                               @RequestParam(required = false) String msg) {
        //大屏 监控埋点
        String ip = ScreenControllerUtil.getClientIPAddress(request);
        MonitorLogger.setCaller("HIP0202", ip, "PC");
        Transaction transaction = null;

        //返回结果
        Map<String, Object> result = new HashMap<>(), params = new HashMap<>();
        params = SpecialConvertUtil.formatJsonString(msg, params);

        try {
            switch (action) {
                case "getOverview":
                    transaction = MonitorLogger.beginTransactionType("HIP050101");

                    //获取概览指标
                    Map<String, Object> primary = overViewQueryService.countByOverView();
                    result.put("countToday", primary.get("totalCount"));
                    result.put("countTodayFail", primary.get("failCount"));
                    result.put("countHistory", primary.get("historyTotalCount"));
                    result.put("countApp", primary.get("appSize"));
                    result.put("countService", primary.get("serviceSize"));
                    result.put("esbRunningDays", primary.get("runDay"));

                    //今日调用量Top5的服务
                    List<ServiceCount> topForTotal = monitorService.getServiceCountList("Total");
                    CommonOperation.convertServiceName(topForTotal);
                    result.put("topForTotal", topForTotal);

                    //当今日异常不为0时，返回异常数Top服务列表，最多5个
                    if (String.valueOf(primary.get("failCount")).equals("0")) {
                        result.put("topForError", null);
                    } else {
                        List<ServiceCount> topForError = monitorService.getServiceCountList("Error");
                        CommonOperation.convertServiceName(topForError);
                        result.put("topForError", topForError);
                    }
                    break;

                case "listApps":
                    transaction = MonitorLogger.beginTransactionType("HIP050102");

                    List<Map<String, Object>> dataList = ScreenControllerUtil.generateResultForListApps(
                            monitorService.getSystemList());
                    result.put("datas", dataList);
                    break;

                case "getTpmOfServices":
                    transaction = MonitorLogger.beginTransactionType("HIP050103");

                    //21==当日
                    List<Map> tph = overViewQueryService.indexProect_queryTrendChartData(21);
                    result.put("datas", tph);
                    break;

                case "getHardwareIndexes":
                    transaction = MonitorLogger.beginTransactionType("HIP050104");

                    // TODO: 2018/2/5 演示使用的假数据，真实接口待开发
                    result.put("CPU", "43");
                    result.put("Memory", "85");
                    result.put("Disk", "12");
                    break;

                case "getCommunicationByAid":
                    transaction = MonitorLogger.beginTransactionType("HIP050105");

                    Map<String, Object> map = new HashMap<>();
                    //时间类型为当天
                    map.put(QueryParameterKeys.TIMETYPE.getKey(), "today");
                    //系统id
                    map.put(QueryParameterKeys.DOMAIN.getKey(), params.get("aid"));
                    //SERVER表示提供方，CLIENT表示消费方
                    map.put(QueryParameterKeys.SOC.getKey(), "SERVER");

                    List<ServerCountWithType> serverTypeList = transactionDataQueryService
                            .queryCommunicationStaticByServer(map);
                    result.put("asProvider", ScreenControllerUtil.
                            generateResultForCommunicationChart(serverTypeList));

                    map.replace(QueryParameterKeys.SOC.getKey(), "CLIENT");
                    List<ServerCountWithType> clientTypeList = transactionDataQueryService
                            .queryCommunicationStaticByServer(map);
                    result.put("asConsumer", ScreenControllerUtil.
                            generateResultForCommunicationChart(clientTypeList));
                    break;

                case "getServicesByIds":
                    transaction = MonitorLogger.beginTransactionType("HIP050106");

                    List<ServiceCount> serviceCountList = monitorService.getServiceList(params);
                    CommonOperation.convertServiceName(serviceCountList);
                    result.put("services",serviceCountList);
                    break;
            }
            ScreenControllerUtil.generateSuccessResponse(result);
            transaction.addData("outParams", result);
            transaction.success();
        } catch (Exception e) {
            ScreenControllerUtil.generateFailResponse(result, e);
            Objects.requireNonNull(transaction).setStatus(e);
            transaction.complete();
        }

        addManagerNumber(transaction, 殷奇隆);

        return JSON.toJSONString(result);
    }

}
