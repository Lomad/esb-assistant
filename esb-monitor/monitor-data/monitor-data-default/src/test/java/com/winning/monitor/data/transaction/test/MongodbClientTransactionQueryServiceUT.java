package com.winning.monitor.data.transaction.test;

import com.winning.monitor.data.api.IClientTransactionDataQueryService;
import com.winning.monitor.data.api.largerScreen.IScreenDataStandardService;
import com.winning.monitor.data.api.transaction.domain.TransactionCallTimesReport;
import com.winning.monitor.utils.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.LinkedHashSet;

/**
 * Created by wangwh on 2016/11/3.
 */
@ContextConfiguration(locations = {"classpath*:META-INF/spring/*-context.xml"})
public class MongodbClientTransactionQueryServiceUT extends
        AbstractJUnit4SpringContextTests {


    @Autowired
    private IClientTransactionDataQueryService IClienttransactionDataQuery;

    @Autowired
    private IScreenDataStandardService basicAppService;


    @Test
    public void testQueryClientDomains() {
        LinkedHashSet<String> set = IClienttransactionDataQuery.getAllClientNames("BI");
        System.out.println(set);
    }


    @Test
    public void testQueryLastHourClientReportByClient() {
//        TransactionStatisticReport report = IClienttransactionDataQuery.queryLastHourClientReportByClient("BI","住院系统");
//        Assert.assertNotNull(report);

        basicAppService.queryMonthCountBeforeMDM(DateUtils.getCurrentTimeString());
    }

    @Test
    public void testQueryTodayClientReportByClient() {
//        TransactionStatisticReport report = IClienttransactionDataQuery.queryTodayClientTypeReportByClient("BI","住院系统",null);
//        Assert.assertNotNull(report);
        String now = DateUtils.getCurrentTimeString();
        System.out.println(now);

    }

    @Test
    public void testQueryHourClientReportByClient() {
//        TransactionStatisticReport report = IClienttransactionDataQuery.queryHourClientReportByClient("BI","HIS","2016-11-03 15:00:00");
//        Assert.assertNotNull(report);
    }

    @Test
    public void testQueryDayClientReportByClient() {
//        TransactionStatisticReport report = IClienttransactionDataQuery.queryDayClientReportByClient("BI", "His", "2016-10-27",null);
//        Assert.assertNotNull(report);
    }

    @Test
    public void testQueryWeekClientReportByClient() {
//        TransactionStatisticReport report = IClienttransactionDataQuery.queryWeekClientReportByClient("BI", "HIS", "2016-10-31" ,null);
//        Assert.assertNotNull(report);
    }


    @Test
    public void testQueryMonthClientReportByClient() {
//        TransactionStatisticReport report = IClienttransactionDataQuery.queryMonthClientReportByClient("BI", "HIS", "2016-10-01");
//        Assert.assertNotNull(report);
    }

    @Test
    public void testQueryLastHourTransactionTypeCallTimesReportByServer(){
        TransactionCallTimesReport report =
                IClienttransactionDataQuery.queryLastHourTransactionTypeCallTimesReportByClient("BI", "住院系统","test-group","门诊收费服务");
        Assert.assertNotNull(report);
    }

    @Test
    public void testQueryTodayTransactionTypeCallTimesReportByServer(){
        TransactionCallTimesReport report =
                IClienttransactionDataQuery.queryTodayTransactionTypeCallTimesReportByClient("BI", "住院系统","test-group","门诊收费服务");
        Assert.assertNotNull(report);
    }

    @Test
    public void testQueryHourTransactionTypeCallTimesReportByServer(){
        TransactionCallTimesReport report =
                IClienttransactionDataQuery.queryHourTransactionTypeCallTimesReportByClient("BI", "住院系统","test-group","2016-11-04 15:00:00","门诊收费服务");
        Assert.assertNotNull(report);
    }

    @Test
    public void testQueryDayTransactionTypeCallTimesReportByServer(){
        TransactionCallTimesReport report =
                IClienttransactionDataQuery.queryDayTransactionTypeCallTimesReportByClient("BI", "住院系统","test-group","2016-11-04","门诊收费服务");
        Assert.assertNotNull(report);
    }


    @Test
    public void testQueryWeekTransactionTypeCallTimesReportByServer(){
        TransactionCallTimesReport report =
                IClienttransactionDataQuery.queryWeekTransactionTypeCallTimesReportByClient("BI", "住院系统","test-group","2016-10-31","门诊收费服务");
        Assert.assertNotNull(report);
    }

    @Test
    public void testQueryMonthTransactionTypeCallTimesReportByServer(){
        TransactionCallTimesReport report =
                IClienttransactionDataQuery.queryMonthTransactionTypeCallTimesReportByClient("BI", "住院系统","test-group","2016-10-01","门诊收费服务");
        Assert.assertNotNull(report);
    }

}
