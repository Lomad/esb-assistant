package com.winning.esb.controller.monitor.restful;

import com.alibaba.fastjson.JSON;
import com.winning.esb.controller.monitor.model.ServiceStatistic;
import com.winning.esb.controller.monitor.utils.ScreenControllerUtil;
import com.winning.esb.model.AppInfoModel;
import com.winning.esb.service.IAppInfoService;
import com.winning.esb.utils.ListUtils;
import com.winning.monitor.agent.logging.MonitorLogger;
import com.winning.monitor.agent.logging.transaction.Transaction;
import com.winning.monitor.data.api.IBaseInfoService;
import com.winning.monitor.data.api.IOverViewQueryService;
import com.winning.monitor.data.api.ITransactionDataQueryService;
import com.winning.monitor.data.api.base.ServiceStatisticVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.winning.esb.controller.monitor.utils.ScreenControllerUtil.*;
import static com.winning.esb.controller.monitor.utils.ScreenControllerUtil.MANAGER_NUMBER.殷奇隆;

/**
 * @Author Lemod
 * @Version 2017/9/22
 */
@Controller
//xuehao - 2018-03-23 : 适用于旧版大屏程序，将本类的所有代码拷贝到“ServiceShowScreenController”类，并启用下句代码，即可切换到旧版大屏程序
//@RequestMapping(value = "/api/bigscreen/monitor/")
@RequestMapping(value = "/api/bigscreen/monitorOld/")
public class ServiceShowScreenControllerOld {

    private static final Logger logger = LoggerFactory.getLogger(ServiceShowScreenController.class);

    @Autowired
    private IOverViewQueryService overViewQueryService;
    @Autowired
    private ITransactionDataQueryService transactionDataQueryService;
    @Autowired
    private IAppInfoService appInfoService;
    @Autowired
    private IBaseInfoService baseInfoService;

