package com.winning.monitor.data.transaction;

import com.winning.esb.model.AppInfoModel;
import com.winning.esb.model.ConfigsModel;
import com.winning.esb.model.SvcInfoModel;
import com.winning.esb.model.enums.ConfigsCodeConst;
import com.winning.esb.service.IAppInfoService;
import com.winning.esb.service.IConfigsService;
import com.winning.esb.service.ISvcInfoService;
import com.winning.esb.stable.DatabaseConst;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.MapUtils;
import com.winning.monitor.data.api.IBaseInfoService;
import com.winning.monitor.data.api.IOverViewQueryService;
import com.winning.monitor.data.api.base.DayCountWithServers;
import com.winning.monitor.data.api.base.ServerCountWithType;
import com.winning.monitor.data.api.base.ServiceDurationStatisticVO;
import com.winning.monitor.data.api.enums.DateType;
import com.winning.monitor.data.api.transaction.domain.TransactionMessageList;
import com.winning.monitor.data.storage.api.IOverViewStorage;
import com.winning.monitor.data.storage.api.entity.MessageTreeList;
import com.winning.monitor.data.transaction.utils.DataUtils;
import com.winning.monitor.utils.DateUtils;
import com.winning.monitor.utils.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zdj, xuehao
 * @date 17/08/20
 */
@Service
public class OverViewQueryService implements IOverViewQueryService {
    @Autowired
    private IOverViewStorage overViewStorage;
    @Autowired
    private IAppInfoService appInfoService;
    @Autowired
    private ISvcInfoService svcInfoService;
    @Autowired
    private IConfigsService configsService;
    @Autowired
    private IBaseInfoService baseInfoService;

