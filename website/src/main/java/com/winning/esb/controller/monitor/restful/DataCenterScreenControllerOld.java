package com.winning.esb.controller.monitor.restful;

import com.alibaba.fastjson.JSON;
import com.winning.esb.controller.monitor.model.BigScreenEnum;
import com.winning.esb.controller.monitor.utils.ScreenControllerUtil;
import com.winning.monitor.agent.logging.MonitorLogger;
import com.winning.monitor.agent.logging.transaction.Transaction;
import com.winning.monitor.data.api.largerScreen.IScreenDataCenterService;
import com.winning.monitor.data.api.largerScreen.entity.DatabaseInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.winning.esb.controller.monitor.utils.ScreenControllerUtil.MANAGER_NUMBER.郑远远;
import static com.winning.esb.controller.monitor.utils.ScreenControllerUtil.MANAGER_NUMBER.高然;
import static com.winning.esb.controller.monitor.utils.ScreenControllerUtil.addManagerNumber;

/**
 * @Author Lemod
 * @Version 2017/9/25
 */
@Controller
//xuehao - 2018-03-23 : 适用于旧版大屏程序，将本类的所有代码拷贝到“ServiceShowScreenController”类，并启用下句代码，即可切换到旧版大屏程序
//@RequestMapping(value = "/api/bigscreen/dataCenter/")
@RequestMapping(value = "/api/bigscreen/dataCenterOld/")
public class DataCenterScreenControllerOld {

    @Autowired
    private IScreenDataCenterService dataCenterService;

    @RequestMapping(value = "commonHandle", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String CommonHandle(HttpServletRequest request, @RequestParam String action, @RequestParam(required = false) String aid) {
        String ip = ScreenControllerUtil.getClientIPAddress(request);
        MonitorLogger.setCaller("HIP0202", ip, "PC");

        Map<String, Object> result = new HashMap<>();

        Transaction transaction;

        switch (action) {
            case "getOverview":
                transaction = MonitorLogger.beginTransactionType("HIP010604");
                try {
                    Map pri = dataCenterService.countByOverView();
                    //作业监控、数据检验、接口检验
                    Map second = dataCenterService.queryJobMonitorIndex();
                    if (pri != null) {
                        result.put("ageLimit", pri.get("cdrYear"));
                        result.put("countMedicalRecord", pri.get("lcwdCount"));

                        List<String> appList = new ArrayList<>();
                        List<Map> systemList = (List) pri.get("cdrSyss");
                        for (Map sys : systemList) {
                            appList.addAll(sys.values());
                        }
                        result.put("countApp", appList.size());
                        result.put("appList", appList);

                        result.put("jobMonitor", second.get("jobMonitor"));
                        result.put("dataCheck", second.get("dataCheck"));
                        result.put("interfaceCheck", second.get("interfaceCheck"));
                    }
                    transaction.addData("outParams", result);
                    transaction.success();
                    ScreenControllerUtil.generateSuccessResponse(result);
                } catch (Exception e) {
                    ScreenControllerUtil.generateFailResponse(result, e);
                    transaction.addData("errorMessage", e.getMessage());
                    transaction.setStatus(e);
                    transaction.complete();
                }
                addManagerNumber(transaction, 高然);
                break;
            case "getIndexes":
                transaction = MonitorLogger.beginTransactionType("HIP010601");
                try {
                    if (StringUtils.hasText(aid)) {
                        getIndexes(result, aid);
                        ScreenControllerUtil.generateSuccessResponse(result);
                        transaction.addData("outParams", result);
                        transaction.success();
                    }
                } catch (Exception e) {
                    ScreenControllerUtil.generateFailResponse(result, e);
                    transaction.addData("errorMessage", e.getMessage());
                    transaction.setStatus(e);
                    transaction.complete();
                }
                if (aid != null) {
                    if (aid.equals(BigScreenEnum.ODR.getCode())) {
                        addManagerNumber(transaction, 郑远远);
                    } else {
                        addManagerNumber(transaction, 高然);
                    }
                }
                break;
            case "getTrafficMonitor":
                transaction = MonitorLogger.beginTransactionType("HIP010602");
                try {
                    List pointList = dataCenterService.GetCDCcount("");

                    //将Null值转换为0
                    //converterNullToZero(pointList);

                    ScreenControllerUtil.generateSuccessResponse(result);
                    result.put("pointList", pointList);

                    transaction.addData("outParams", result);
                    transaction.success();
                } catch (Exception e) {
                    ScreenControllerUtil.generateFailResponse(result, e);
                    transaction.addData("errorMessage", e.getMessage());
                    transaction.setStatus(e);
                    transaction.complete();
                }
                addManagerNumber(transaction, 高然);
                break;
            case "getDataPercent":
                transaction = MonitorLogger.beginTransactionType("HIP010603");
                try {
                    List sysList = dataCenterService.GetCDCcount("all");

                    //将Null转换成0
                    //converterNullToZero(sysList);

                    ScreenControllerUtil.generateSuccessResponse(result);
                    result.put("systemList", sysList);

                    transaction.addData("outParams", result);
                    transaction.success();
                } catch (Exception e) {
                    ScreenControllerUtil.generateFailResponse(result, e);
                    transaction.addData("errorMessage", e.getMessage());
                    transaction.setStatus(e);
                    transaction.complete();
                }
                addManagerNumber(transaction, 高然);
                break;
        }
        return JSON.toJSONString(result);
    }

    private void getIndexes(Map<String, Object> result, String aid) {
        Map<String, Map> datas = new HashMap<>();

        if (StringUtils.hasText(aid)) {
            datas.put(aid, getIndex(aid));//add(getIndex(aid));
        } else {
            datas.put(BigScreenEnum.CDR.getCode(), getIndex(BigScreenEnum.CDR.getCode()));
            datas.put(BigScreenEnum.ODR.getCode(), getIndex(BigScreenEnum.ODR.getCode()));
            datas.put(BigScreenEnum.ODS.getCode(), getIndex(BigScreenEnum.ODS.getCode()));
            datas.put(BigScreenEnum.WDK.getCode(), getIndex(BigScreenEnum.WDK.getCode()));
            datas.put(BigScreenEnum.BU.getCode(), getIndex(BigScreenEnum.BU.getCode()));
        }

        result.put("datas", datas);
    }

    private Map<String, Object> getIndex(String aid) {
        Map<String, Object> res = new HashMap<>();

        DatabaseInfoVO vo;
        if (aid.equals(BigScreenEnum.ODR.getCode())) {
//            // yql 2018/1/16 省立版本 第三方ODR库
//            vo = dataCenterService.getODRInfo(aid);
            //公司通用版本
            vo = dataCenterService.getOdrInfo();
        } else {
            vo = dataCenterService.getDataBaseInfo(aid);
        }

        if (vo != null) {
            res.put("name", aid);
            res.put("ipAddress", vo.getIp());
            res.put("dbKind", vo.getDbType());
            res.put("extractData", vo.getDataNumber());
            res.put("countSuccess", vo.getDataNumberSuccess());
            res.put("countFail", vo.getDataNumberFailure());
            res.put("cpu", ScreenControllerUtil.roundByString(vo.getCpuPercent()));
            res.put("memory", ScreenControllerUtil.roundByString(vo.getMemoryPercent()));
            res.put("disk", ScreenControllerUtil.roundByString(vo.getDiskSizePercent()));
            res.put("db", ScreenControllerUtil.roundByString(vo.getDbSizePercent()));
        }
        return res;
    }
}