    @RequestMapping(value = "commonHandle", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String CommonHandle(HttpServletRequest request, @RequestParam String action, @RequestParam(required = false) String aid, @RequestParam(required = false) String sid) {
        //大屏 监控埋点
        String ip = ScreenControllerUtil.getClientIPAddress(request);
        MonitorLogger.setCaller("HIP0202", ip, "PC");
        Transaction transaction = null;

        //返回结果
        Map<String, Object> result = new HashMap<>();
        try {
            switch (action) {
                case "getOverview":
                    //大屏 监控埋点
                    transaction = MonitorLogger.beginTransactionType("HIP050101");
                    transaction.addData(INTERFACE_FUNCTION, "获取服务监控统计指标");
                    transaction.addData(INTERFACE_DATA_SOURCE, "统一监控");
                    //查询监控概览指标
                    result = getOverView();

                    transaction.addData(INTERFACE_OUTPUT, result);
                    break;
                case "listApps":
                    transaction = MonitorLogger.beginTransactionType("HIP050102");
                    transaction.addData(INTERFACE_FUNCTION, "获取监控当天所有业务系统");
                    transaction.addData(INTERFACE_DATA_SOURCE, "统一监控");
                    List<Map<String, String>> list = getSystems();
                    ScreenControllerUtil.generateSuccessResponse(result);
                    result.put("datas", list);

                    transaction.addData(INTERFACE_OUTPUT, result);
                    break;
                case "getServiceByAid":
                    transaction = MonitorLogger.beginTransactionType("HIP050103");
                    transaction.addData(INTERFACE_FUNCTION, "获取对应系统涉及的服务");
                    transaction.addData(INTERFACE_DATA_SOURCE, "统一监控");
                    if (StringUtils.hasText(aid)) {
                        result = getServiceCount(aid);

                        transaction.addData(INTERFACE_OUTPUT, result);
                    } else {
                        transaction.addData(INTERFACE_ERROR_PARAMS, "缺少系统ID");
                        transaction.setStatus("error");
                    }
                    break;
                case "getFlowBySid":
                    transaction = MonitorLogger.beginTransactionType("HIP050104");
                    transaction.addData(INTERFACE_FUNCTION, "根据服务ID获取服务调用流程");
                    transaction.addData(INTERFACE_DATA_SOURCE, "统一监控");
                    List<ServiceStatisticVO> voList = transactionDataQueryService.getServiceStatistic(sid);//procedureService.queryServiceFlow(sid);
                    if (voList != null && voList.size() > 0) {
                        generateFlow(result, voList);
                    }
                    transaction.addData(INTERFACE_ERROR_PARAMS, result);
                    break;
            }
            if ("unset".equals(transaction.getStatus())) {
                transaction.success();
                ScreenControllerUtil.generateSuccessResponse(result);
            } else {
                transaction.complete();
                ScreenControllerUtil.generateFailResponse(result, new Exception("入参有误！"));
            }
        } catch (Exception e) {
            transaction.setStatus(e);
            transaction.complete();
            ScreenControllerUtil.generateFailResponse(result, e);
        }

        addManagerNumber(transaction, 殷奇隆);

        return JSON.toJSONString(result);
    }

    private List<Map<String, String>> getSystems() {
        List<Map<String, String>> result = new ArrayList<>();
        //该方法获取的为历史所有系统
        /*List<AppInfoModel> appInfoModelList = appInfoService.listActiveWithoutEsb();*/

        //该方法获取的为当天有业务的系统
        List<String> appProvidersToday = baseInfoService.loopProviderToday();
        //获取消费方系统代码
        List<String> appConsumersToday = baseInfoService.loopConsumerToday();
        //删除重复的系统代码
        appProvidersToday.removeAll(appConsumersToday);
        //整合提供方和消费方，便于后续查询
        appProvidersToday.addAll(appConsumersToday);
        //查询在用的业务系统（如果当天所有系统都没有发生业务，则传入一个不存在的值查询即可，例如“-1”）
        Map<String, Object> queryAppMap = new HashMap<>();
        queryAppMap.put("appIdList", ListUtils.isEmpty(appProvidersToday) ? "-1" : appProvidersToday);
        List<AppInfoModel> appActiveList = appInfoService.listActive(queryAppMap);
        for (AppInfoModel obj : appActiveList) {
            Map<String, String> data = new HashMap<>();
            data.put("id", obj.getAppId());
            data.put("name", obj.getAppName());
            result.add(data);
        }
        return result;
    }

    private void generateFlow(Map result, List<ServiceStatisticVO> voList) {
        List<Map> consumers = new ArrayList<>();
        Map<String, ServiceStatisticVO> map = this.mergeSameStatistic(voList);

        //管理平台获取的系统对象
        Map<String, String> appIdNameMap = appInfoService.mapAppIdName();
        String appId, appName;
        for (ServiceStatisticVO vo : map.values()) {
            appId = vo.getConsumerId();
            //转换消费方系统名称
            if (!appIdNameMap.containsKey(appId)) {
                logger.error("服务管理平台中未找到匹配系统：" + appId);
                appName = appId;
            } else {
                appName = appIdNameMap.get(appId);
            }

            Map<String, String> consumer = new HashMap<>();
            consumer.put("id", appId);
            consumer.put("name", appName);
            consumer.put("count", String.valueOf(vo.getTotalCount()));
            consumer.put("countFail", String.valueOf(vo.getFailCount()));

            consumers.add(consumer);
        }

        //转换提供方系统名称
        ServiceStatisticVO statisticVO = voList.get(0);
        appId = statisticVO.getProviderId();
        if (!appIdNameMap.containsKey(appId)) {
            logger.error("服务管理平台中未找到匹配系统：" + appId);
            appName = appId;
        } else {
            appName = appIdNameMap.get(appId);
        }
        Map<String, String> provider = new HashMap<>();
        provider.put("id", appId);
        provider.put("name", appName);

        result.put("consumer", consumers);
        result.put("provider", provider);
    }

    private Map<String, Object> getOverView() {
        Map<String, Object> primary = overViewQueryService.countByOverView();

        Map<String, Object> result = new HashMap<>();
        result.put("countToday", primary.get("totalCount"));
        result.put("countTodayFail", primary.get("failCount"));
        result.put("countHistory", primary.get("historyTotalCount"));
        result.put("countApp", primary.get("appSize"));
        result.put("countService", primary.get("serviceSize"));
        result.put("esbRunningDays", primary.get("runDay"));

        return result;
    }

    private Map<String, Object> getServiceCount(String domain) {
        Map<String, Object> res = new HashMap<>();

//        AppInfoModel appInfo = appInfoService.getByAppId(domain);
        if (domain != null) {
            Map<String, Object> result = overViewQueryService.queryDetailsByAppInfo(domain, false);

            List<ServiceStatistic> providerList;
            List<ServiceStatistic> consumerList;

            LinkedList<Map<String, Object>> services =
                    (LinkedList<Map<String, Object>>) result.get("server");
            providerList = convertServiceName(services);
            res.put("provideService", providerList);

            LinkedList<Map<String, Object>> consumer =
                    (LinkedList<Map<String, Object>>) result.get("consumer");
            consumerList = convertServiceName(consumer);
            res.put("consumeService", consumerList);
        }
        return res;
    }

    /**
     * 转换服务name属性
     */
    private List<ServiceStatistic> convertServiceName(LinkedList<Map<String, Object>> services) {
        List<ServiceStatistic> statisticList = new ArrayList<>();

        //转换服务名称
        for (Map<String, Object> details : services) {
            ServiceStatistic statistic = new ServiceStatistic(details);
            statisticList.add(statistic);
        }
        return statisticList;
    }

    /**
     * 累加相同的消费方的服务消费次数
     *
     * @param voList
     * @return
     */
    private Map<String, ServiceStatisticVO> mergeSameStatistic(List<ServiceStatisticVO> voList) {
        Map<String, ServiceStatisticVO> consumerMap = new HashMap<>();

        for (ServiceStatisticVO vo : voList) {
            String consumerId = vo.getConsumerId();
            if (consumerMap.keySet().contains(consumerId)) {
                ServiceStatisticVO temp = consumerMap.get(consumerId);
                long totalCount = temp.getTotalCount() + vo.getTotalCount();
                long failCount = temp.getFailCount() + vo.getFailCount();

                temp.setTotalCount(totalCount);
                temp.setFailCount(failCount);
            } else {
                consumerMap.put(consumerId, vo);
            }
        }
        return consumerMap;
    }

}