    /**
     * 获取初始化数据，返回的Map的key如下：
     * showErrorLower - 监控概览显错下限值
     * showSysUpper - 监控概览显示的系统最大数量
     */
    @Override
    public Map<String, Object> init() {
        Map<String, Object> resultMap = new HashMap<>();

        //获取目标参数
        List<String> codeList = Arrays.asList(
                ConfigsCodeConst.MONITOR_OVERVIEW_SHOW_ERROR_LOWER,
                ConfigsCodeConst.MONITOR_OVERVIEW_SHOW_SYS_UPPER,
                ConfigsCodeConst.MONITOR_OVERVIEW_SHOW_SYS_NO_DATA
        );
        Map<String, String> configCodeValue = configsService.getValueByCode(codeList);

        //获取监控概览显错下限值
        try {
            String val = configCodeValue.get(ConfigsCodeConst.MONITOR_OVERVIEW_SHOW_ERROR_LOWER);
            int showErrorLower = !StringUtils.isEmpty(val) ? Integer.parseInt(val) : 0;
            if (showErrorLower < 0) {
                showErrorLower = 0;
            }
            resultMap.put("showErrorLower", showErrorLower);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //获取监控概览显示的系统数量最大值
        try {
            String val = configCodeValue.get(ConfigsCodeConst.MONITOR_OVERVIEW_SHOW_SYS_UPPER);
            int showSysUpper = !StringUtils.isEmpty(val) ? Integer.parseInt(val) : 0;
            resultMap.put("showSysUpper", showSysUpper);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //监控概览显示无数据的系统
        try {
            String val = configCodeValue.get(ConfigsCodeConst.MONITOR_OVERVIEW_SHOW_SYS_NO_DATA);
            int showSysNoData = !StringUtils.isEmpty(val) ? Integer.parseInt(val) : 1;
            resultMap.put("showSysNoData", showSysNoData);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return resultMap;
    }

    @Override
    public Map<String, Object> countByOverView() {
        Map<String, Object> result = new HashMap<>();
        List<AppInfoModel> appInfoList = new ArrayList<>();
        List<String> svcCodeList = new ArrayList<>();

        //获取在用的业务系统与服务，并统计当日调用数与失败数
        getDayCount(com.winning.esb.utils.DateUtils.getCurrentDateString(), appInfoList, svcCodeList, result);

        int appSize, svcSize;
        //监控概览显示无数据的系统
        int showSysNoData = configsService.getMonitorOverviewShowSysNoData();
        if (showSysNoData == ConfigsCodeConst.MONITOR_OVERVIEW_SHOW_SYS_NO_DATA_ENUM.Show.getCode()) {
            //接入系统个数
            appSize = ListUtils.isEmpty(appInfoList) ? 0 : appInfoList.size();
            //接入服务个数
            svcSize = svcCodeList.size();
        } else {
            //获取提供方系统代码
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
            appSize = ListUtils.isEmpty(appActiveList) ? 0 : appActiveList.size();
            //获取在用的服务代码
            List<String> svcCodes = baseInfoService.loopSvcToday(appProvidersToday);
            svcSize = ListUtils.isEmpty(svcCodes) ? 0 : svcCodes.size();
        }
        //接入系统个数
        result.put("appSize", appSize);
        //接入服务个数
        result.put("serviceSize", svcSize);

        //系统运行天数
        String runTime = null;
        Long runDay = null;
        ConfigsModel configsModel = configsService.getByCode(ConfigsCodeConst.StartTime);
        if (configsModel != null && !StringUtils.isEmpty(configsModel.getValue())) {
            runTime = configsModel.getValue();
            Long start = DateUtils.toDateTime(runTime);
            if (start != null) {
                Long now = DateUtils.getCurrentTime();
                runDay = DateUtils.calculateday(start, now);
            }
        }
        result.put("runTime", runTime);
        result.put("runDay", runDay);

        //历史总量
        configsModel = configsService.getByCode(ConfigsCodeConst.MonitorHistoryCallCount);
        result.put("historyTotalCount", configsModel.getValue());
        return result;
    }

    /**
     * 获取在用的业务系统与服务
     *
     * @param targetDate  目标日期，格式：2018-03-27
     * @param appInfoList 用于回传的业务系统列表
     * @param svcCodeList 用于回传的服务嗲吗列表
     */
    private List<ServerCountWithType> getDayCount(String targetDate, List<AppInfoModel> appInfoList, List<String> svcCodeList,
                                                  Map<String, Object> result) {
        //获取已启用的业务系统
        appInfoList.addAll(appInfoService.listActiveWithoutEsb());
        Map<Integer, AppInfoModel> appInfoModelMap = appInfoService.mapIdObject(appInfoList);
        List<String> appIds = appInfoService.listAppId(appInfoList);
        //获取所有服务代码
        Map<String, Object> querySvcMap = new HashMap<>();
        querySvcMap.put("aidList", appInfoModelMap.keySet());
        svcCodeList.addAll(svcInfoService.listCode(querySvcMap));

        //今日调用、今日异常
        List<ServerCountWithType> appCountList = overViewStorage.dayCountGroupBySys(targetDate, svcCodeList);
        //转换业务名称
        long totalCount = 0, failCount = 0;
        if (!ListUtils.isEmpty(appCountList)) {
            Map<String, AppInfoModel> appIdObjMap = appInfoService.mapAppIdObject(appInfoList);
            //统计调用总数和失败总数，并转换名称
            String appId;
            for (ServerCountWithType item : appCountList) {
                appId = item.getDomain();
                if (appIdObjMap.containsKey(appId) && item.getTotalCount() != null && item.getFailCount() != null) {
                    totalCount += item.getTotalCount().longValue();
                    failCount += item.getFailCount().longValue();
                    item.setType(appIdObjMap.get(appId).getAppName());
                }
            }
        }
        result.put(IOverViewStorage.TOTAL_COUNT, totalCount);
        result.put(IOverViewStorage.FAIL_COUNT, failCount);

        return appCountList;
    }

    @Override
    public List<Map> queryTrendChartData(String appId, int type) {
        Long now = DateUtils.getCurrentTime();
        Date nowDate = new Date();
        nowDate.setTime(now);
        //获取开始时间
        Date start;
        if (type == DateType.LAST24H.getKey()) {
            start = com.winning.esb.utils.DateUtils.addDate(nowDate, Calendar.HOUR, -23, true);
        } else if (type == DateType.LAST7D.getKey()) {
            start = com.winning.esb.utils.DateUtils.addDate(nowDate, Calendar.DAY_OF_MONTH, -6, true);
        } else if (type == DateType.LAST30D.getKey()) {
            start = com.winning.esb.utils.DateUtils.addDate(nowDate, Calendar.DAY_OF_MONTH, -29, true);
        } else {
            start = com.winning.esb.utils.DateUtils.addDate(nowDate, Calendar.HOUR, 0, true);
        }
        String startTime = DateUtils.toDateString(start.getTime());
        String endTime = DateUtils.toDateString(now);
        Map<String, Map<String, Object>> trendDataMap = overViewStorage.queryTrendChartData(startTime, endTime, appId, type);

        List result = new LinkedList<Map>();
        Map<String, Object> item;
        Calendar cal = Calendar.getInstance();
        String dateKey, dateKeyShort;
        if (type == DateType.LAST1H.getKey()) {
            int currentMinite = com.winning.esb.utils.DateUtils.getDate(nowDate, Calendar.MINUTE);
            for (int i = currentMinite; i >= 0; i--) {
                start = com.winning.esb.utils.DateUtils.addDate(nowDate, Calendar.MINUTE, -i, true);
                dateKey = com.winning.esb.utils.DateUtils.toDateString(start.getTime());
                item = trendDataMap.get(dateKey);
                if (item == null) {
                    item = new HashMap<>();
                    item.put(IOverViewStorage.TOTAL_COUNT, 0);
                    item.put(IOverViewStorage.FAIL_COUNT, 0);
                }
                //小时格式化，并删除小时开头的0
                dateKeyShort = dateKey.substring(14, 16);
                if (dateKeyShort.startsWith("0")) {
                    dateKeyShort = dateKeyShort.substring(1);
                }
                item.put("time", dateKeyShort);
                //完整时间
                item.put("timeFull", dateKey);
                result.add(item);
            }
        } else if (type == DateType.LAST24H.getKey()) {
            for (int i = 23; i >= 0; i--) {
                start = com.winning.esb.utils.DateUtils.addDate(nowDate, Calendar.HOUR, -i, true);
                dateKey = com.winning.esb.utils.DateUtils.toDateString(start.getTime());
                item = trendDataMap.get(dateKey);
                if (item == null) {
                    item = new HashMap<>();
                    item.put(IOverViewStorage.TOTAL_COUNT, 0);
                    item.put(IOverViewStorage.FAIL_COUNT, 0);
                }
                //小时格式化，并删除小时开头的0
                dateKeyShort = dateKey.substring(11, 16);
                if (dateKeyShort.startsWith("0")) {
                    dateKeyShort = dateKeyShort.substring(1);
                }
                item.put("time", dateKeyShort);
                //完整时间
                item.put("timeFull", dateKey);
                result.add(item);
            }
        } else if (type == DateType.LAST7D.getKey() || type == DateType.LAST30D.getKey()) {
            int dateLen = (type == DateType.LAST7D.getKey()) ? 6 : 29;
            for (int i = dateLen; i >= 0; i--) {
                start = com.winning.esb.utils.DateUtils.addDate(nowDate, Calendar.DAY_OF_MONTH, -i, true);
                dateKey = com.winning.esb.utils.DateUtils.toDateString(start.getTime());
                item = trendDataMap.get(dateKey);
                if (item == null) {
                    item = new HashMap<>();
                    item.put(IOverViewStorage.TOTAL_COUNT, 0);
                    item.put(IOverViewStorage.FAIL_COUNT, 0);
                }
                //日期格式化，并删除月份开头的0
                dateKeyShort = dateKey.substring(5, 10);
                if (dateKeyShort.startsWith("0")) {
                    dateKeyShort = dateKeyShort.substring(1);
                }
                item.put("time", dateKeyShort);
                //完整时间
                item.put("timeFull", dateKey);
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public List<Map> indexProect_queryTrendChartData(int type) {
        Long now = DateUtils.getCurrentTime();
        String startTime;
        Long start = null;
        if (type == DateType.TODAY.getKey()) {
            start = DateUtils.getStartTime(now, DateType.DAY);
        } else if (type == DateType.CURRENTWEEK.getKey()) {
            start = DateUtils.getStartTime(now, DateType.WEEK);
        } else if (type == DateType.CURRENTMONTH.getKey()) {
            start = DateUtils.getStartTime(now, DateType.MONTH);
        }
        startTime = DateUtils.toDateString(start);

        //获取已启用的业务系统
        List<AppInfoModel> appInfoList = appInfoService.listActiveWithoutEsb();
        Map<Integer, AppInfoModel> appInfoModelMap = appInfoService.mapIdObject(appInfoList);
        Map<String, Object> querySvcMap = new HashMap<>();
        querySvcMap.put("aidList", appInfoModelMap.keySet());
        List<String> svcCodeList = svcInfoService.listCode(querySvcMap);
        //统计时间与调用次数
        Map<String, Map<String, Object>> trendDataMap = overViewStorage
                .indexProject_queryTrendChartData(startTime, type, svcCodeList);

        List result = new LinkedList<Map>();
        Map<String, Object> item;
        Calendar cal = Calendar.getInstance();
        if (type == DateType.TODAY.getKey()) {
            String key = DateUtils.toDateString(now, new SimpleDateFormat("yyyy-MM-dd"));
            cal.setTimeInMillis(now);
            int nowHour = cal.get(Calendar.HOUR_OF_DAY);
            for (int i = 0; i <= nowHour; i++) {
                String hour = String.format("%02d:00", i);
                String time = key + " " + hour + ":00";
                Map trendData = trendDataMap.get(time);
                item = new HashMap<>();
                item.put("time", hour);
                if (trendData == null) {
                    item.put(IOverViewStorage.TOTAL_COUNT, 0);
                    item.put(IOverViewStorage.FAIL_COUNT, 0);
                } else {
                    item.put(IOverViewStorage.TOTAL_COUNT, trendData.get(IOverViewStorage.TOTAL_COUNT));
                    item.put(IOverViewStorage.FAIL_COUNT, trendData.get(IOverViewStorage.FAIL_COUNT));
                }
                result.add(item);
            }
        } else if (type == DateType.CURRENTWEEK.getKey()) {
            int today = DateUtils.getWeeek(now);
            cal.setTimeInMillis(start);
            for (int i = 1; i <= today; i++) {
                String time = DateUtils.toDateString(cal.getTimeInMillis(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
                String week = DateUtils.getWeekDay(i);
                Map trendData = trendDataMap.get(time);
                item = new HashMap<String, Object>();
                item.put("time", week);
                if (trendData == null) {
                    item.put(IOverViewStorage.TOTAL_COUNT, 0);
                    item.put(IOverViewStorage.FAIL_COUNT, 0);
                } else {
                    item.put(IOverViewStorage.TOTAL_COUNT, trendData.get(IOverViewStorage.TOTAL_COUNT));
                    item.put(IOverViewStorage.FAIL_COUNT, trendData.get(IOverViewStorage.FAIL_COUNT));
                }
                result.add(item);
                cal.add(Calendar.DATE, 1);
            }
        } else if (type == DateType.CURRENTMONTH.getKey()) {
            cal.setTimeInMillis(now);
            int today = cal.get(Calendar.DAY_OF_MONTH);
            cal.setTimeInMillis(start);
            for (int i = 1; i <= today; i++) {
                String time = DateUtils.toDateString(cal.getTimeInMillis(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
                Map trendData = trendDataMap.get(time);
                item = new HashMap<>();
                item.put("time", time.substring(0, 10));
                if (trendData == null) {
                    item.put(IOverViewStorage.TOTAL_COUNT, 0);
                    item.put(IOverViewStorage.FAIL_COUNT, 0);
                } else {
                    item.put(IOverViewStorage.TOTAL_COUNT, trendData.get(IOverViewStorage.TOTAL_COUNT));
                    item.put(IOverViewStorage.FAIL_COUNT, trendData.get(IOverViewStorage.FAIL_COUNT));
                }
                result.add(item);
                cal.add(Calendar.DATE, 1);
            }
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> queryClientTypeChartData(int type) {
        String startTime = null;
        Long now = DateUtils.getCurrentTime();
        String endTime = DateUtils.toDateString(now);
        if (type == DateType.TODAY.getKey()) {
            startTime = DateUtils.toDateString(DateUtils.getStartTime(now, DateType.DAY));
        } else if (type == DateType.CURRENTWEEK.getKey()) {
            startTime = DateUtils.toDateString(DateUtils.getStartTime(now, DateType.WEEK));
        } else if (type == DateType.CURRENTMONTH.getKey()) {
            startTime = DateUtils.toDateString(DateUtils.getStartTime(now, DateType.MONTH));
        }
        LinkedList<Map<String, Object>> result = overViewStorage.queryClientTypeChartData(startTime, endTime);

        Map<String, String> idNameMap;
        List<String> idList = new ArrayList<>();
        for (Map item : result) {
            String domain = (String) item.get("type");
            idList.add(domain);
        }
        idNameMap = appInfoService.mapAppIdName(idList);
        if (!MapUtils.isEmpty(idNameMap)) {
            for (Map<String, Object> item : result) {
                String domain = (String) item.get("type");
                String appName = idNameMap.get(domain);
                if (!StringUtils.isEmpty(appName)) {
                    item.replace("type", appName);
                }
            }
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> queryGroupByAppInfo(Map datas) {
        String startTime = null;
        Long now = DateUtils.getCurrentTime();
        String endTime = DateUtils.toDateString(now);
        int type = Integer.parseInt(String.valueOf(datas.get("type")));
        if (type == DateType.TODAY.getKey()) {
            startTime = DateUtils.toDateString(DateUtils.getStartTime(now, DateType.DAY));
        }

        //获取业务系统
        List<AppInfoModel> appInfoList = appInfoService.listActiveWithoutEsb(datas);
        List<Map<String, Object>> result = new ArrayList<>();
        Long serverTotalCount, serverFailCount, consumerTotalCount, consumerFailCount;
        if (!ListUtils.isEmpty(appInfoList)) {
            Map<String, Object> item;
            Map<String, Long> itemCount;
            String appId;

            //获取系统ID与对象的Map【测试】
            Map<String, AppInfoModel> appIdObjMap = appInfoService.mapAppIdObject(appInfoList);

            //获取系统ID与对象的Map
            Map<Integer, AppInfoModel> idObjMap = appInfoService.mapIdObject(appInfoList);
            //获取所有服务代码
            Map querySvcMap = new HashMap();
            querySvcMap.put("aidList", idObjMap.keySet());
            List<String> svcCodeList = svcInfoService.listCode(querySvcMap);
            //获取提供方统计
            Map<String, Map<String, Long>> serverMap = overViewStorage.queryDataByServer(svcCodeList, startTime, endTime);
            //获取消费方统计
            Map<String, Map<String, Long>> cunsumerMap = overViewStorage.queryDataByConsumer(svcCodeList, startTime, endTime);
            for (AppInfoModel appInfo : appInfoList) {
                appId = appInfo.getAppId();
                //获取提供方统计
                if (!MapUtils.isEmpty(serverMap) && serverMap.containsKey(appId)) {
                    itemCount = serverMap.get(appId);
                    serverTotalCount = MathUtils.parseLong(itemCount.get(IOverViewStorage.TOTAL_COUNT));
                    serverFailCount = MathUtils.parseLong(itemCount.get(IOverViewStorage.FAIL_COUNT));
                } else {
                    serverTotalCount = 0L;
                    serverFailCount = 0L;
                }
                //获取消费方统计
                if (!MapUtils.isEmpty(cunsumerMap) && cunsumerMap.containsKey(appId)) {
                    itemCount = cunsumerMap.get(appId);
                    consumerTotalCount = MathUtils.parseLong(itemCount.get(IOverViewStorage.TOTAL_COUNT));
                    consumerFailCount = MathUtils.parseLong(itemCount.get(IOverViewStorage.FAIL_COUNT));
                } else {
                    consumerTotalCount = 0L;
                    consumerFailCount = 0L;
                }

                //业务系统信息
                item = new HashMap<>();
                Map<String, Object> tempMap = new HashMap<>();
                tempMap.put("id", appId);
                tempMap.put("name", appInfo.getAppName());
                item.put("app", tempMap);
                //提供方统计结果
                tempMap = new HashMap<>();
                tempMap.put(IOverViewStorage.TOTAL_COUNT, serverTotalCount);
                tempMap.put(IOverViewStorage.FAIL_COUNT, serverFailCount);
                item.put("server", tempMap);
                //消费方统计结果
                tempMap = new HashMap<>();
                tempMap.put(IOverViewStorage.TOTAL_COUNT, consumerTotalCount);
                tempMap.put(IOverViewStorage.FAIL_COUNT, consumerFailCount);
                item.put("consumer", tempMap);
                //总的统计结果
                item.put("count", serverTotalCount + consumerTotalCount);
                result.add(item);
            }

            descByCount(result, "count", DatabaseConst.ORDER_DESC);  //将统计结果按照总次数降序排列
        }

        //获取监控概览显示的系统数量最大值
        int showSysUpper = configsService.getMonitorOverviewShowSysUpper();
        if (result.size() > showSysUpper) {
            result = result.subList(0, showSysUpper);
        }
        //监控概览显示无数据的系统
        int showSysNoData = configsService.getMonitorOverviewShowSysNoData();
        if (showSysNoData == ConfigsCodeConst.MONITOR_OVERVIEW_SHOW_SYS_NO_DATA_ENUM.Hide.getCode() && !ListUtils.isEmpty(result)) {
            //选择第一个（最大）检测其count是否为0，如果不大于0，则说明所有系统都没有监控数据，如果过滤，将导致界面信息为空，所以该情况下无需过滤
            long count = Long.parseLong(result.get(0).get("count").toString());
            if (count > 0) {
                List<Map<String, Object>> resultFilter = new ArrayList<>();
                for (Map<String, Object> mapItem : result) {
                    count = Long.parseLong(mapItem.get("count").toString());
                    if (count > 0) {
                        resultFilter.add(mapItem);
                    }
                }
                return resultFilter;
            }
        }

        return result;
    }

    @Override
    public Map queryDetailsByAppInfo(String appId, boolean showAllProvidedSvc) {
        Long now = DateUtils.getCurrentTime();
        String endTime = DateUtils.toDateString(now);
        String startTime = DateUtils.toDateString(DateUtils.getStartTime(now, DateType.DAY));

        Map result = new HashMap();
        List item = new LinkedList();
        List<Map<String, Object>> tempList;
        Map detail;
        String serviceCode, serviceName;
        long totalCount, failCount;
        SvcInfoModel svcInfoModel;
        List<SvcInfoModel> svcInfoModelList;
        Object appIdObj;
        //如果为空，表示ESB类型的系统
        if (StringUtils.isEmpty(appId)) {
            List<AppInfoModel> appInfoModels = appInfoService.listEsb();
            if (!ListUtils.isEmpty(appInfoModels)) {
                Map<String, AppInfoModel> appIdObjMap = appInfoService.mapAppIdObject(appInfoModels);
                //根据系统代码获取服务列表
                List<String> appIdTempList = ListUtils.transferToList(appIdObjMap.keySet());
                svcInfoModelList = this.svcInfoService.getByAppId(appIdTempList);
                if (!ListUtils.isEmpty(appIdTempList) && appIdTempList.size() == 1) {
                    appIdObj = appIdTempList.get(0);
                } else {
                    appIdObj = appIdTempList;
                }
            } else {
                svcInfoModelList = null;
                appIdObj = null;
            }
        } else {
            appIdObj = appId;
            //根据系统代码获取服务列表
            svcInfoModelList = this.svcInfoService.getByAppId(appId);
        }

        //如果没有服务，则无需统计提供方角色
        if (!StringUtils.isEmpty(appIdObj) && !ListUtils.isEmpty(svcInfoModelList)) {
            //作为提供方角色时，统计调用情况
            tempList = overViewStorage.queryDataDetailsByServer(appIdObj, startTime, endTime);
            Map<String, Map<String, Object>> svcCodeStatMap = null;
            if (!ListUtils.isEmpty(tempList)) {
                svcCodeStatMap = new HashMap<>();
                for (Map<String, Object> tempMap : tempList) {
                    svcCodeStatMap.put(tempMap.get("serviceName").toString(), tempMap);
                }
            }
            //生成最终结果
            for (int i = 0, count = svcInfoModelList.size(); i < count; i++) {
                svcInfoModel = svcInfoModelList.get(i);
                serviceCode = svcInfoModel.getCode();
                serviceName = svcInfoModel.getName();
                detail = new LinkedHashMap<String, Object>();
                if (MapUtils.isEmptyValue(svcCodeStatMap, serviceCode)) {
                    totalCount = failCount = 0L;
                } else {
                    totalCount = Long.parseLong(svcCodeStatMap.get(serviceCode).get(IOverViewStorage.TOTAL_COUNT).toString());
                    failCount = Long.parseLong(svcCodeStatMap.get(serviceCode).get(IOverViewStorage.FAIL_COUNT).toString());
                }
                //如果“showAllProvidedSvc”决定是否显示未调用的服务信息
                if (showAllProvidedSvc || totalCount > 0) {
                    detail.put("serviceCode", serviceCode);
                    detail.put("serviceName", serviceName);
                    detail.put(IOverViewStorage.TOTAL_COUNT, totalCount);
                    detail.put(IOverViewStorage.FAIL_COUNT, failCount);
                    item.add(detail);
                }
            }
        }
        descByCount(item, IOverViewStorage.FAIL_COUNT, DatabaseConst.ORDER_DESC);  //将统计结果按照总次数降序排列
        result.put("server", item);

        //作为消费方角色时，统计调用情况
        if (!StringUtils.isEmpty(appIdObj)) {
            tempList = overViewStorage.queryDataDetailsByConsumer(appIdObj, startTime, endTime);
            if (!ListUtils.isEmpty(tempList)) {
                //获取所有的调用方系统AppId
                List<String> providerAppIdList = new ArrayList<>();
                List<String> providerSvcCodeList = new ArrayList<>();
                for (Map<String, Object> map : tempList) {
                    providerAppIdList.add((String) map.get("domain"));
                    providerSvcCodeList.add((String) map.get("serviceName"));
                }
                //获取系统AppId与Name的映射Map
                Map<String, String> appIdNameMap = this.appInfoService.mapAppIdName(providerAppIdList);
                //获取服务代码与名称的映射Map
                Map<String, String> svcCodeNameMap = this.svcInfoService.mapCodeName(providerSvcCodeList);

                //重设系统(服务)的代码与名称
                String providerAppId;
                for (Map<String, Object> map : tempList) {
                    //设置系统名称
                    providerAppId = (String) map.get("domain");
                    map.put("appName", (!MapUtils.isEmpty(appIdNameMap) && appIdNameMap.containsKey(providerAppId))
                            ? appIdNameMap.get(providerAppId) : providerAppId);
                    //重置服务代码与名称
                    serviceCode = (String) map.get("serviceName");
                    serviceName = svcCodeNameMap.get(serviceCode);
                    if (!StringUtils.isEmpty(serviceName)) {
                        map.put("serviceCode", serviceCode);
                        map.put("serviceName", serviceName);
                    }
                }
            }
            descByCount(tempList, IOverViewStorage.FAIL_COUNT, DatabaseConst.ORDER_DESC);  //将统计结果按照总次数降序排列
            result.put("consumer", tempList);
        } else {
            result.put("consumer", new ArrayList());
        }

        return result;
    }

    /**
     * 将统计结果按照总次数降序排列
     *
     * @param colName   排序的字段名称
     * @param orderType 排序类型：ASC - 升序（默认）， DESC - 降序
     */
    private void descByCount(List item, String colName, String orderType) {
        if (!ListUtils.isEmpty(item)) {
            Collections.sort(item, new Comparator<Map>() {
                @Override
                public int compare(Map o1, Map o2) {
                    long value1 = MathUtils.parseLong(o1.get(colName));
                    long value2 = MathUtils.parseLong(o2.get(colName));
                    if (value1 > value2) {
                        return DatabaseConst.ORDER_DESC.equals(orderType) ? -1 : 1;
                    } else if (value1 < value2) {
                        return DatabaseConst.ORDER_DESC.equals(orderType) ? 1 : -1;
                    } else {
                        return 0;
                    }
                }
            });
        }
    }

    @Override
    public long totalHistory() {
        return overViewStorage.totalHistory();
    }

    @Override
    public List<ServerCountWithType> queryErrorConsumers() {
        String todayStartTime = DateUtils.getStartTime(DateUtils.DAY_TYPE.TODAY);
        return overViewStorage.queryErrorConsumers(todayStartTime);
    }

    @Override
    public List<ServiceDurationStatisticVO> queryServiceDuration() {
        String todayStartTime = DateUtils.getStartTime(DateUtils.DAY_TYPE.TODAY);
        List<ServiceDurationStatisticVO> durationList =
                overViewStorage.queryServiceDuration(todayStartTime);

        for (ServiceDurationStatisticVO vo : durationList) {
            Double duration = Double.parseDouble(vo.getDuration());
            BigDecimal b = new BigDecimal(duration);
            duration = b.setScale(2,
                    BigDecimal.ROUND_HALF_UP).doubleValue();

            vo.setDuration(duration.toString());
        }
        return durationList;
    }

    @Override
    public TransactionMessageList queryDetailsDuration() {
        MessageTreeList messageTreeList = overViewStorage.queryDetailsDuration();

        TransactionMessageList transactionMessageList = new TransactionMessageList();
        transactionMessageList.setTotalSize(messageTreeList.getTotalSize());
        transactionMessageList.setTransactionMessages(DataUtils.convertAppName(
                messageTreeList, appInfoService, svcInfoService));

        //单次明细耗时倒叙
        transactionMessageList.getTransactionMessages().sort((o1, o2) -> {
            double com1 = o1.getUseTime();
            double com2 = o2.getUseTime();
            return com1 >= com2 ? -1 : 1;
        });
        return transactionMessageList;
    }

    @Override
    public DayCountWithServers queryHistoryCountStatistic(String targetDate) {
        Map<String, Object> result = new HashMap<>();
        List<AppInfoModel> appInfoList = new ArrayList<>();
        List<String> svcCodeList = new ArrayList<>();

        //获取在用的业务系统与服务，并统计当日调用数与失败数
        List<ServerCountWithType> appCountList = getDayCount(targetDate, appInfoList, svcCodeList, result);

        //生成返回对象
        DayCountWithServers dayCountWithServers = new DayCountWithServers();
        dayCountWithServers.setTargetStartTime(targetDate);
        dayCountWithServers.setDailyTotalCount(Long.parseLong(result.get(IOverViewStorage.TOTAL_COUNT).toString()));
        dayCountWithServers.setDailyFailCount(Long.parseLong(result.get(IOverViewStorage.FAIL_COUNT).toString()));
        dayCountWithServers.setAppCountList(appCountList);
        return dayCountWithServers;
    }
}