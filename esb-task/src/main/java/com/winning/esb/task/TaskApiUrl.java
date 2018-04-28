package com.winning.esb.task;

import com.winning.esb.model.enums.ConfigsCodeConst;
import com.winning.esb.service.IConfigsService;
import com.winning.esb.service.taskmark.SyncToEsbMark;
import com.winning.esb.utils.AppCtxUtils;
import com.winning.esb.service.middleware.IMiddlewareService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class TaskApiUrl implements ITask {
    private static final Logger logger = LoggerFactory.getLogger(TaskApiUrl.class);
    @Autowired
    private IConfigsService configsService;
    private Map<String, IMiddlewareService> esbMap;
    @PostConstruct
    private void init() {
        esbMap = AppCtxUtils.getBeansOfType(IMiddlewareService.class);
    }

    @Override
    public void run() {
        if(SyncToEsbMark.getSyncApiUrl()) {
            try {
                releaseApiUrl();
                //关闭同步开关
                SyncToEsbMark.setSyncApiUrl(false);
            } catch (Exception ex) {
                logger.error("将apiUrl信息同步到ESB发生异常错误！" + ex.getMessage());
            }
        }
    }

    private String releaseApiUrl(){
        String url = configsService.getByCode(ConfigsCodeConst.ESBUrl).getValue();
        String esbType = configsService.getByCode(ConfigsCodeConst.ESBType).getValue();
        IMiddlewareService middlewareService = esbMap.get(esbType);
        middlewareService.releaseApiUrl(url);
        return null;
    }


}
