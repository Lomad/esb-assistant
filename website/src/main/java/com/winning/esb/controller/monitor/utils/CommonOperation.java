package com.winning.esb.controller.monitor.utils;

import com.winning.esb.service.IAppInfoService;
import com.winning.esb.service.ISvcInfoService;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.MapUtils;
import com.winning.monitor.data.api.base.ServiceDurationStatisticVO;
import com.winning.monitor.data.api.largerScreen.entity.ServiceCount;
import com.winning.monitor.data.api.base.ServerCountWithType;
import com.winning.monitor.data.api.transaction.domain.TransactionStatisticData;
import com.winning.monitor.data.api.transaction.domain.TransactionStatisticReport;
import com.winning.monitor.utils.ApplicationContextUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

/**
 * Created by xuehao on 2017/8/1.
 */
public class CommonOperation {

    private static final ISvcInfoService SVC_INFO_SERVICE =
            ApplicationContextUtils.getApplicationContext().getBean(ISvcInfoService.class);

    public static final IAppInfoService APP_INFO_SERVICE =
            ApplicationContextUtils.getApplicationContext().getBean(IAppInfoService.class);

    public static void setModeAndView(ModelAndView mv, Map map) {
        try {
            String domain = map.get("domain").toString();
            String type = map.get("type").toString();
            String status = null, isRemote = null;
            if (map.containsKey("status")) {
                status = map.get("status").toString();
            }
            if (map.containsKey("isRemote")) {
                isRemote = map.get("isRemote").toString();
            }
            String downDomain = (String) map.get("downDomain");
            mv.addObject("domain", domain);
            mv.addObject("type", type);
            mv.addObject("downDomain", downDomain);
            mv.addObject("status", status);
            mv.addObject("isRemote", isRemote);
        } catch (Exception e) {

        }
    }

    /**
     * 实现管理平台系统名称的转换1
     *
     * @param countWithTypes 服务监控 系统统计对象
     * @return 在管理平台中尚未注册的系统
     */
    public static List<String> convertAppNameWithServerCount(List<ServerCountWithType> countWithTypes) {
        List<String> appIdList = new ArrayList<>();
        if (!ListUtils.isEmpty(countWithTypes)) {
            for (ServerCountWithType obj : countWithTypes) {
                appIdList.add(obj.getDomain());
            }
        }
        Map<String, String> appMap = APP_INFO_SERVICE.mapAppIdName(appIdList);
        return convertAppName(countWithTypes, appMap);
    }

    private static List<String> convertAppName(List<ServerCountWithType> countWithTypes,
                                               Map<String, String> appMap) {
        List<String> unChanged = new ArrayList<>();

        for (ServerCountWithType countWithType : countWithTypes) {
            String type = countWithType.getType();
            if (!MapUtils.isEmpty(appMap) && appMap.containsKey(type)) {
                countWithType.setType(appMap.get(type));
            } else {
                unChanged.add(type);
            }
        }
        return unChanged;
    }

    /**
     * 对接管理平台 实现系统名称转换 Target：{@link TransactionStatisticData}
     */
    public static void convertAppNameWithReport(TransactionStatisticReport report,
                                                IAppInfoService appInfoService) {
        //服务管理平台 系统对象
        Map<String, String> appMap = appInfoService.mapAppIdName();

        List<TransactionStatisticData> dataList = report.getTransactionStatisticDatas();
        TransactionStatisticData example = dataList.get(0);
        String uniqueAppId = example.getServerAppName();
        //获取系统名称
        String appName = MapUtils.isEmpty(appMap) ? uniqueAppId : appMap.get(uniqueAppId);
        if (StringUtils.isEmpty(appName)) {
            appName = uniqueAppId;
        }
        //设置系统名称
        for (TransactionStatisticData data : dataList) {
            data.setServerAppName(appName);
        }
    }

    /**
     * 根据系统id到管理平台查询其对应的系统名称
     */
    public static Map<String, String> queryAppNameById(String... appIds) {
        //服务管理平台 系统对象
        Map<String, String> appMap = APP_INFO_SERVICE.mapAppIdName(appIds);
        return appMap;
    }

    /**
     * 对接管理平台，实现服务监控内部服务名称的转换
     *
     * @param report 服务监控 服务调用指标统计对象
     */
    public static void convertServiceNameWithStatisticReport(TransactionStatisticReport report,
                                                             ISvcInfoService svcInfoService) {
        List<TransactionStatisticData> dataList = report.getTransactionStatisticDatas();

        String code;
        //获取服务代码列表
        List<String> codeList = new ArrayList<>();
        for (TransactionStatisticData data : dataList) {
            codeList.add(data.getTransactionTypeName());
        }
        //根据服务代码列表查询code与name的对应
        Map<String, String> mapCodeName = svcInfoService.mapCodeName(codeList);
        //设置服务名称
        if (!MapUtils.isEmpty(mapCodeName)) {
            for (TransactionStatisticData data : dataList) {
                code = data.getTransactionTypeName();
                data.setTransactionTypeName(mapCodeName.containsKey(code) ? mapCodeName.get(code) : code);
            }
        }
    }

    /**
     * 用于映射服务id和服务名称的公共方法
     * 目前实现了两种对象的转换：
     *  {@link ServiceCount}
     *  {@link ServiceDurationStatisticVO}
     *
     * @param list 任何包含服务id的对象
     * @param <T> 源数据对象
     */
    public static <T> void convertServiceName(List<T> list) {
        List<String> serviceIds = new ArrayList<>();
        for (T each : list) {
            if (each instanceof ServiceCount) {
                serviceIds.add(((ServiceCount) each).getServiceName());
            }
            if (each instanceof ServiceDurationStatisticVO) {
                serviceIds.add(((ServiceDurationStatisticVO) each).getServiceId());
            }
        }
        Map<String, String> mapOfService =
                queryServiceNameById(serviceIds.toArray(new String[serviceIds.size()]));

        for (T each : list) {
            if (each instanceof ServiceCount) {
                String serviceId = ((ServiceCount) each).getServiceName();
                ((ServiceCount) each).setServiceName(mapOfService.get(serviceId));
            }
            if (each instanceof ServiceDurationStatisticVO) {
                String serviceId = ((ServiceDurationStatisticVO) each).getServiceId();
                ((ServiceDurationStatisticVO) each).setServiceName(mapOfService.get(serviceId));
            }
        }
    }

    public static Map<String, String> queryServiceNameById(String... serviceIds) {
        List<String> codeList = ListUtils.transferToList(serviceIds);
        //根据服务代码列表查询code与name的对应
        return SVC_INFO_SERVICE.mapCodeName(codeList);
    }

    /**
     * 转换统计报表中的系统名和服务名
     *
     * @param report {@code TransactionStatisticReport}
     */
    public static void convertAppNameAndServiceName(TransactionStatisticReport report,
                                                    IAppInfoService appInfoService,
                                                    ISvcInfoService svcInfoService) {
        if (report.getTotalSize() > 0) {
            convertAppNameWithReport(report, appInfoService);
            convertServiceNameWithStatisticReport(report, svcInfoService);
        }
    }

    public static Map<String, Object> getPreviousIndexes(Map<String, Object> map) {
        Map<String, Object> previousIndexes;
        if (StringUtils.isEmpty(map.get("previousPageTS"))) {
            previousIndexes = null;
        } else {
            previousIndexes = new HashMap<>();
            previousIndexes.putAll((Map<? extends String, ?>) map.get("previousPageTS"));
            previousIndexes.put("previousPage", map.get("previousPage"));
        }
        return previousIndexes;
    }
}