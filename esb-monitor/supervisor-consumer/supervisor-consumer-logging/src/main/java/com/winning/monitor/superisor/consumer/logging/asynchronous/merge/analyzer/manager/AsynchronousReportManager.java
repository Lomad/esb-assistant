package com.winning.monitor.superisor.consumer.logging.asynchronous.merge.analyzer.manager;

import com.winning.monitor.data.api.transaction.vo.TransactionReportVO;
import com.winning.monitor.data.storage.api.IAsynchronousLogMessageStorage;
import com.winning.monitor.superisor.consumer.api.report.AbstractReportManager;
import com.winning.monitor.superisor.consumer.logging.transaction.TransactionReportConverter;
import com.winning.monitor.superisor.consumer.logging.transaction.entity.TransactionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.Map;

/**
 * @Author Lemod
 * @Version 2017/9/12
 */
//@Component(value = "asynchronousAnalyzer")
public class AsynchronousReportManager
        extends AbstractReportManager<TransactionReport> implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(AsynchronousReportManager.class);
    private static final long HOUR = 3600000L;
    private Thread m_thread;
    private volatile boolean active = true;

    @Autowired
    private IAsynchronousLogMessageStorage logMessageStorage;

    @PostConstruct
    public void start(){
        logger.info("Manager已经初始化！");
        if (m_thread != null) {
            return;
        }

        m_thread = new Thread(this);
        m_thread.setDaemon(true);
        m_thread.start();
    }

    @PreDestroy
    public void shutdown() {
        active = false;
        for (long time : m_reports.keySet()) {
            storeHourlyReports(time, StoragePolicy.FILE_AND_DB, 0);
        }
    }

    @Override
    protected TransactionReport makeReport(String group, String domain, long startTime, long duration) {
        TransactionReport report = new TransactionReport(group, domain);
        report.setStartTime(new Date(startTime));
        report.setEndTime(new Date(startTime + duration - 1));
        return report;
    }

    @Override
    public Map<String, TransactionReport> loadHourlyReports(long startTime, int index) {
        return null;
    }

    @Override
    public void storeHourlyReports(long startTime, StoragePolicy storagePolicy, int index) {
        Map<String ,TransactionReport> reports = m_reports.get(startTime);

        if (reports == null){
            logger.info("当前时间{}报表不存在!当前存在数据为{}", new Date(startTime), reports.keySet());
            return;
        }
        for (TransactionReport report : reports.values()) {
            TransactionReportVO reportVO =
                    TransactionReportConverter.toTransactionReportVO(report);

            boolean successUpd = logMessageStorage.updateRealTimeReport(reportVO);
            if (!successUpd) {
                logger.error("更新组装后统计报告失败！");
            }
            if (storagePolicy.forDatabase() && successUpd){
                cleanup(startTime);
            }
        }
    }

    @Override
    public void run() {
        while (active){
            try {
                for (long startTime : this.m_reports.keySet()) {
                    if (isActive(startTime)) {
                        this.storeHourlyReports(startTime, StoragePolicy.FILE, 0);
                    } else {
                        this.storeHourlyReports(startTime, StoragePolicy.FILE_AND_DB, 0);
                    }
                }
            } catch (Exception e) {

            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isActive(long time) {
        long current = System.currentTimeMillis();
        return (current - time) < HOUR * 2;
    }
}
