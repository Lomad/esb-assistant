package com.winning.esb.controller.monitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.winning.esb.controller.monitor.utils.CommonOperation;
import com.winning.esb.service.IAppInfoService;
import com.winning.esb.service.ISvcInfoService;
import com.winning.esb.utils.JsonUtils;
import com.winning.esb.utils.MapUtils;
import com.winning.monitor.data.api.ITransactionDataQueryService;
import com.winning.monitor.data.api.base.ServerCountWithType;
import com.winning.monitor.data.api.base.ServiceShowVO;
import com.winning.monitor.data.api.enums.QueryParameterKeys;
import com.winning.monitor.data.api.transaction.domain.*;
import com.winning.monitor.data.api.base.SumVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.*;

/**
 * Created by Admin on 2016/10/20.
 */
@Controller
@RequestMapping(value = {"/view", "/ajax"})
public class PaasController {
    private static final Logger logger = LoggerFactory.getLogger(PaasController.class);

    @Autowired
    private ITransactionDataQueryService transactionDataQuery;
    private ObjectMapper objectMapper = new ObjectMapper();
    private String GroupId = "BI";

    @Autowired
    private IAppInfoService appInfoService;
    @Autowired
    private ISvcInfoService svcInfoService;

    private enum RealTimeType {
        /**
         * 当前一小时
         */
        LASTHOUR("lasthour"),
        /**
         * 当天
         */
        TODAY("today"),
        /**
         * 指定一小时
         */
        APPOINTEDHOUR("appointHour");

        private String name;

        RealTimeType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @RequestMapping(value = {"/paas/serverrealtime"})
    public ModelAndView serverrealtime(String datas) {
        ModelAndView mv = new ModelAndView("monitor/serverrealtime");

        try {
            Map<String, Object> map = this.objectMapper.readValue(datas, Map.class);
            CommonOperation.setModeAndView(mv, map);
        } catch (Exception e) {

        }
        return mv;
    }

    @RequestMapping(value = {"/paas/basemanagement"})
    public ModelAndView basemanagement() {
        return new ModelAndView("monitor/basemanagement");
    }

    @RequestMapping(value = {"/paas/lastserviceshow"})
    public ModelAndView lastserviceshow() {
        return new ModelAndView("monitor/lastserviceshow");
    }

