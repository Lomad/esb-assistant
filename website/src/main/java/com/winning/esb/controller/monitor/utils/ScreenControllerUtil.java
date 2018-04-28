package com.winning.esb.controller.monitor.utils;

import com.winning.monitor.agent.logging.transaction.Transaction;
import com.winning.monitor.data.api.base.RunningStatusUnPTVO;
import com.winning.monitor.data.api.base.ServerCountWithType;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.winning.esb.controller.monitor.utils.ScreenControllerUtil.MANAGER_NUMBER.*;

/**
 * @Author Lemod
 * @Version 2017/10/13
 */
public class ScreenControllerUtil {

    public static final String INTERFACE_FUNCTION = "接口功能";
    public static final String INTERFACE_DATA_SOURCE = "接口数据来源";
    public static final String INTERFACE_OUTPUT = "接口返回值";
    public static final String INTERFACE_ERROR_PARAMS = "接口入参错误";
    public static final String INTERFACE_MANAGER = "接口负责人";
    public static final String INTERFACE_MANAGER_CALL = "负责人联系方式";

    public enum MANAGER_NUMBER {
        殷奇隆("QQ:403252077"), 高然("QQ:1652117"),
        顾传欢("QQ:785068548"), 郑德俊("QQ:254880723"),
        郑远远("QQ:396006797");

        private String number;

        MANAGER_NUMBER(String number) {
            this.number = number;
        }

        public String getNumber() {
            return number;
        }
    }

    public static void addManagerNumber(Transaction transaction, MANAGER_NUMBER manager) {
        switch (manager) {
            case 殷奇隆:
                transaction.addData(INTERFACE_MANAGER, 殷奇隆);
                transaction.addData(INTERFACE_MANAGER_CALL, 殷奇隆.getNumber());
                break;
            case 高然:
                transaction.addData(INTERFACE_MANAGER, 高然);
                transaction.addData(INTERFACE_MANAGER_CALL, 高然.getNumber());
                break;
            case 郑德俊:
                transaction.addData(INTERFACE_MANAGER, 郑德俊);
                transaction.addData(INTERFACE_MANAGER_CALL, 郑德俊.getNumber());
                break;
            case 顾传欢:
                transaction.addData(INTERFACE_MANAGER, 顾传欢);
                transaction.addData(INTERFACE_MANAGER_CALL, 顾传欢.getNumber());
                break;
            case 郑远远:
                transaction.addData(INTERFACE_MANAGER, 郑远远);
                transaction.addData(INTERFACE_MANAGER_CALL, 郑远远.getNumber());
                break;
        }
    }

    public static void generateSuccessResponse(Map result) {
        result.put("success", true);
        result.put("errorMsg", null);
    }

    public static void generateFailResponse(Map result, Exception e) {
        result.put("success", false);
        result.put("errorMsg", e.getMessage());
    }

    public static long roundByString(String target) {
        Double number = Double.parseDouble(target);
        return Math.round(number);
    }

    /**
     * 获取RESTful调用方真实IP地址
     *
     * @param request 请求
     * @return ip地址
     */
    public static String getClientIPAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 ||
                "unknown".equalsIgnoreCase(ip) || "null".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 ||
                "unknown".equalsIgnoreCase(ip) || "null".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 ||
                "unknown".equalsIgnoreCase(ip) || "null".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 该方法专门用于服务监控大屏，listApps接口
     *
     * @param voList 系统和异常统计信息
     * @return 接口对应的结果集
     */
    public static List<Map<String, Object>>
    generateResultForListApps(List<RunningStatusUnPTVO> voList) {
        Set<String> systemIdSet = new HashSet<>();
        for (RunningStatusUnPTVO vo : voList) {
            systemIdSet.add(vo.getServer());
            systemIdSet.add(vo.getClient());
        }
        Map<String, String> mapOfSystemId = CommonOperation
                .queryAppNameById(systemIdSet.toArray(new String[systemIdSet.size()]));
        List<Map<String, Object>> results = new ArrayList<>();
        for (String systemId : systemIdSet) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", systemId);
            item.put("name", mapOfSystemId.get(systemId));
            item.put("hasError", false);
            for (RunningStatusUnPTVO vo : voList) {
                if (systemId.equals(vo.getServer())
                        || systemId.equals(vo.getClient())) {
                    if (vo.getFailCount() > 0) {
                        item.replace("hasError", true);
                    }
                }
            }
            results.add(item);
        }
        return results;
    }

    public static List<Map<String, Object>>
    generateResultForCommunicationChart(List<ServerCountWithType> serverCountWithTypes) {
        CommonOperation.convertAppNameWithServerCount(serverCountWithTypes);

        //优先级从低到高倒序，先total再fail
        serverCountWithTypes.sort(
                Comparator.comparingLong(ServerCountWithType::getTotalCount).reversed()
                        .thenComparingLong(ServerCountWithType::getFailCount).reversed()
        );

        List<Map<String, Object>> results = new ArrayList<>();
        for (ServerCountWithType countWithType : serverCountWithTypes) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", countWithType.getDomain());
            item.put("name", countWithType.getType());
            item.put("count", countWithType.getTotalCount());
            item.put("countFail", countWithType.getFailCount());

            results.add(item);
        }
        return results;
    }
}
