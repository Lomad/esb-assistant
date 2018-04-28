package com.winning.monitor.superisor.consumer.logging.transaction;

import com.winning.monitor.agent.config.utils.NetworkInterfaceManager;
import com.winning.monitor.data.api.transaction.HistoryTransactionReportMerger;
import com.winning.monitor.data.api.transaction.vo.TransactionReportType;
import com.winning.monitor.data.api.transaction.vo.TransactionReportVO;
import com.winning.monitor.data.storage.api.ITransactionDataStorage;
import com.winning.monitor.supervisor.core.helper.TimeHelper;
import com.winning.monitor.supervisor.core.task.TaskBuilder;
import com.winning.monitor.supervisor.core.task.TaskHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Component
public class TransactionReportBuilder implements TaskBuilder {

    public final static String TASK_BUILDER_NAME = "TransactionReportBuilder";
    private static final Logger logger = LoggerFactory.getLogger(TransactionReportBuilder.class);

    @Autowired
    private ITransactionDataStorage transactionDataStorage;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public String getName() {
        return TASK_BUILDER_NAME;
    }

    @Override
    public boolean buildDailyTask(String name, String group, String domain, Date period) {

        try {
            long startTime = period.getTime();
            Date endTime = new Date(startTime + TimeHelper.ONE_DAY);

            TransactionReportVO report =
                    this.queryHourlyReportsByDuration(group, domain, period, endTime, 24);

            if (report == null) {
                logger.info("未找到{}的日报表数据!", domain);
                return true;
            }

            endTime = new Date(endTime.getTime() - TimeHelper.ONE_SECOND);
            report.setStartTime(this.sdf.format(period));
            report.setEndTime(this.sdf.format(endTime));
            report.setType(TransactionReportType.DAILY);

            this.saveHistoryTransactionReport(report);

            logger.info("{}的日报表生成报表成功!", domain);

            return true;
        } catch (Exception e) {
            logger.error("TransactionReportBuilder.buildHourlyTask生成报表时发生错误", e);
        }

        return false;
    }

    @Override
    public boolean buildHourlyTask(String name, String group, String domain, Date period) {
        try {
            long startTime = period.getTime();
            Date endTime = new Date(startTime + TimeHelper.ONE_HOUR - TimeHelper.ONE_SECOND);

            String kssj = this.sdf.format(period);
            String jssj = this.sdf.format(endTime);

            //查询出所有时间段内的实时数据
            List<TransactionReportVO> reports =
                    this.transactionDataStorage.queryRealtimeTransactionReports(group, domain,
                            kssj, jssj);

            HistoryTransactionReportMerger transactionReportMerger = new HistoryTransactionReportMerger(0, 1);
            TransactionReportVO report = transactionReportMerger.mergeReports(reports);

            if (report == null) {
                logger.info("未找到{}的小时报表数据!", domain);
                return true;
            }

            //endTime = new Date(endTime.getTime() - TimeHelper.ONE_SECOND);
            report.setStartTime(this.sdf.format(period));
            report.setEndTime(this.sdf.format(endTime));
            report.setType(TransactionReportType.HOURLY);

            this.saveHistoryTransactionReport(report);

            logger.info("{}的小时报表生成报表成功!", domain);

            return true;
        } catch (Exception e) {
            logger.error("TransactionReportBuilder.buildHourlyTask生成报表时发生错误", e);
        }
        return false;
    }

    @Override
    public boolean buildMonthlyTask(String name, String group, String domain, Date period) {
        try {
            Date end = null;

            if (period.equals(TimeHelper.getCurrentMonth())) {
                end = new Date(TimeHelper.getCurrentDay().getTime() + TimeHelper.ONE_DAY);
            } else {
                end = TaskHelper.nextMonthStart(period);
            }

            TransactionReportVO report =
                    this.queryDailyReportsByDuration(group, domain, period, end);

            if (report == null) {
                logger.info("未找到{}的月报表数据!", domain);
                return true;
            }

            report.setIp(NetworkInterfaceManager.INSTANCE.getLocalHostAddress());
            Date endTime = new Date(TaskHelper.nextMonthStart(period).getTime() - TimeHelper.ONE_SECOND);
//            Date endTime = new Date(end.getTime() - TimeHelper.ONE_SECOND);
            report.setStartTime(this.sdf.format(period));
            report.setEndTime(this.sdf.format(endTime));
            report.setType(TransactionReportType.MONTHLY);

            this.saveHistoryTransactionReport(report);

            logger.info("{}的月报表生成报表成功!", domain);

            return true;
        } catch (Exception e) {
            logger.error("TransactionReportBuilder.buildHourlyTask生成报表时发生错误", e);
        }

        return false;
    }

    @Override
    public boolean buildWeeklyTask(String name, String group, String domain, Date period) {
        try {
            Date end = null;

            if (period.equals(TimeHelper.getCurrentWeek())) {
                end = new Date(TimeHelper.getCurrentDay().getTime() + TimeHelper.ONE_DAY);
            } else {
                end = new Date(period.getTime() + TimeHelper.ONE_WEEK);
            }

            TransactionReportVO report =
                    this.queryDailyReportsByDuration(group, domain, period, end);

            if (report == null) {
                logger.info("未找到{}的周报表数据!", domain);
                return true;
            }

            Date endTime = new Date(period.getTime() + TimeHelper.ONE_WEEK - TimeHelper.ONE_SECOND);
            //new Date(end.getTime() - TimeHelper.ONE_SECOND);
            report.setStartTime(this.sdf.format(period));
            report.setEndTime(this.sdf.format(endTime));
            report.setType(TransactionReportType.WEEKLY);

            this.saveHistoryTransactionReport(report);

            logger.info("{}的周报表生成报表成功!", domain);

            return true;
        } catch (Exception e) {
            logger.error("TransactionReportBuilder.buildHourlyTask生成报表时发生错误", e);
        }

        return false;
    }


    private TransactionReportVO queryHourlyReportsByDuration(String group, String domain, Date start, Date end, int hours) {
        String kssj = this.sdf.format(start);
        String jssj = this.sdf.format(end);

        //查询出所有时间段内的实时数据
        List<TransactionReportVO> reports =
                this.transactionDataStorage.queryHistoryTransactionReports(group, domain,null,
                        kssj, jssj, TransactionReportType.HOURLY);

        HistoryTransactionReportMerger transactionReportMerger = new HistoryTransactionReportMerger(0, hours);
        TransactionReportVO transactionReport = transactionReportMerger.mergeReports(reports);
        return transactionReport;
    }

    private TransactionReportVO queryDailyReportsByDuration(String group, String domain, Date start, Date end) {
        String kssj = this.sdf.format(start);
        String jssj = this.sdf.format(end);

        //查询出所有时间段内的实时数据
        List<TransactionReportVO> reports =
                this.transactionDataStorage.queryHistoryTransactionReports(group, domain,null,
                        kssj, jssj, TransactionReportType.DAILY);

        int days = this.daysBetween(start, end);
        HistoryTransactionReportMerger transactionReportMerger = new HistoryTransactionReportMerger(days, 0);
        TransactionReportVO transactionReport = transactionReportMerger.mergeReports(reports);
        return transactionReport;
    }

    private void saveHistoryTransactionReport(TransactionReportVO transactionReportVO) {
        this.transactionDataStorage.storeHistoryTransactionReport(transactionReportVO);
    }

    public int daysBetween(Date start, Date end) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        long time1 = cal.getTimeInMillis();
        cal.setTime(end);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

}
