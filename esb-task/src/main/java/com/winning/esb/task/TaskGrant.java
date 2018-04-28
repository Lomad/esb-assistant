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

/**
 * Created by xuehao on 2017/9/11.
 */
@Component
public class TaskGrant implements ITask {
    private static final Logger logger = LoggerFactory.getLogger(TaskGrant.class);

    @Autowired
    private IConfigsService configsService;
    private Map<String, IMiddlewareService> esbMap;
    @PostConstruct
    private void init() {
        esbMap = AppCtxUtils.getBeansOfType(IMiddlewareService.class);
    }


    @Override
    public void run() {
        //将授权信息同步到Esb
        if(SyncToEsbMark.getSyncGrant()) {
            try {
                releaseLicKey();
                //关闭同步开关
                SyncToEsbMark.setSyncGrant(false);
            } catch (Exception ex) {
                logger.error("将授权信息同步到ESB发生异常错误！" + ex.getMessage());
            }
        }
    }

    public String releaseLicKey() {
        String url = configsService.getByCode(ConfigsCodeConst.ESBUrl).getValue();
        String esbType = configsService.getByCode(ConfigsCodeConst.ESBType).getValue();
        IMiddlewareService middlewareService = esbMap.get(esbType);
        middlewareService.releaseGrant(url);
        return null;
    }

}