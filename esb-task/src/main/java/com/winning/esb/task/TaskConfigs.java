package com.winning.esb.task;

import com.winning.esb.model.ConfigsModel;
import com.winning.esb.model.enums.ConfigsCodeConst;
import com.winning.esb.service.IConfigsService;
import com.winning.esb.utils.DateUtils;
import com.winning.esb.utils.StringUtils;
import com.winning.monitor.data.api.IOverViewQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by xuehao on 2017/9/11.
 */
@Component
public class TaskConfigs {
    private static final Logger logger = LoggerFactory.getLogger(TaskConfigs.class);

    @Autowired
    private IConfigsService configsService;
    @Autowired
    private IOverViewQueryService overviewService;

    /**
     * 监控中服务的历史调用总数配置
     */
    private static ConfigsModel configsTotalHistory;

    @PostConstruct
    private void init() {
        configsTotalHistory = configsService.getByCode(ConfigsCodeConst.MonitorHistoryCallCount);
    }

    /**
     * 设置系统的启动时间，如果对应的参数不存在或值为空，则设为当前启动时间【该函数只需系统启动时执行一次即可】
     */
    public void writeAppStartTime() {
        try {
            ConfigsModel configsModel = configsService.getByCode(ConfigsCodeConst.StartTime);
            if (configsModel != null && StringUtils.isEmpty(configsModel.getValue())) {
                String startTime = DateUtils.getCurrentDatetimeString();
                configsModel.setValue(startTime);
                configsService.editValue(configsModel);
            }
        } catch (Exception ex) {
            logger.error("设置系统启动时间发生异常错误！");
            ex.printStackTrace();
        }
    }

    /**
     * 统计监控中服务的历史调用总数
     */
    public void totalHistory() {
        try {
            if(configsTotalHistory==null) {
                init();
            }
            if (configsTotalHistory != null) {
                long totalCount = overviewService.totalHistory();
                configsTotalHistory.setValue(String.valueOf(totalCount));
                configsService.editValue(configsTotalHistory);
            }
        } catch (Exception ex) {
            logger.error("统计监控中服务的历史调用总数发生异常错误！");
            ex.printStackTrace();
        }
    }
}