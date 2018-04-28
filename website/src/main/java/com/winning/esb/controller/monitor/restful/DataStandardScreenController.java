package com.winning.esb.controller.monitor.restful;

import com.alibaba.fastjson.JSON;
import com.winning.esb.controller.monitor.utils.ScreenControllerUtil;
import com.winning.esb.controller.monitor.utils.SpecialConvertUtil;
import com.winning.monitor.agent.logging.MonitorLogger;
import com.winning.monitor.agent.logging.transaction.Transaction;
import com.winning.monitor.data.api.largerScreen.IScreenDataStandardService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.winning.esb.controller.monitor.utils.ScreenControllerUtil.MANAGER_NUMBER.郑德俊;
import static com.winning.esb.controller.monitor.utils.ScreenControllerUtil.MANAGER_NUMBER.顾传欢;
import static com.winning.esb.controller.monitor.utils.ScreenControllerUtil.MANAGER_NUMBER.高然;
import static com.winning.esb.controller.monitor.utils.ScreenControllerUtil.addManagerNumber;

/**
 * @Author Lemod
 * @Version 2017/9/27
 */
@Controller
@RequestMapping(value = "/api/bigscreen/dataStandard/")
public class DataStandardScreenController {

    private static final Logger logger = LoggerFactory.getLogger(DataStandardScreenController.class);

    private static final String MDM = "MDM", EMPI = "EMPI", WDK = "WDK";

    @Autowired
    private IScreenDataStandardService basicAppService;