    @RequestMapping(value = {"/paas/serverrealtime__serversysrealtime"})
    public ModelAndView serversysrealtime(String datas) {
        Map<String, Object> map = null;
        try {
            map = this.objectMapper.readValue(datas, Map.class);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        ModelAndView mv = new ModelAndView("monitor/serversysrealtime");
        String transactionTypeId = map.get("transactionTypeId").toString();
        String serverAppId = map.get("serverAppId").toString();
        String serverIpAddress = map.get("serverIpAddress").toString();
        String type = map.get("type").toString();
        String time = map.get("time").toString();

        String clientAppId = map.get("clientAppId").toString();

        Map<String, String> appNameMap = CommonOperation.queryAppNameById(serverAppId, clientAppId);
        Map<String, String> serviceNameMap = CommonOperation.queryServiceNameById(transactionTypeId);

        mv.addObject("transactionTypeId", transactionTypeId);
        mv.addObject("transactionTypeName", serviceNameMap.get(transactionTypeId));
        mv.addObject("serverIpAddress", serverIpAddress);
        mv.addObject("serverAppName", appNameMap.get(serverAppId));
        mv.addObject("serverAppId", serverAppId);
        mv.addObject("type", type);
        mv.addObject("time", time);
        mv.addObject("clientAppName", appNameMap.get(clientAppId));
        mv.addObject("clientAppId", clientAppId);
        return mv;
    }

    @RequestMapping(value = {"/paas/serverhistory__serversyshistory"})
    public ModelAndView serversyshistory(String datas) {
        Map<String, Object> map = null;
        try {
            map = this.objectMapper.readValue(datas, Map.class);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        ModelAndView mv = new ModelAndView("monitor/serversyshistory");
        String transactionTypeId = map.get("transactionTypeId").toString();
        String serverIpAddress = map.get("serverIpAddr").toString();
        String serverAppId = map.get("serverAppId").toString();
        String clientAppId = map.get("clientAppId").toString();
        String type = map.get("type").toString();
        String value = map.get("value").toString();
        String dateValue = map.get("dateValue").toString();

        Map<String, String> appNameMap = CommonOperation.queryAppNameById(serverAppId, clientAppId);
        Map<String, String> serviceNameMap = CommonOperation.queryServiceNameById(transactionTypeId);


        mv.addObject("transactionTypeId", transactionTypeId);
        mv.addObject("serverAppId", serverAppId);
        mv.addObject("clientAppId", clientAppId);
        mv.addObject("transactionTypeName", serviceNameMap.get(transactionTypeId));
        mv.addObject("serverIpAddr", serverIpAddress);
        mv.addObject("serverAppName", appNameMap.get(serverAppId));
        mv.addObject("clientAppName", appNameMap.get(clientAppId));
        mv.addObject("type", type);
        mv.addObject("value", value);
        mv.addObject("dateValue", dateValue);
        return mv;
    }

    @RequestMapping(value = {"/paas/serverhistory__serverstephistory", "/paas/clienthistory__serverstephistory"})
    public ModelAndView serverstephistory(String datas) {
        Map<String, Object> map = null;
        try {
            map = this.objectMapper.readValue(datas, Map.class);
        } catch (IOException e) {
            //e.printStackTrace();
        }

        ModelAndView mv = new ModelAndView("monitor/serverstephistory");
        String transactionTypeId = map.get("transactionTypeId").toString();
        String serverIpAddress = map.get("serverIpAddress").toString();
        String serverAppId = map.get("serverAppId").toString();
        String type = map.get("type").toString();
        String value = map.get("value").toString();
        String historyPageType = map.get("historyPageType").toString();
        String dateValue = map.get("dateValue").toString();

        String clientAppId = map.get("clientAppId").toString();

        Map<String, String> appNameMap = CommonOperation.queryAppNameById(serverAppId, clientAppId);
        Map<String, String> serviceNameMap = CommonOperation.queryServiceNameById(transactionTypeId);

        Iterator key = map.keySet().iterator();
        while (key.hasNext()) {
            if ("clientAppName".equals(key.next())) {
                mv.addObject("clientAppId", appNameMap.get(clientAppId));
            }
        }

        mv.addObject("transactionTypeId", transactionTypeId);
        mv.addObject("serverAppId", serverAppId);
        mv.addObject("clientAppId", clientAppId);
        mv.addObject("transactionTypeName", serviceNameMap.get(transactionTypeId));
        mv.addObject("serverIpAddress", serverIpAddress);
        mv.addObject("serverAppName", appNameMap.get(serverAppId));
        mv.addObject("type", type);
        mv.addObject("value", value);
        mv.addObject("historyPageType", historyPageType);
        mv.addObject("dateValue", dateValue);
        return mv;
    }

    @RequestMapping(value = {"/paas/serverrealtime__serversteprealtime"})
    public ModelAndView serversteprealtime(String datas) {
        Map<String, Object> map = null;
        try {
            map = this.objectMapper.readValue(datas, Map.class);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        ModelAndView mv = new ModelAndView("monitor/serversteprealtime");
        String transactionTypeId = map.get("transactionTypeId").toString();
        String serverIpAddress = map.get("serverIpAddress").toString();
        String serverAppId = map.get("serverAppId").toString();
        String type = map.get("type").toString();
        String time = map.get("time").toString();
        String clientAppId = map.get("clientAppId").toString();

        Map<String, String> appNameMap = CommonOperation.queryAppNameById(serverAppId, clientAppId);
        Map<String, String> serviceNameMap = CommonOperation.queryServiceNameById(transactionTypeId);

        mv.addObject("transactionTypeId", transactionTypeId);
        mv.addObject("transactionTypeName", serviceNameMap.get(transactionTypeId));
        mv.addObject("serverIpAddress", serverIpAddress);
        mv.addObject("serverAppId", serverAppId);
        mv.addObject("serverAppName", appNameMap.get(serverAppId));
        mv.addObject("type", type);
        mv.addObject("time", time);
        mv.addObject("clientAppId", clientAppId);
        mv.addObject("clientAppName", appNameMap.get(clientAppId));
        return mv;
    }

    @RequestMapping(value = {"/paas/clientrealtime__clientsteprealtime"})
    public ModelAndView clientsteprealtime(String datas) {
        Map<String, Object> map = null;
        try {
            map = this.objectMapper.readValue(datas, Map.class);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        ModelAndView mv = new ModelAndView("monitor/clientsteprealtime");
        String transactionTypeId = map.get("transactionTypeId").toString();
        String serverIpAddress = map.get("serverIpAddress").toString();
        String serverAppId = map.get("serverAppId").toString();
        String type = map.get("type").toString();
        String time = map.get("time").toString();
        String clientAppId = map.get("clientAppId").toString();

        Map<String, String> appNameMap = CommonOperation.queryAppNameById(serverAppId, clientAppId);
        Map<String, String> serviceNameMap = CommonOperation.queryServiceNameById(transactionTypeId);

        mv.addObject("transactionTypeId", transactionTypeId);
        mv.addObject("transactionTypeName", serviceNameMap.get(transactionTypeId));
        mv.addObject("serverIpAddress", serverIpAddress);
        mv.addObject("serverAppId", serverAppId);
        mv.addObject("serverAppName", appNameMap.get(serverAppId));
        mv.addObject("type", type);
        mv.addObject("time", time);
        mv.addObject("clientAppId", clientAppId);
        mv.addObject("clientAppName", appNameMap.get(clientAppId));
        return mv;
    }

    @RequestMapping(value = {"/paas/serverhistory__serverdetailedhistory", "/paas/clienthistory__serverdetailedhistory"})
    public ModelAndView serverdetailedhistory(String datas) {
        Map<String, Object> map = null;
        try {
            map = this.objectMapper.readValue(datas, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ModelAndView mv = new ModelAndView("monitor/serverdetailedhistory");
        String transactionTypeId = map.get("transactionTypeId").toString();
        String serverIpAddress = map.get("serverIpAddress").toString();
        String serverAppId = map.get("serverAppId").toString();
        String type = map.get("type").toString();
        String time = map.get("value").toString();
        String clientAppId = map.get("clientAppId").toString();
        String clientIpAddress = map.get("clientIpAddress").toString();
        String status = map.get("status").toString();
        String dateValue = map.get("dateValue").toString();
        String historyPageType = map.get("historyPageType").toString();

        Map<String, String> appNameMap = CommonOperation.queryAppNameById(serverAppId, clientAppId);
        Map<String, String> serviceNameMap = CommonOperation.queryServiceNameById(transactionTypeId);

        mv.addObject("transactionTypeId", transactionTypeId);
        mv.addObject("serverAppId", serverAppId);
        mv.addObject("clientAppId", clientAppId);
        mv.addObject("transactionTypeName", serviceNameMap.get(transactionTypeId));
        mv.addObject("serverIpAddress", serverIpAddress);
        mv.addObject("serverAppName", appNameMap.get(serverAppId));
        mv.addObject("type", type);
        mv.addObject("value", time);
        mv.addObject("clientAppName", appNameMap.get(clientAppId));
        mv.addObject("clientIpAddress", clientIpAddress);
        mv.addObject("status", status);
        mv.addObject("dateValue", dateValue);
        mv.addObject("historyPageType", historyPageType);
        return mv;
    }

//    //xuehao 2018-03-24：弃用，待删除，已改为通过前台直接传参（无需从后台绕一圈）
//    @Deprecated
//    @RequestMapping(value = {"/paas/serverrealtime__serverdetailedrealtimeOLD"})
//    public ModelAndView serverdetailedrealtimeOLD(String datas) {
//        Map<String, Object> map = null;
//        try {
//            map = this.objectMapper.readValue(datas, Map.class);
//        } catch (IOException e) {
//            //e.printStackTrace();
//        }
//        ModelAndView mv = new ModelAndView("monitor/serverdetailedrealtime");
//        String transactionTypeId = null, serverIpAddress = null,
//                serverAppId = null, type = null, time = null,
//                clientAppId = null, clientIpAddress = null, status = null;
//        if (map.containsKey("transactionTypeId")) {
//            transactionTypeId = map.get("transactionTypeId").toString();
//        }
//        if (map.containsKey("serverIpAddress")) {
//            serverIpAddress = map.get("serverIpAddress").toString();
//        }
//        if (map.containsKey("serverAppId")) {
//            serverAppId = map.get("serverAppId").toString();
//        }
//        if (map.containsKey("type")) {
//            type = map.get("type").toString();
//        }
//        if (map.containsKey("time")) {
//            time = map.get("time").toString();
//        }
//        if (map.containsKey("clientAppId")) {
//            clientAppId = map.get("clientAppId").toString();
//        }
//        if (map.containsKey("clientIpAddress")) {
//            clientIpAddress = map.get("clientIpAddress").toString();
//        }
//        if (map.containsKey("status")) {
//            status = map.get("status").toString();
//        }
//
//        Map<String, String> appNameMap = CommonOperation.queryAppNameById(serverAppId, clientAppId);
//        Map<String, String> serviceNameMap = CommonOperation.queryServiceNameById(transactionTypeId);
//
//        mv.addObject("transactionTypeId", transactionTypeId);
//        mv.addObject("transactionTypeName", serviceNameMap.get(transactionTypeId));
//        mv.addObject("serverIpAddress", serverIpAddress);
//        mv.addObject("serverAppId", serverAppId);
//        mv.addObject("serverAppName", appNameMap.get(serverAppId));
//        mv.addObject("type", type);
//        mv.addObject("time", time);
//        mv.addObject("clientAppId", clientAppId);
//        mv.addObject("clientAppName", appNameMap.get(clientAppId));
//        mv.addObject("clientIpAddress", clientIpAddress);
//        mv.addObject("status", status);
//        return mv;
//    }

    @RequestMapping(value = {"/paas/serverrealtime__serverdetailedrealtime"})
    public ModelAndView serverdetailedrealtime() {
        return new ModelAndView("monitor/detailedRealtime");
    }

//    @RequestMapping(value = {"/paas/clientrealtime__clientdetailedrealtime"})
//    public ModelAndView clientdetailedrealtime(String datas) {
//        Map<String, Object> map = JsonUtils.jsonToMap(datas);
//        String transactionTypeId = StringUtils.isEmpty(map.get("transactionTypeId")) ? "" : map.get("transactionTypeId").toString();
//        String serverIpAddress = StringUtils.isEmpty(map.get("serverIpAddress")) ? "" : map.get("serverIpAddress").toString();
//        String serverAppId = StringUtils.isEmpty(map.get("serverAppId")) ? "" : map.get("serverAppId").toString();
//        String clientIpAddress = StringUtils.isEmpty(map.get("clientIpAddress")) ? "" : map.get("clientIpAddress").toString();
//        String clientAppId = StringUtils.isEmpty(map.get("clientAppId")) ? "" : map.get("clientAppId").toString();
//        String status = StringUtils.isEmpty(map.get("status")) ? "" : map.get("status").toString();
//        String type = StringUtils.isEmpty(map.get("type")) ? "" : map.get("type").toString();
//        String time = StringUtils.isEmpty(map.get("time")) ? "" : map.get("time").toString();
//
//        Map<String, String> appNameMap = appInfoService.mapAppIdName(Arrays.asList(serverAppId, clientAppId));
//        Map<String, String> serviceNameMap = svcInfoService.mapCodeName(Arrays.asList(transactionTypeId));
//
//        ModelAndView mv = new ModelAndView("monitor/clientdetailedrealtime");
//        mv.addObject("transactionTypeName", serviceNameMap.get(transactionTypeId));
//        mv.addObject("transactionTypeId", transactionTypeId);
//        mv.addObject("serverIpAddress", serverIpAddress);
//        mv.addObject("serverAppName", appNameMap.get(serverAppId));
//        mv.addObject("serverAppId", serverAppId);
//        mv.addObject("type", type);
//        mv.addObject("time", time);
//        mv.addObject("clientAppName", appNameMap.get(clientAppId));
//        mv.addObject("clientAppId", clientAppId);
//        mv.addObject("clientIpAddress", clientIpAddress);
//        mv.addObject("status", status);
//        return mv;
//    }

    @RequestMapping(value = {"/paas/clientrealtime__clientdetailedrealtime"})
    public ModelAndView clientdetailedrealtime() {
        return new ModelAndView("monitor/detailedRealtime");
    }

    @RequestMapping(value = {"/paas/clientrealtime"})
    public ModelAndView clientrealtime(String datas) {
        ModelAndView mv = new ModelAndView("monitor/clientrealtime");

        try {
            Map<String, Object> map = this.objectMapper.readValue(datas, Map.class);
            CommonOperation.setModeAndView(mv, map);
        } catch (Exception e) {

        }
        return mv;
    }

    @RequestMapping(value = {"/paas/serverhistory"})
    public ModelAndView serverhistory(String datas) {

        ModelAndView mv = new ModelAndView("monitor/serverhistory");

        if (datas != null) {
            try {
                Map<String, Object> map = this.objectMapper.readValue(datas, Map.class);

                String domain = map.get("domain").toString();
                String type = map.get("type").toString();
                String status = map.get("status").toString();
                String isRemote = map.get("isRemote").toString();
                mv.addObject("domain", domain);
                mv.addObject("type", type);
                mv.addObject("status", status);
                mv.addObject("isRemote", isRemote);
            } catch (Exception e) {

            }
        }

        return mv;
    }

    @RequestMapping(value = {"/paas/clienthistory"})
    public ModelAndView clienthistory(String datas) {
        ModelAndView mv = new ModelAndView("monitor/clienthistory");

        try {
            Map<String, Object> map = this.objectMapper.readValue(datas, Map.class);

            String domain = map.get("domain").toString();
            String type = map.get("type").toString();
            String status = map.get("status").toString();
            String isRemote = map.get("isRemote").toString();
            mv.addObject("domain", domain);
            mv.addObject("type", type);
            mv.addObject("status", status);
            mv.addObject("isRemote", isRemote);
        } catch (Exception e) {

        }
        return mv;
    }

    @RequestMapping(value = {"/paas/clientdetailedhistory"})
    public ModelAndView clientdetailedhistory(String datas) {
        Map<String, Object> map = null;
        try {
            map = this.objectMapper.readValue(datas, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ModelAndView mv = new ModelAndView("monitor/clientdetailedhistory");
        String transactionTypeName = map.get("transactionTypeName").toString();
        String serverIpAddress = map.get("serverIpAddress").toString();
        String serverAppName = map.get("serverAppName").toString();
        String type = map.get("type").toString();
        String time = map.get("value").toString();
        String clientAppName = map.get("clientAppName").toString();
        String clientIpAddress = map.get("clientIpAddress").toString();
        String status = map.get("status").toString();
        String historyPageType = map.get("historyPageType").toString();
        mv.addObject("transactionTypeName", transactionTypeName);
        mv.addObject("serverIpAddress", serverIpAddress);
        mv.addObject("serverAppName", serverAppName);
        mv.addObject("type", type);
        mv.addObject("value", time);
        mv.addObject("clientAppName", clientAppName);
        mv.addObject("clientIpAddress", clientIpAddress);
        mv.addObject("status", status);
        mv.addObject("historyPageType", historyPageType);
        return mv;
    }

    @RequestMapping(value = {"/paas/clientstephistory"})
    public ModelAndView clientstephistory(String datas) {
        Map<String, Object> map = null;
        try {
            map = this.objectMapper.readValue(datas, Map.class);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        ModelAndView mv = new ModelAndView("monitor/clientstephistory");
        String transactionTypeName = map.get("transactionTypeName").toString();
        String serverIpAddress = map.get("serverIpAddress").toString();
        String serverAppName = map.get("serverAppName").toString();
        String type = map.get("type").toString();
        String value = map.get("value").toString();
        String historyPageType = map.get("historyPageType").toString();
        mv.addObject("transactionTypeName", transactionTypeName);
        mv.addObject("serverIpAddress", serverIpAddress);
        mv.addObject("serverAppName", serverAppName);
        mv.addObject("type", type);
        mv.addObject("value", value);
        mv.addObject("historyPageType", historyPageType);
        return mv;
    }

    /**
     * yql-2017.6.2
     * 获取系统关系-实时
     *
     * @param flname   app name
     * @param soc      server or client
     * @param timeType currentHour,today,specifiedHour
     * @param time     specifiedHour text
     * @return list of {@link ServerCountWithType}
     */
    @RequestMapping(value = {"/paas/queryAllCommunicationPoints"})
    public
    @ResponseBody
    List<ServerCountWithType> queryAllCommunicationPoints(String flname, String soc, String timeType, String time) {
        Map<String, Object> map = new HashMap<>();
        map.put(QueryParameterKeys.TIMETYPE.getKey(), timeType);
        map.put(QueryParameterKeys.TIME.getKey(), time);
        map.put(QueryParameterKeys.DOMAIN.getKey(), flname);
        map.put(QueryParameterKeys.SOC.getKey(), soc);

        List<ServerCountWithType> typeList = transactionDataQuery.queryCommunicationStaticByServer(map);
        //Map<String, AppInfoVO> appInfoVOMap = appInfoService.getAppMap();
        List<String> unMatched = CommonOperation.convertAppNameWithServerCount(typeList);
        if (unMatched != null && unMatched.size() > 0) {
            logger.warn("服务管理平台中未找到匹配系统：" + unMatched.toString());
        }

        return typeList;
    }

    /**
     * 获取所有的应用服务系统对应的IP地址
     *
     * @param serverAppName
     * @return
     */
    @RequestMapping(value = {"/paas/getAllServerIpAddress"})
    public
    @ResponseBody
    LinkedHashSet<String> getAllServerIpAddress(String serverAppName) {
        LinkedHashSet<String> set = transactionDataQuery.getAllServerIpAddress(GroupId, serverAppName);
        return set;
    }

    /**
     * 获取最近一小时的TransactionType服务统计结果,根据服务端IP进行分组,不进行分页
     *
     * @param flname
     * @return
     */
    @RequestMapping(value = {"/paas/queryTransactionTypeList"})
    public
    @ResponseBody
    TransactionStatisticReport queryTransactionTypeList(String flname, String clientAppName) {
        TransactionStatisticReport report = transactionDataQuery.queryLastHourTransactionTypeReportByServer(GroupId, flname, clientAppName);
        try {
            CommonOperation.convertServiceNameWithStatisticReport(report, svcInfoService);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return report;
    }

    /**
     * 获取指定小时的TransactionType服务统计结果,根据服务端IP进行分组,不进行分页
     *
     * @param flname
     * @param time
     * @return
     */
    @RequestMapping(value = {"/paas/queryHourTransactionTypeReportByServer"})
    public
    @ResponseBody
    TransactionStatisticReport queryHourTransactionTypeReportByServer(String flname, String time, String clientAppName) {
        TransactionStatisticReport report = transactionDataQuery.queryHourTransactionTypeReportByServer(GroupId, flname, time, clientAppName);
        try {
            CommonOperation.convertServiceNameWithStatisticReport(report, svcInfoService);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return report;
    }


    /**
     * 获取当天的TransactionType服务统计结果,根据服务端IP进行分组,不进行分页
     *
     * @param flname
     * @return
     */
    @RequestMapping(value = {"/paas/queryTodayTransactionTypeReportByServer"})
    public
    @ResponseBody
    TransactionStatisticReport queryTodayTransactionTypeReportByServer(String flname, String status, String clientAppName) {
        TransactionStatisticReport report = transactionDataQuery.queryTodayTransactionTypeReportByServer(GroupId, flname, status, clientAppName);
        try {
            CommonOperation.convertServiceNameWithStatisticReport(report, svcInfoService);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return report;
    }


    /**
     * 获取最近一小时的TransactionType服务对应的消费者统计结果,根据客户端应用名称进行分组,不进行分页
     *
     * @param serverAppName
     * @param transactionTypeName
     * @param serverIpAddress
     * @return
     */
    @RequestMapping(value = {"/paas/queryLastHourTransactionTypeReportByClient"})
    public
    @ResponseBody
    TransactionStatisticReport queryLastHourTransactionTypeReportByClient(String serverAppName,
                                                                          String transactionTypeName,
                                                                          String serverIpAddress,
                                                                          String clientAppName) {
        TransactionStatisticReport report = transactionDataQuery.queryLastHourTransactionTypeReportByClient(GroupId, serverAppName, transactionTypeName, serverIpAddress, clientAppName);
        return report;
    }


    /**
     * 获取当天的TransactionType服务对应的消费者统计结果,根据客户端应用名称进行分组,不进行分页
     *
     * @param serverAppName
     * @param transactionTypeName
     * @param serverIpAddress
     * @return
     */
    @RequestMapping(value = {"/paas/queryTodayTransactionTypeReportByClient"})
    public
    @ResponseBody
    TransactionStatisticReport queryTodayTransactionTypeReportByClient(String serverAppName,
                                                                       String transactionTypeName,
                                                                       String serverIpAddress,
                                                                       String clientAppName) {
        TransactionStatisticReport report = transactionDataQuery.queryTodayTransactionTypeReportByClient(GroupId, serverAppName, transactionTypeName, serverIpAddress, clientAppName);
        return report;
    }

    /***
     * 获取指定小时的TransactionType服务对应的消费者统计结果,根据客户端应用名称进行分组,不进行分页
     * @param serverAppName
     * @param transactionTypeName
     * @param serverIpAddress
     * @return
     */
    @RequestMapping(value = {"/paas/queryHourTransactionTypeReportByClient"})
    public
    @ResponseBody
    TransactionStatisticReport queryHourTransactionTypeReportByClient(String serverAppName,
                                                                      String transactionTypeName,
                                                                      String serverIpAddress,
                                                                      String time,
                                                                      String clientAppName) {
        TransactionStatisticReport report = transactionDataQuery.queryHourTransactionTypeReportByClient(GroupId, serverAppName, time, transactionTypeName, serverIpAddress, clientAppName);
        return report;
    }


    /**
     * 获取最近一小时的TransactionType调用次数的结果集,不进行分页
     *
     * @param serverAppName
     * @param transactionTypeName
     * @param serverIpAddress
     * @return 调用次数结果集, 返回对象中durations的总长度为60, Key值为0-59,表示一小时从第0分钟到第59分钟的每分钟调用次数
     */
    @RequestMapping(value = {"/paas/queryLastHourTransactionTypeCallTimesReportByServer"})
    @ResponseBody
    public TransactionCallTimesReport queryLastHourTransactionTypeCallTimesReportByServer(String serverAppName,
                                                                                          String transactionTypeName,
                                                                                          String serverIpAddress,
                                                                                          String clientAppName) {
        TransactionCallTimesReport report = transactionDataQuery.queryLastHourTransactionTypeCallTimesReportByServer(GroupId, serverAppName, transactionTypeName, serverIpAddress, clientAppName);
        return report;
    }

    /**
     * 获取指定一小时的TransactionType调用次数的结果集,不进行分页
     *
     * @param serverAppName
     * @param hour
     * @param transactionTypeName
     * @param serverIpAddress
     * @return
     */
    @RequestMapping(value = {"/paas/queryHourTransactionTypeCallTimesReportByServer"})
    @ResponseBody
    public TransactionCallTimesReport queryHourTransactionTypeCallTimesReportByServer(String serverAppName,
                                                                                      String hour,
                                                                                      String transactionTypeName,
                                                                                      String serverIpAddress,
                                                                                      String clientAppName) {
        TransactionCallTimesReport report = transactionDataQuery.queryHourTransactionTypeCallTimesReportByServer(GroupId, serverAppName, hour, transactionTypeName, serverIpAddress, clientAppName);
        return report;
    }

    /**
     * 获取当天的TransactionType调用次数的结果集,不进行分页
     *
     * @param serverAppName
     * @param transactionTypeName
     * @param serverIpAddress
     * @return 调用次数结果集, 返回对象中durations的总长度为24, Key值为0-23,表示一天从0点到23点的每小时调用次数
     */
    @RequestMapping(value = {"/paas/queryTodayTransactionTypeCallTimesReportByServer"})
    @ResponseBody
    public TransactionCallTimesReport queryTodayTransactionTypeCallTimesReportByServer(String serverAppName,
                                                                                       String transactionTypeName,
                                                                                       String serverIpAddress,
                                                                                       String clientAppName) {
        TransactionCallTimesReport report = transactionDataQuery.queryTodayTransactionTypeCallTimesReportByServer(GroupId, serverAppName, transactionTypeName, serverIpAddress, clientAppName);
        return report;
    }

    /**
     * 获取最近一小时的TransactionName服务步骤统计结果不进行分页
     *
     * @param serverAppName
     * @param transactionTypeName
     * @param serverIpAddress
     * @return
     */
    @RequestMapping(value = {"/paas/queryLastHourTransactionNameReportByServer"})
    @ResponseBody
    public TransactionStatisticReport queryLastHourTransactionNameReportByServer(String serverAppName,
                                                                                 String transactionTypeName,
                                                                                 String serverIpAddress, String clientAppName) {
        TransactionStatisticReport report = transactionDataQuery.queryLastHourTransactionNameReportByServer(GroupId, serverAppName, transactionTypeName, serverIpAddress, clientAppName);
        return report;
    }

    /**
     * 获取当天的TransactionName服务步骤统计结果不进行分页
     *
     * @param serverAppName
     * @param transactionTypeName
     * @param serverIpAddress
     * @return
     */
    @RequestMapping(value = {"/paas/queryTodayTransactionNameReportByServer"})
    public
    @ResponseBody
    TransactionStatisticReport queryTodayTransactionNameReportByServer(String serverAppName,
                                                                       String transactionTypeName,
                                                                       String serverIpAddress, String clientAppName) {
        TransactionStatisticReport report = transactionDataQuery.queryTodayTransactionNameReportByServer(GroupId, serverAppName, transactionTypeName, serverIpAddress, clientAppName);
        return report;
    }


    /**
     * 获取指定小时的TransactionName服务步骤统计结果不进行分页
     *
     * @param serverAppName
     * @param transactionTypeName
     * @param time
     * @param serverIpAddress
     * @return 统计数据结果集
     */
    @RequestMapping(value = {"/paas/queryHourTransactionNameReportByServer"})
    public
    @ResponseBody
    TransactionStatisticReport queryHourTransactionNameReportByServer(String serverAppName,
                                                                      String transactionTypeName,
                                                                      String serverIpAddress,
                                                                      String time, String clientAppName) {
//        System.out.println(serverAppName+"--"+time+"--"+transactionTypeName+"--"+serverIpAddress);
//        System.out.println("------------------------------------");
        TransactionStatisticReport report = transactionDataQuery.queryHourTransactionNameReportByServer(GroupId, serverAppName, time, transactionTypeName, serverIpAddress, clientAppName);
        return report;
    }

    /**
     * 获取最近一小时内的调用消息明细记录
     *
     * @return
     */
    @RequestMapping(value = {"/paas/queryLastHourTransactionMessageList"})
    public
    @ResponseBody
    TransactionMessageList queryLastHourTransactionMessageList(String datas) {
        Map<String, Object> map = null;
        try {
            map = this.objectMapper.readValue(datas, Map.class);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        String serverAppName = map.get("serverAppName").toString();
        String transactionTypeName = map.get("transactionTypeName").toString();
        String transactionName = "";//map.get("transactionName").toString();
        String serverIpAddress = map.get("serverIpAddress").toString();
        String clientAppName = map.get("clientAppName").toString();
        String clientIpAddress = map.get("clientIpAddress").toString();
        String status = map.get("status").toString();

        String keyWords = null;
        if (!map.containsKey("keyWords")) {
            map.put("keyWords", null);
        } else {
            keyWords = map.get("keyWords").toString();
        }
        String limitStartTime = (String) map.getOrDefault("limitStartTime", "");
        String limitEndTime = (String) map.getOrDefault("limitEndTime", "");

        String durationTop = String.valueOf(map.getOrDefault("durationTop",""));
        int startIndex = Integer.parseInt(map.get("start").toString());
        int pageSize = Integer.parseInt(map.get("pageSize").toString());

        //消费方明细 关键字查询：提供或消费方IP
        String inputIP = (String) map.getOrDefault("inputIP","");

        Map<String, Object> previousIndexes = CommonOperation.getPreviousIndexes(map);

        TransactionMessageList report = transactionDataQuery.
                queryLastHourTransactionMessageList(GroupId, serverAppName,
                        transactionTypeName, transactionName, serverIpAddress,
                        clientAppName, clientIpAddress, status,
                        keyWords, limitStartTime, limitEndTime,
                        startIndex, pageSize, durationTop, previousIndexes);
        return report;
    }

    /**
     * 获取当天内的调用消息明细记录
     *  xuehao 2018-03-25：完善该函数，支持当前小时、指定小时、当天查询
     */
    @RequestMapping(value = {"/paas/queryTodayTransactionMessageList"})
    @ResponseBody
    public TransactionMessageList queryTodayTransactionMessageList(String datas) {
        Map<String, Object> map = JsonUtils.jsonToMap(datas);
        String serverAppName = (String) map.getOrDefault("serverAppName", "");
        String transactionTypeName = (String) map.getOrDefault("transactionTypeName", "");
        String transactionName = "";
        String serverIpAddress = (String) map.getOrDefault("serverIpAddress", "");
        String clientAppName = (String) map.getOrDefault("clientAppName", "");
        String clientIpAddress = (String) map.getOrDefault("clientIpAddress", "");
        String status = (String) map.getOrDefault("status", "");
        String keyWords = (String) map.getOrDefault("keyWords", "");
        String limitStartTime = (String) map.getOrDefault("limitStartTime", "");
        String limitEndTime = (String) map.getOrDefault("limitEndTime", "");
        int startIndex = Integer.parseInt(map.get("start").toString());
        int pageSize = Integer.parseInt(map.get("pageSize").toString());

        //消费方明细 关键字查询：提供或消费方IP
        String inputIP = (String) map.getOrDefault("inputIP","");
        String durationTop = String.valueOf(map.getOrDefault("durationTop",""));

        Map<String, Object> previousIndexes = CommonOperation.getPreviousIndexes(map);
        TransactionMessageList report = transactionDataQuery.queryTodayTransactionMessageList(GroupId, serverAppName,
                transactionTypeName, transactionName, serverIpAddress,
                clientAppName, clientIpAddress, status,
                keyWords, inputIP, limitStartTime, limitEndTime,
                startIndex, pageSize, durationTop, previousIndexes);
        return report;
    }

    /**
     * 获取指定小时内的调用消息明细记录
     *
     * @param datas
     * @return
     */
    @RequestMapping(value = {"/paas/queryHourTransactionMessageList"})
    public
    @ResponseBody
    TransactionMessageList queryHourTransactionMessageList(String datas) {
        Map<String, Object> map = null;
        try {
            map = this.objectMapper.readValue(datas, Map.class);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        String serverAppName = map.get("serverAppName").toString();
        String transactionTypeName = map.get("transactionTypeName").toString();
        String transactionName = "";//map.get("transactionName").toString();
        String serverIpAddress = map.get("serverIpAddress").toString();
        String clientAppName = map.get("clientAppName").toString();
        String clientIpAddress = map.get("clientIpAddress").toString();
        String status = map.get("status").toString();
        String time = map.get("time").toString();
        String keyWords = null;
        if (!map.containsKey("keyWords")) {
            map.put("keyWords", null);
        } else {
            keyWords = map.get("keyWords").toString();
        }
        String durationTop = String.valueOf(map.getOrDefault("durationTop",""));
        int startIndex = Integer.parseInt(map.get("start").toString());
        int pageSize = Integer.parseInt(map.get("pageSize").toString());
//        String[] linkkey={"transactionTypeName","serverIpAddress","clientAppName","clientIpAddress","duration","status","time"};

        Map<String, Object> previousIndexes = CommonOperation.getPreviousIndexes(map);


        TransactionMessageList report = transactionDataQuery.
                queryHourTransactionMessageList(GroupId, serverAppName, time,
                        transactionTypeName, transactionName, serverIpAddress,
                        clientAppName, clientIpAddress, status, keyWords,
                        startIndex, pageSize, durationTop, previousIndexes);
        return report;
    }

    //day

    /**
     * 获取指定日期的TransactionType服务统计结果,根据服务端IP进行分组,不进行分页
     *
     * @param flname
     * @param date   指定日期,格式为 yyyy-MM-dd
     * @return
     */
    @RequestMapping(value = {"/paas/queryDayTransactionTypeReportByServer"})
    @ResponseBody
    public TransactionStatisticReport queryDayTransactionTypeReportByServer(String flname, String clientAppName, String date, String status) {
        TransactionStatisticReport report = transactionDataQuery.queryDayTransactionTypeReportByServer(GroupId, flname, date, status, clientAppName);
        CommonOperation.convertServiceNameWithStatisticReport(report, svcInfoService);
        return report;
    }

    /**
     * 获取指定日期的TransactionType服务对应的消费者统计结果,根据客户端应用名称进行分组,不进行分页
     *
     * @param flname
     * @param date
     * @param transactionTypeName
     * @param serverIpAddress
     * @return
     */
    @RequestMapping(value = {"/paas/queryDayTransactionTypeReportByClient"})
    @ResponseBody
    public TransactionStatisticReport queryDayTransactionTypeReportByClient(String flname, String clientAppName, String date, String transactionTypeName, String serverIpAddress) {
        TransactionStatisticReport report = transactionDataQuery.queryDayTransactionTypeReportByClient(GroupId, flname, date, transactionTypeName, serverIpAddress, clientAppName);
        return report;
    }

    /**
     * 获取指定日期的TransactionType调用次数的结果集,不进行分页
     *
     * @param flname
     * @param date
     * @param transactionTypeName
     * @param serverIpAddress
     * @return  返回Map，共有两组值，key与value分别为：hour - 小时数组，times - 次数数组
     */
    @RequestMapping(value = {"/paas/queryDayTransactionTypeCallTimesReportByServer"})
    @ResponseBody
    public TransactionCallTimesReport queryDayTransactionTypeCallTimesReportByServer(String flname, String date, String transactionTypeName, String serverIpAddress) {
        TransactionCallTimesReport report = transactionDataQuery.queryDayTransactionTypeCallTimesReportByServer(GroupId, flname, date, transactionTypeName, serverIpAddress);
        return report;
    }

    /**
     * 获取指定天的TransactionName服务步骤统计结果不进行分页
     *
     * @param flname
     * @param date
     * @param transactionTypeName
     * @param serverIpAddress
     * @return
     */
    @RequestMapping(value = {"/paas/queryDayTransactionNameReportByServer"})
    @ResponseBody
    public TransactionStatisticReport queryDayTransactionNameReportByServer(String flname, String date, String transactionTypeName, String serverIpAddress, String clientAppName) {
        TransactionStatisticReport report = transactionDataQuery.queryDayTransactionNameReportByServer(GroupId, flname, date, transactionTypeName, serverIpAddress, clientAppName);
        return report;
    }

    /**
     * 获取指定日期内的调用消息明细记录
     *
     * @param datas
     * @return
     */
    @RequestMapping(value = {"/paas/queryDayTransactionMessageList"})
    @ResponseBody
    public TransactionMessageList queryDayTransactionMessageList(String datas) {
        Map<String, Object> map = null;
        try {
            map = this.objectMapper.readValue(datas, Map.class);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        String serverAppName = map.get("serverAppName").toString();
        String transactionTypeName = map.get("transactionTypeName").toString();
        String transactionName = "";//map.get("transactionName").toString();
        String serverIpAddress = map.get("serverIpAddress").toString();
        String clientAppName = map.get("clientAppName").toString().toString();
        String clientIpAddress = map.get("clientIpAddress").toString();
        String status = map.get("status").toString();
        String date = map.get("date").toString();
        String keyWords = null;
        if (!map.containsKey("keyWords")) {
            map.put("keyWords", null);
        } else {
            keyWords = map.get("keyWords").toString();
        }
        int startIndex = (int) map.get("start");
        int pageSize = (int) map.get("pageSize");

        Map<String, Object> previousIndexes;
        if (StringUtils.isEmpty(map.get("previousPageTS"))) {
            previousIndexes = null;
        } else {
            previousIndexes = new HashMap<>();
            previousIndexes.putAll((Map<? extends String, ?>) map.get("previousPageTS"));
            previousIndexes.put("previousPage", map.get("previousPage"));
        }

        TransactionMessageList report = transactionDataQuery.queryDayTransactionMessageList(GroupId, serverAppName, date, transactionTypeName, transactionName, serverIpAddress, clientAppName, clientIpAddress, status, keyWords, startIndex, pageSize, previousIndexes);
        return report;
    }
    //week

    /**
     * 获取指定周的TransactionType服务统计结果,根据服务端IP进行分组,不进行分页
     *
     * @param flname
     * @param date   指定周的第一天日期,格式为 yyyy-MM-dd
     * @return
     */
    @RequestMapping(value = {"/paas/queryWeekTransactionTypeReportByServer"})
    @ResponseBody
    public TransactionStatisticReport queryWeekTransactionTypeReportByServer(String flname, String clientAppName, String date, String status) {
//        System.out.println("week"+flname+"---"+date);
        TransactionStatisticReport report = transactionDataQuery.queryWeekTransactionTypeReportByServer(GroupId, flname, date, status, clientAppName);
        CommonOperation.convertServiceNameWithStatisticReport(report, svcInfoService);
        return report;
    }

    /**
     * 获取指定周的TransactionType服务对应的消费者统计结果,根据客户端应用名称进行分组,不进行分页
     *
     * @param flname
     * @param date
     * @param transactionTypeName
     * @param serverIpAddress
     * @return
     */
    @RequestMapping(value = {"/paas/queryWeekTransactionTypeReportByClient"})
    @ResponseBody
    public TransactionStatisticReport queryWeekTransactionTypeReportByClient(String flname, String clientAppName, String date, String transactionTypeName, String serverIpAddress) {
        TransactionStatisticReport report = transactionDataQuery.queryWeekTransactionTypeReportByClient(GroupId, flname, date, transactionTypeName, serverIpAddress, clientAppName);
        return report;
    }

    /**
     * 获取指定周的TransactionType调用次数的结果集,不进行分页
     *
     * @param flname
     * @param date
     * @param transactionTypeName
     * @param serverIpAddress
     * @return
     */
    @RequestMapping(value = {"/paas/queryWeekTransactionTypeCallTimesReportByServer"})
    @ResponseBody
    public TransactionCallTimesReport queryWeekTransactionTypeCallTimesReportByServer(String flname, String date, String transactionTypeName, String serverIpAddress) {
        TransactionCallTimesReport report = transactionDataQuery.queryWeekTransactionTypeCallTimesReportByServer(GroupId, flname, date, transactionTypeName, serverIpAddress);
        return report;
    }

    /**
     * 获取指定周的TransactionName服务步骤统计结果不进行分页
     *
     * @param flname
     * @param date
     * @param transactionTypeName
     * @param serverIpAddress
     * @return
     */
    @RequestMapping(value = {"/paas/queryWeekTransactionNameReportByServer"})
    @ResponseBody
    public TransactionStatisticReport queryWeekTransactionNameReportByServer(String flname, String date, String transactionTypeName, String serverIpAddress, String clientAppName) {
        TransactionStatisticReport report = transactionDataQuery.queryWeekTransactionNameReportByServer(GroupId, flname, date, transactionTypeName, serverIpAddress, clientAppName);
        return report;
    }

    /**
     * 获取指定周内的调用消息明细记录
     *
     * @param datas
     * @return
     */
    @RequestMapping(value = {"/paas/queryWeekTransactionMessageList"})
    @ResponseBody
    public TransactionMessageList queryWeekTransactionMessageList(String datas) {
        Map<String, Object> map = null;
        try {
            map = this.objectMapper.readValue(datas, Map.class);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        String serverAppName = map.get("serverAppName").toString();
        String transactionTypeName = map.get("transactionTypeName").toString();
        String transactionName = "";//map.get("transactionName").toString();
        String serverIpAddress = map.get("serverIpAddress").toString();
        String clientAppName = map.get("clientAppName").toString();
        String clientIpAddress = map.get("clientIpAddress").toString();
        String status = map.get("status").toString();
        String date = map.get("date").toString();
        String keyWords = null;
        if (!map.containsKey("keyWords")) {
            map.put("keyWords", null);
        } else {
            keyWords = map.get("keyWords").toString();
        }
        int startIndex = (int) map.get("start");
        int pageSize = (int) map.get("pageSize");

        Map<String, Object> previousIndexes;
        if (StringUtils.isEmpty(map.get("previousPageTS"))) {
            previousIndexes = null;
        } else {
            previousIndexes = new HashMap<>();
            previousIndexes.putAll((Map<? extends String, ?>) map.get("previousPageTS"));
            previousIndexes.put("previousPage", map.get("previousPage"));
        }
        TransactionMessageList report = transactionDataQuery.queryWeekTransactionMessageList(GroupId, serverAppName, date, transactionTypeName, transactionName, serverIpAddress, clientAppName, clientIpAddress, status, keyWords, startIndex, pageSize, previousIndexes);
        return report;
    }

    //month

    /**
     * 获取指定月的TransactionType服务统计结果,根据服务端IP进行分组,不进行分页
     *
     * @param flname
     * @param date   指定月份的第一条日期,格式为 yyyy-MM-dd
     * @return
     */
    @RequestMapping(value = {"/paas/queryMonthTransactionTypeReportByServer"})
    public
    @ResponseBody
    TransactionStatisticReport queryMonthTransactionTypeReportByServer(String flname, String clientAppName, String date) {
        TransactionStatisticReport report = transactionDataQuery.queryMonthTransactionTypeReportByServer(GroupId, flname, date, clientAppName);
        CommonOperation.convertServiceNameWithStatisticReport(report, svcInfoService);
        return report;
    }

    /**
     * 获取指定月的TransactionType服务对应的消费者统计结果,根据客户端应用名称进行分组,不进行分页
     *
     * @param flname
     * @param date
     * @param transactionTypeName
     * @param serverIpAddress
     * @return
     */
    @RequestMapping(value = {"/paas/queryMonthTransactionTypeReportByClient"})
    @ResponseBody
    public TransactionStatisticReport queryMonthTransactionTypeReportByClient(String flname, String clientAppName, String date, String transactionTypeName, String serverIpAddress) {
        TransactionStatisticReport report = transactionDataQuery.queryMonthTransactionTypeReportByClient(GroupId, flname, date, transactionTypeName, serverIpAddress, clientAppName);
        return report;
    }

    /**
     * 获取指定月的TransactionType调用次数的结果集,不进行分页
     *
     * @param flname
     * @param date
     * @param transactionTypeName
     * @param serverIpAddress
     * @return
     */
    @RequestMapping(value = {"/paas/queryMonthTransactionTypeCallTimesReportByServer"})
    @ResponseBody
    public TransactionCallTimesReport queryMonthTransactionTypeCallTimesReportByServer(String flname, String date, String transactionTypeName, String serverIpAddress) {
        TransactionCallTimesReport report = transactionDataQuery.queryMonthTransactionTypeCallTimesReportByServer(GroupId, flname, date, transactionTypeName, serverIpAddress);
        return report;
    }

    /**
     * 获取指定月的TransactionName服务步骤统计结果不进行分页
     *
     * @param flname
     * @param date
     * @param transactionTypeName
     * @param serverIpAddress
     * @return
     */
    @RequestMapping(value = {"/paas/queryMonthTransactionNameReportByServer"})
    @ResponseBody
    public TransactionStatisticReport queryMonthTransactionNameReportByServer(String flname, String date, String transactionTypeName, String serverIpAddress, String clientAppName) {
        TransactionStatisticReport report = transactionDataQuery.queryMonthTransactionNameReportByServer(GroupId, flname, date, transactionTypeName, serverIpAddress, clientAppName);
        return report;
    }

    /**
     * 获取指定月内的调用消息明细记录
     *
     * @param datas
     * @return
     */
    @RequestMapping(value = {"/paas/queryMonthTransactionMessageList"})
    @ResponseBody
    public TransactionMessageList queryMonthTransactionMessageList(String datas) {
        Map<String, Object> map = null;
        try {
            map = this.objectMapper.readValue(datas, Map.class);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        String serverAppName = map.get("serverAppName").toString();
        String transactionTypeName = map.get("transactionTypeName").toString();
        String transactionName = "";//map.get("transactionName").toString();
        String serverIpAddress = map.get("serverIpAddress").toString();
        String clientAppName = map.get("clientAppName").toString();
        String clientIpAddress = map.get("clientIpAddress").toString();
        String status = map.get("status").toString();
        String date = map.get("date").toString();
        String keyWords = null;
        if (!map.containsKey("keyWords")) {
            map.put("keyWords", null);
        } else {
            keyWords = map.get("keyWords").toString();
        }
        int startIndex = (int) map.get("start");
        int pageSize = (int) map.get("pageSize");

        Map<String, Object> previousIndexes = CommonOperation.getPreviousIndexes(map);

        TransactionMessageList report = transactionDataQuery.queryMonthTransactionMessageList(GroupId, serverAppName, date, transactionTypeName, transactionName, serverIpAddress, clientAppName, clientIpAddress, status, keyWords, startIndex, pageSize, previousIndexes);
        return report;
    }

    /**
     * 获取指定调用记录的参数明细
     *
     * @param messageId
     * @param serverAppName
     * @return Detail:
     */
    @RequestMapping(value = {"/paas/queryTransactionMessageListDetail"})
    @ResponseBody
    public TransactionMessageListDetail queryTransactionMessageListDetail(String messageId, int index, String serverAppName) {
        TransactionMessageListDetail detail = transactionDataQuery.queryTransactionMessageListDetails(GroupId, messageId, index, serverAppName);
        return detail;
    }

    @RequestMapping(value = {"/paas/getServiceFlowShow"})
    @ResponseBody
    public List<ServiceShowVO> getServiceFlowShow(String serverAppName, String messageId) {
        return this.transactionDataQuery.getServiceFlowShow(serverAppName, messageId);
    }

    /**
     * 首页：服务异常次数Top10
     *
     * @return
     */
    @RequestMapping(value = {"/paas/countAllServiceSizeByTop"})
    @ResponseBody
    public List<SumVO> countAllServiceSizeByTop(int top, String startTime, String endTime, Map map) {
        map.put("top", top);
        return transactionDataQuery.countAllServiceSizeByTop(startTime, endTime, map);
    }

    /**
     * 通用的获取调用消息的明细记录，支持当前小时、指定小时、当天、历史等
     * xuehao 2018-03-25：新增
     */
    @RequestMapping(value = {"/paas/queryCommonTransactionMessageList"})
    @ResponseBody
    public TransactionMessageList queryCommonTransactionMessageList(String datas) {
        Map<String, Object> map = JsonUtils.jsonToMap(datas);
        TransactionMessageList report = transactionDataQuery.queryCommonTransactionMessageList(GroupId, map);
        return report;
    }

}