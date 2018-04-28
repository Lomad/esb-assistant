package com.winning.esb.controller.monitor;

import com.winning.esb.controller.monitor.utils.CommonOperation;
import com.winning.esb.service.IAppInfoService;
import com.winning.esb.service.ISvcInfoService;
import com.winning.monitor.data.api.IClientTransactionDataQueryService;
import com.winning.monitor.data.api.transaction.domain.TransactionCallTimesReport;
import com.winning.monitor.data.api.transaction.domain.TransactionStatisticData;
import com.winning.monitor.data.api.transaction.domain.TransactionStatisticReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by Evan on 2016/11/3.
 */
@Controller
@RequestMapping(value = {"/view", "/ajax"})
public class ClientHistoryController {
    @Autowired
    private IClientTransactionDataQueryService ClientTransactionDataQuery;
    @Autowired
    private IAppInfoService appInfoService;
    @Autowired
    private ISvcInfoService svcInfoService;

    private String GroupId = "BI";

    /**
     * 返回所有的系统名称
     *
     * @return
     */
    @RequestMapping(value = {"/paas/getAllClientNames"})
    @ResponseBody
    public LinkedHashSet<String> getAllClientNames() {
        LinkedHashSet<String> set = ClientTransactionDataQuery.getAllClientNames(GroupId);
        return set;
    }

    /**
     * 获取指定日期的TransactionType服务统计结果,根据服务端IP进行分组,不进行分页
     */
    @RequestMapping(value = {"/paas/queryDayClientReportByClient"})
    @ResponseBody
    public TransactionStatisticReport queryDayClientReportByClient(String clientAppName, String serverAppName, String date, String status) {
        TransactionStatisticReport report = ClientTransactionDataQuery.queryDayClientReportByClient(GroupId, clientAppName, date, status, serverAppName);
        //转换系统与服务名称
        CommonOperation.convertAppNameAndServiceName(report, appInfoService, svcInfoService);
        return report;
    }

    /**
     * 获取指定周的TransactionType服务统计结果,根据服务端IP进行分组,不进行分页
     */
    @RequestMapping(value = {"/paas/queryWeekClientReportByClient"})
    @ResponseBody
    public TransactionStatisticReport queryWeekClientReportByClient(String clientAppName, String serverAppName, String date, String status) {
        TransactionStatisticReport report = ClientTransactionDataQuery.queryWeekClientReportByClient(GroupId, clientAppName, date, status, serverAppName);
        //
        CommonOperation.convertAppNameAndServiceName(report, appInfoService, svcInfoService);
        return report;
    }

    /**
     * 获取指定的月TransactionType服务统计结果,根据服务端IP进行分组,不进行分页
     */
    @RequestMapping(value = {"/paas/queryMonthClientReportByClient"})
    @ResponseBody
    public TransactionStatisticReport queryMonthClientReportByClient(String clientAppName, String serverAppName, String date) {
        TransactionStatisticReport report = ClientTransactionDataQuery.queryMonthClientReportByClient(GroupId, clientAppName, date, serverAppName);
        //
        CommonOperation.convertAppNameAndServiceName(report, appInfoService, svcInfoService);
        return report;
    }

    /**
     * 获取指定天的TransactionType调用次数的结果集,不进行分页
     */
    @RequestMapping(value = {"/paas/queryDayTransactionTypeCallTimesReportByClient"})
    @ResponseBody
    public TransactionCallTimesReport queryDayTransactionTypeCallTimesReportByClient(String clientAppName, String serverAppName, String date, String transactionTypeName) {
        TransactionCallTimesReport report = ClientTransactionDataQuery.queryDayTransactionTypeCallTimesReportByClient(GroupId, clientAppName, serverAppName, date, transactionTypeName);
        return report;

    }

    /**
     * 获取指定周的TransactionType调用次数的结果集,不进行分页
     */
    @RequestMapping(value = {"/paas/queryWeekTransactionTypeCallTimesReportByClient"})
    @ResponseBody
    public TransactionCallTimesReport queryWeekTransactionTypeCallTimesReportByClient(String clientAppName, String flname, String date, String transactionTypeName) {
        TransactionCallTimesReport report = ClientTransactionDataQuery.queryWeekTransactionTypeCallTimesReportByClient(GroupId, clientAppName, flname, date, transactionTypeName);
        return report;

    }

    /**
     * 获取指定月的TransactionType调用次数的结果集,不进行分页
     *
     * @param clientAppName
     * @param flname
     * @param date
     * @param transactionTypeName
     * @return
     */
    @RequestMapping(value = {"/paas/queryMonthTransactionTypeCallTimesReportByClient"})
    @ResponseBody
    public TransactionCallTimesReport queryMonthTransactionTypeCallTimesReportByClient(String clientAppName, String flname, String date, String transactionTypeName) {
        TransactionCallTimesReport report = ClientTransactionDataQuery.queryMonthTransactionTypeCallTimesReportByClient(GroupId, clientAppName, flname, date, transactionTypeName);
        return report;

    }


    public TransactionStatisticReport getNewReport(TransactionStatisticReport report) {
        List<TransactionStatisticData> TsdArray = report.getTransactionStatisticDatas();
        List<TransactionStatisticData> newTsdArray = new ArrayList<TransactionStatisticData>();
        TransactionStatisticReport newreport = new TransactionStatisticReport();
        long totalSize = 0;
        ourter:
        for (int i = 0; i < TsdArray.size(); i++) {

            TransactionStatisticData Tsd = TsdArray.get(i);
            String serverAppName = Tsd.getServerAppName();

            for (int j = 0; j < newTsdArray.size(); j++) {
                TransactionStatisticData ts = newTsdArray.get(j);
                if (ts.getServerAppName().equals(serverAppName)) {
                    List<TransactionStatisticData> tep = ts.getTransactionStatisticDataDetails();
                    tep.add(Tsd);
                    ts.setTransactionStatisticDataDetails(tep);
                    totalSize++;
                    break ourter;
                }
            }

            TransactionStatisticData newTsd = new TransactionStatisticData();
            newTsd.setServerAppName(serverAppName);
            List<TransactionStatisticData> tep = new ArrayList<TransactionStatisticData>();
            tep.add(Tsd);
            newTsd.setTransactionStatisticDataDetails(tep);
            newTsdArray.add(newTsd);
            totalSize++;
        }

        newreport.setTransactionStatisticDatas(newTsdArray);
        newreport.setTotalSize(totalSize);
        return newreport;
    }

}