    @RequestMapping(value = "commonHandle", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String CommonHandle(HttpServletRequest request,
                               @RequestParam String action,
                               @RequestParam(required = false) String msg) {
        String ip = ScreenControllerUtil.getClientIPAddress(request);
        MonitorLogger.setCaller("HIP0202", ip, "PC");
        Transaction transaction;

        Map<String, Object> result = new HashMap<>(), params = new HashMap<>();
        params = SpecialConvertUtil.formatJsonString(msg, params);

        switch (action) {
            case "mdmOverview":
                transaction = MonitorLogger.beginTransactionType("HIP030106");
                try {
                    result = generateOverview(MDM, String.valueOf(params.get("now")));
                    transaction.addData("outParams", result);
                    transaction.success();
                } catch (Exception e) {
                    transaction.addData("errorMessage", e.getMessage());
                    transaction.setStatus(e);
                    transaction.complete();
                }
                addManagerNumber(transaction, 郑德俊);
                break;
            case "mdmCountBeforeMonth":
                transaction = MonitorLogger.beginTransactionType("HIP030107");
                if (StringUtils.hasText((String) params.get("now"))) {
                    try {
                        List monthList = basicAppService.queryMonthCountBeforeMDM((String) params.get("now"));
                        result.put("datas", monthList);
                        ScreenControllerUtil.generateSuccessResponse(result);
                        transaction.addData("outParams", result);
                        transaction.success();
                    } catch (Exception e) {
                        ScreenControllerUtil.generateFailResponse(result, e);
                        transaction.addData("errorMessage", e.getMessage());
                        transaction.setStatus(e);
                        transaction.complete();
                    }
                } else {
                    logger.error("时间参数为空！");
                    ScreenControllerUtil.generateFailResponse(result, new NullPointerException(logger.toString()));
                    transaction.setStatus("1");
                    transaction.complete();
                }
                addManagerNumber(transaction, 郑德俊);
                break;
            case "mdmCountSysPie":
                transaction = MonitorLogger.beginTransactionType("HIP030108");
                if (StringUtils.hasText((String) params.get("now"))) {
                    try {
                        List sysList = basicAppService.queryPerOfCommunication((String) params.get("now"));
                        result.put("datas", sysList);
                        ScreenControllerUtil.generateSuccessResponse(result);
                        transaction.addData("outParams", result);
                        transaction.success();
                    } catch (Exception e) {
                        ScreenControllerUtil.generateFailResponse(result, e);
                        transaction.addData("errorMessage", e.getMessage());
                        transaction.setStatus(e);
                        transaction.complete();
                    }
                } else {
                    logger.error("时间参数为空！");
                    ScreenControllerUtil.generateFailResponse(result, new NullPointerException(logger.toString()));
                    transaction.setStatus("1");
                    transaction.complete();
                }
                addManagerNumber(transaction, 郑德俊);
                break;
            case "mdmCountItem":
                transaction = MonitorLogger.beginTransactionType("HIP030109");
                if (StringUtils.hasText((String) params.get("now"))) {
                    try {
                        List updateList = basicAppService.queryUpdateCount((String) params.get("now"));
                        result.put("datas", updateList);
                        ScreenControllerUtil.generateSuccessResponse(result);
                        transaction.addData("outParams", result);
                        transaction.success();
                    } catch (Exception e) {
                        ScreenControllerUtil.generateFailResponse(result, e);
                        transaction.addData("errorMessage", e.getMessage());
                        transaction.setStatus(e);
                        transaction.complete();
                    }
                } else {
                    logger.error("时间参数为空！");
                    ScreenControllerUtil.generateFailResponse(result, new NullPointerException(logger.toString()));
                    transaction.setStatus("1");
                    transaction.complete();
                }
                addManagerNumber(transaction, 郑德俊);
                break;
            case "empiOverview":
                transaction = MonitorLogger.beginTransactionType("HIP030211");
                try {
                    result = generateOverview(EMPI, (String) params.get("now"));
                    transaction.addData("outParams", result);
                    transaction.success();
                } catch (Exception e) {
                    transaction.addData("errorMessage", e.getMessage());
                    transaction.setStatus(e);
                    transaction.complete();
                }
                addManagerNumber(transaction, 顾传欢);
                break;
            case "empiAddress":
                transaction = MonitorLogger.beginTransactionType("HIP030212");
                try {
                    List addressList = basicAppService.queryAddressInfo((String) params.get("code"));
                    result.put("datas", addressList);
                    ScreenControllerUtil.generateSuccessResponse(result);
                    transaction.addData("outParams", result);
                    transaction.success();
                } catch (Exception e) {
                    ScreenControllerUtil.generateFailResponse(result, e);
                    transaction.addData("errorMessage", e.getMessage());
                    transaction.setStatus(e);
                    transaction.complete();
                }
                addManagerNumber(transaction, 顾传欢);
                break;
/*            case "gxwdOverview":
                transaction = MonitorLogger.beginTransactionType("HIP010605");
                try {
                    String procedureKey = "#result-set-1";
                    List periodList = basicAppService.queryTimePeriodStatistic(procedureKey);

                    Map<String, Object> overviewIndex = (Map<String, Object>) periodList.get(0);
                    result.put("gxwdCount", overviewIndex.get("近一月待授权人数"));
                    result.put("sjjCount", overviewIndex.get("近一月授权人数"));
                    result.put("sysCount", overviewIndex.get("近一月权限下发次数"));

                    ScreenControllerUtil.generateSuccessResponse(result);
                    transaction.addData("outParams", result);
                    transaction.success();
                } catch (Exception e) {
                    ScreenControllerUtil.generateFailResponse(result, e);
                    transaction.setStatus(e);
                    transaction.complete();
                }
                break;
            case "gxwdSdfx":
                transaction = MonitorLogger.beginTransactionType("HIP010606");
                try {
                    String procedureKey = "#result-set-2";
                    List resultSet = basicAppService.queryTimePeriodStatistic(procedureKey);

                    List<Map> periodList = new ArrayList<>();
                    for (Object timeByCount : resultSet) {
                        Map<String, Object> map = new HashMap<>();
                        if (timeByCount instanceof HashMap) {
                            map.put("xaxis", ((HashMap) timeByCount).get("timeKey"));
                            map.put("nincredata", ((HashMap) timeByCount).get("val"));

                            periodList.add(map);
                        }
                    }

                    result.put("datas", periodList);
                    ScreenControllerUtil.generateSuccessResponse(result);
                    transaction.addData("outParams", result);
                    transaction.success();
                } catch (Exception e) {
                    ScreenControllerUtil.generateFailResponse(result, e);
                    transaction.setStatus(e);
                    transaction.complete();
                }
                break;*/
            case "gxwdOverview":
                transaction = MonitorLogger.beginTransactionType("HIP010605");
                try {
                    result = generateOverview(WDK,null);
                    transaction.success();
                    ScreenControllerUtil.generateSuccessResponse(result);
                } catch (Exception e) {
                    transaction.addData("errorMessage", e.getMessage());
                    transaction.setStatus(e);
                    transaction.complete();
                    ScreenControllerUtil.generateFailResponse(result, e);
                }
                addManagerNumber(transaction, 高然);
                break;
            case "gxwdSdfx":
                transaction = MonitorLogger.beginTransactionType("HIP010606");
                if (StringUtils.hasText((String) params.get("cycletype"))) {
                    try {
                        List periodList = basicAppService.queryTimePeriodStatistic((String) params.get("cycletype"));
                        result.put("datas",periodList);
                        transaction.success();
                        ScreenControllerUtil.generateSuccessResponse(result);
                    } catch (Exception e) {
                        transaction.addData("errorMessage", e.getMessage());
                        transaction.setStatus(e);
                        transaction.complete();
                        ScreenControllerUtil.generateFailResponse(result, e);
                    }
                } else {
                    transaction.complete();
                    ScreenControllerUtil.generateFailResponse(result,
                            new Exception("入参有误！"));
                    logger.error("时间参数为空！");
                }
                addManagerNumber(transaction, 高然);
                break;
        }

        return JSON.toJSONString(result);
    }

    private Map generateOverview(String sys, String time) {
        Map result = new HashMap();

        if (sys.equals(WDK)) {
            return basicAppService.countByOverView(sys, time);
        }

        if (StringUtils.hasText(time)) {
            try {
                result = basicAppService.countByOverView(sys, time);
                ScreenControllerUtil.generateSuccessResponse(result);
            } catch (Exception e) {
                ScreenControllerUtil.generateFailResponse(result, e);
            }
        } else {
            logger.error("时间参数为空！");
            ScreenControllerUtil.generateFailResponse(result, new NullPointerException(logger.toString()));
        }
        if (sys.equals(EMPI)){
            Map unRegistry = basicAppService.getUnRegistryCount();
            int unRegistryCount = (int) unRegistry.get("sum");

            result.replace("history_patient_merge_count",unRegistryCount);
        }
        return result;
    }
}
