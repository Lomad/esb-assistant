package com.winning.monitor.supervisor.core.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
public class ReportFacade implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(ReportFacade.class);

    private ApplicationContext applicationContext;

    private Map<String, TaskBuilder> reportBuilders;

    @PostConstruct
    public void initReportBuilders() {
        this.reportBuilders = new HashMap<>();
        Map<String, TaskBuilder> map = this.applicationContext.getBeansOfType(TaskBuilder.class);
        for (TaskBuilder taskBuilder : map.values()) {
            this.reportBuilders.put(taskBuilder.getName(), taskBuilder);
        }
    }

    public boolean builderReport(Task task) {
        try {
            if (task == null) {
                return false;
            }
            int type = task.getTaskType();
            String reportName = task.getReportName();
            String reportDomain = task.getReportDomain();
            String reportGroup = task.getReportGroup();
            Date reportPeriod = task.getReportPeriod();
            TaskBuilder reportBuilder = getReportBuilder(reportName);

            if (reportBuilder == null) {
                logger.error("未找到{}对应的reportBuilder", reportName);
                return false;
            } else {
                boolean result = false;

                if (type == TaskManager.REPORT_HOUR) {
                    result = reportBuilder.buildHourlyTask(reportName, reportGroup, reportDomain, reportPeriod);
                } else if (type == TaskManager.REPORT_DAILY) {
                    result = reportBuilder.buildDailyTask(reportName, reportGroup, reportDomain, reportPeriod);
                } else if (type == TaskManager.REPORT_WEEK) {
                    result = reportBuilder.buildWeeklyTask(reportName, reportGroup, reportDomain, reportPeriod);
                } else if (type == TaskManager.REPORT_MONTH) {
                    result = reportBuilder.buildMonthlyTask(reportName, reportGroup, reportDomain, reportPeriod);
                }
                if (result) {
                    return result;
                } else {
                    logger.error("生成报表时发生错误:" + task.toString());
                }
            }
        } catch (Exception e) {
            logger.error("Error when building report," + e.getMessage(), e);
            //Cat.logError(e);
            return false;
        }
        return false;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private TaskBuilder getReportBuilder(String reportName) {
        return this.reportBuilders.get(reportName);
    }
}
