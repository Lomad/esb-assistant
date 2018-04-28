package com.winning.esb.task;

import com.winning.esb.utils.AppCtxUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Map;

@Component
public class ExcuteTasker {
    private static final Logger logger = LoggerFactory.getLogger(ExcuteTasker.class);
    private static final Long DAY = 24 * 60 * 60 * 1000L;
    Map<String, ITask> taskMap;

    @Autowired
    private TaskConfigs taskConfigs;
    @Autowired
    private TaskLoopMonitor taskLoopMonitor;

    @PostConstruct
    private void init() {
        taskMap = AppCtxUtils.getBeansOfType(ITask.class);
    }

    //程序启动只执行一次
    @Scheduled(initialDelay = 0, fixedDelay = Long.MAX_VALUE)
    public void startApp() {
        logger.info("【开始】设置系统的启动时间！");
        taskConfigs.writeAppStartTime();
        logger.info("【结束】设置系统的启动时间！");

        logger.info("【开始】统计监控中服务的历史调用总数！");
        taskConfigs.totalHistory();
        logger.info("【结束】统计监控中服务的历史调用总数！");

        //扫描监控中的基本信息（扫描近两天）
        taskLoopMonitor.run(Calendar.DAY_OF_MONTH);

    }

//    //程序启动5秒一次
//    @Scheduled(initialDelay = 0, fixedDelay = 5000)
//    public void startApp() {
//        logger.info("程序启动执行一次！");
//    }

    //1分钟执行一次
    @Scheduled(cron = "0 0/1 * * * ?")
    public void everyMinute() {
        logger.info("1分钟任务开始！");

        //扫描地址任务
        taskMap.get("taskSvcUrl").run();

        //扫描授权任务
        taskMap.get("taskGrant").run();

        //扫描Token任务
        taskMap.get("taskToken").run();

        //扫描TaskSecret任务
        taskMap.get("taskSecret").run();

        //扫描taskToken_Test任务
        taskMap.get("taskToken_Test").run();

        //扫描服务系统对应任务
        taskMap.get("taskSvcApp").run();

        //同步API的URL地址
        taskMap.get("taskApiUrl").run();
        logger.info("1分钟任务结束！");
    }

    //5分钟执行一次
    @Scheduled(cron = "0 0/5 * * * ?")
    public void everyFiveMinute() {
        logger.info("5分钟任务开始！");

        //扫描监控中的基本信息（扫描近两个小时）
        taskLoopMonitor.run(Calendar.HOUR_OF_DAY);

        logger.info("5分钟任务结束！");
    }

    //每天凌晨1点5分执行一次
    @Scheduled(cron = "0 5 1 * * ?")
    public void everyday() {
        logger.info("【开始】统计监控中服务的历史调用总数！");
        taskConfigs.totalHistory();
        logger.info("【结束】统计监控中服务的历史调用总数！");

        //扫描监控中的基本信息（扫描近两天）
        taskLoopMonitor.run(Calendar.DAY_OF_MONTH);

    }

}