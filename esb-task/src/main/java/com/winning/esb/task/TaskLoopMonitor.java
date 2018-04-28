package com.winning.esb.task;

import com.winning.monitor.data.api.IBaseInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 扫描监控数据，例如业务系统、服务等信息
 */
@Component
public class TaskLoopMonitor {
    private static final Logger logger = LoggerFactory.getLogger(TaskLoopMonitor.class);

    @Autowired
    private IBaseInfoService baseInfoService;

    /**
     * 执行扫描任务，建议执行如下：
     * 1、程序启动时和每天执行一次“近两天（今天与昨天）”扫描；
     * 2、定时执行“近两个小时（当前小时与前一小时）”；
     * @param timeType  时间类型：取值只支持“Calendar.DAY_OF_MONTH、Calendar.HOUR_OF_DAY”两种
     */
    public void run(int timeType) {
        //扫描业务系统信息
        try {
            baseInfoService.loopAppFromRealtimeReport(timeType);
        } catch (Exception ex) {
            logger.error("扫描监控中的业务系统信息发生异常错误！" + ex.getMessage());
        }
        //扫描服务信息
        try {
            baseInfoService.loopSvcFromRealtimeReport(timeType);
        } catch (Exception ex) {
            logger.error("扫描监控中的服务信息发生异常错误！" + ex.getMessage());
        }
    }

}