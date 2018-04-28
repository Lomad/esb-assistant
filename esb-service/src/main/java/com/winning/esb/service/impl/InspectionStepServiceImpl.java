package com.winning.esb.service.impl;

import com.winning.esb.dao.db.IDataAccessDao;
import com.winning.esb.model.db.DataSourceModel;
import com.winning.esb.model.enums.ConfigsCodeConst;
import com.winning.esb.service.IConfigsService;
import com.winning.esb.service.IInspectionStepService;
import com.winning.esb.service.db.impl.CommonDataService;
import com.winning.esb.utils.AppCtxUtils;
import com.winning.esb.service.middleware.IMiddlewareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InspectionStepServiceImpl implements IInspectionStepService {
    @Autowired
    private IConfigsService configsService;
    private Map<String, IMiddlewareService> esbMap;

    @PostConstruct
    private void init() {
        esbMap = AppCtxUtils.getBeansOfType(IMiddlewareService.class);
    }

    /**
     * resultMap
     * key error1-fszt为1，value-xh的list
     * key error2-fszt为2，value-xh的list
     * key error3-fszt为3，value-xh的list
     * key error4-fszt为5，value-xh的list
     */
    @Override
    public String checkMzHis(Map<String, Object> resultMap) {
        String configCode = ConfigsCodeConst.DB_HISMZ;
        String error = "";
        Map args = new HashMap();
        String err1Sql = "select * from  PF_MZ_MESSAGE where fszt = :fszt ";
        args.put("fszt", 1);

        IDataAccessDao dao = CommonDataService.getDataAccess(configsService, configCode);
        DataSourceModel dsModel = CommonDataService.getDataSourceModelMap().get(configCode);


        List<Map<String, Object>> err1List = dao.doExecuteSql(
                dsModel.getNamedParameterJdbcTemplate(), err1Sql, args);
        args.clear();
        args.put("fszt", 2);
        List<Map<String, Object>> err2List = CommonDataService.getDataAccess(configsService, configCode)
                .doExecuteSql(CommonDataService.getDataSourceModelMap().get(configCode).getNamedParameterJdbcTemplate(), err1Sql, args);
        args.clear();
        args.put("fszt", 3);
        List<Map<String, Object>> err3List = CommonDataService.getDataAccess(configsService, configCode)
                .doExecuteSql(CommonDataService.getDataSourceModelMap().get(configCode).getNamedParameterJdbcTemplate(), err1Sql, args);
        args.clear();
        args.put("fszt", 5);
        List<Map<String, Object>> err4List = CommonDataService.getDataAccess(configsService, configCode)
                .doExecuteSql(CommonDataService.getDataSourceModelMap().get(configCode).getNamedParameterJdbcTemplate(), err1Sql, args);

        List<String> error1IdList = new ArrayList<>();
        for (Map map : err1List) {
            String xh = map.get("xh").toString();
            error1IdList.add(xh);
        }

        List<String> error2IdList = new ArrayList<>();
        for (Map map : err2List) {
            String xh = map.get("xh").toString();
            error2IdList.add(xh);
        }

        List<String> error3IdList = new ArrayList<>();
        for (Map map : err3List) {
            String xh = map.get("xh").toString();
            error3IdList.add(xh);
        }

        List<String> error4IdList = new ArrayList<>();
        for (Map map : err4List) {
            String xh = map.get("xh").toString();
            error4IdList.add(xh);
        }

        resultMap.put("error1", error1IdList);
        resultMap.put("error2", error2IdList);
        resultMap.put("error3", error3IdList);
        resultMap.put("error4", error4IdList);


        if (err1List.size() > 0 || err2List.size() > 0
                || err3List.size() > 0 || err4List.size() > 0) {
            Integer errTotal = err1List.size() + err2List.size()
                    + err3List.size() + err4List.size();
            error = "有" + errTotal + "条数据堵塞";
        }
        return error;
    }

    /**
     * resultMap
     * key error1-fszt为1，value-xh的list
     * key error2-fszt为2，value-xh的list
     * key error3-fszt为3，value-xh的list
     * key error4-fszt为5，value-xh的list
     */
    @Override
    public String checkZyHis(Map<String, Object> resultMap) {
        String configCode = ConfigsCodeConst.DB_HISZY;
        String err = "";
        Map args = new HashMap();
        String err1Sql = "select  * from   PF_ZY_MESSAGE where fszt = :fszt ";
        args.put("fszt", 1);
        List<Map<String, Object>> err1List = CommonDataService.getDataAccess(configsService, configCode)
                .doExecuteSql(CommonDataService.getDataSourceModelMap().get(configCode).getNamedParameterJdbcTemplate(), err1Sql, args);
        args.clear();
        args.put("fszt", 2);
        List<Map<String, Object>> err2List = CommonDataService.getDataAccess(configsService, configCode)
                .doExecuteSql(CommonDataService.getDataSourceModelMap().get(configCode).getNamedParameterJdbcTemplate(), err1Sql, args);
        args.clear();
        args.put("fszt", 3);
        List<Map<String, Object>> err3List = CommonDataService.getDataAccess(configsService, configCode)
                .doExecuteSql(CommonDataService.getDataSourceModelMap().get(configCode).getNamedParameterJdbcTemplate(), err1Sql, args);
        args.clear();
        args.put("fszt", 5);
        List<Map<String, Object>> err4List = CommonDataService.getDataAccess(configsService, configCode)
                .doExecuteSql(CommonDataService.getDataSourceModelMap().get(configCode).getNamedParameterJdbcTemplate(), err1Sql, args);

        List<String> error1IdList = new ArrayList<>();
        for (Map map : err1List) {
            String xh = map.get("xh").toString();
            error1IdList.add(xh);
        }

        List<String> error2IdList = new ArrayList<>();
        for (Map map : err2List) {
            String xh = map.get("xh").toString();
            error2IdList.add(xh);
        }

        List<String> error3IdList = new ArrayList<>();
        for (Map map : err3List) {
            String xh = map.get("xh").toString();
            error3IdList.add(xh);
        }

        List<String> error4IdList = new ArrayList<>();
        for (Map map : err4List) {
            String xh = map.get("xh").toString();
            error4IdList.add(xh);
        }

        resultMap.put("error1", error1IdList);
        resultMap.put("error2", error2IdList);
        resultMap.put("error3", error3IdList);
        resultMap.put("error4", error4IdList);


        if (err1List.size() > 0 || err2List.size() > 0
                || err3List.size() > 0 || err4List.size() > 0) {
            Integer errTotal = err1List.size() + err2List.size()
                    + err3List.size() + err4List.size();
            err = "有" + errTotal + "条数据堵塞";
        }
        return err;
    }

    /**
     * resultMap
     * key error-错误，value-LogID的list
     */
    @Override
    public String checkHL7Engine(Map<String, Object> resultMap) {
        String configCode = ConfigsCodeConst.DB_HL7Engine;
        String err = "";
        Map args = new HashMap();
        String errSql = "select  * from Log where Severity = :Severity ";
        args.put("Severity", "Error");
        List<Map<String, Object>> errorList = CommonDataService.getDataAccess(configsService, configCode)
                .doExecuteSql(CommonDataService.getDataSourceModelMap().get(configCode).getNamedParameterJdbcTemplate(), errSql, args);
        List<String> errorIdList = new ArrayList<>();
        for (Map map : errorList) {
            String xh = map.get("LogID").toString();
            errorIdList.add(xh);
        }

        resultMap.put("error", errorIdList);

        if (errorList.size() > 0) {
            err = "有" + errorList.size() + "条错误";
        }
        return err;
    }

    /**
     * resultMap
     * key stopped-停止的终端，value-终端id的list
     */
    @Override
    public String checkEndpoint(Map<String, Object> resultMap) {
        String err = "";
        String esbType = configsService.getByCode(ConfigsCodeConst.ESBType).getValue();
        String url = configsService.getByCode(ConfigsCodeConst.ESBUrl).getValue();
        IMiddlewareService middlewareService = esbMap.get(esbType);

        List<String> stopIdList = middlewareService.checkEndpoint(url);

        Integer stopped = stopIdList.size();
        resultMap.put("stopped", stopIdList);
        if (stopped > 0) {
            err = "有" + stopped + "个终端停止";

        }
        return err;

    }

    /**
     * resultMap
     * key stopped-停止的路由，value-路由id的list
     */
    @Override
    public String checkRoute(Map<String, Object> resultMap) {
        String err = "";
        String esbType = configsService.getByCode(ConfigsCodeConst.ESBType).getValue();
        String url = configsService.getByCode(ConfigsCodeConst.ESBUrl).getValue();
        IMiddlewareService middlewareService = esbMap.get(esbType);
        List<String> stopIdList = middlewareService.checkRoute(url);
        Integer stopped = stopIdList.size();
        resultMap.put("stopped", stopIdList);
        if (stopped > 0) {
            err = "有" + stopped + "个路由停止";
        }
        return err;

    }

    /**
     * resultMap
     * key cpu，value-cpu占用百分比
     * key memory，value-memory占用百分比
     * key disk，value-disk占用百分比
     */
    @Override
    public String checkHardware(Map<String, Object> resultMap) {

        String err = "";
        String esbType = configsService.getByCode(ConfigsCodeConst.ESBType).getValue();
        String url = configsService.getByCode(ConfigsCodeConst.ESBUrl).getValue();
        IMiddlewareService middlewareService = esbMap.get(esbType);
        //cpu
        Double result1 = middlewareService.checkCpu(url);
        //内存
        Double result2 = middlewareService.checkMemory(url);
        //硬盘
        Double result3 = middlewareService.checkDisk(url);
        resultMap.put("cpu", result1);

        if (result1 > 0.8) {
            err = "cpu占用过高";
        }

        resultMap.put("memory", result2);

        if (result2 > 0.8) {
            err += "内存不足";
        }

        resultMap.put("disk", result3);

        if (result3 > 0.8) {
            err += "硬盘不足";
        }

        return err;
    }

    /**
     * resultMap
     * key-port ，value-port占用百分比
     */
    @Override
    public String checkOs(Map<String, Object> resultMap) {
        String err = "";
        int sum = 0;
        BufferedReader br = null;
        try {
            Process p = Runtime.getRuntime().exec("netstat -ano");
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                sum++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Integer used = sum - 3;
        Double result = new Double(used / 65535);
        resultMap.put("port", result);
        if (result > 0.8) {
            err = "端口不足";
        }
        return err;
    }

    /**
     * resultMap
     * key-errorNum  ，value-错误列表的数量
     */
    @Override
    public String checkErrorList(Map<String, Object> resultMap) {
        String err = "";
        Integer errNum = 0;
        String esbType = configsService.getByCode(ConfigsCodeConst.ESBType).getValue();
        String url = configsService.getByCode(ConfigsCodeConst.ESBUrl).getValue();
        IMiddlewareService middlewareService = esbMap.get(esbType);
        errNum = middlewareService.checkErrorList(url);


        if (errNum > 0) {
            err = "有" + errNum + "条错误消息";
        }
        resultMap.put("errorNum", errNum);
        return err;
    }

}