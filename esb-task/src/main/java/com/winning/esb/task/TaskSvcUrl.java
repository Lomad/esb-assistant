package com.winning.esb.task;

import com.winning.esb.model.enums.ConfigsCodeConst;
import com.winning.esb.service.IConfigsService;
import com.winning.esb.service.ISvcUrlService;
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
public class TaskSvcUrl implements ITask {
    private static final Logger logger = LoggerFactory.getLogger(TaskSvcUrl.class);
    @Autowired
    private ISvcUrlService svcUrlService;
    @Autowired
    private IConfigsService configsService;
    private Map<String, IMiddlewareService> esbMap;

    @PostConstruct
    private void init() {
        esbMap = AppCtxUtils.getBeansOfType(IMiddlewareService.class);
    }


    @Override
    public void run() {
        try {
            //扫描地址状态
            svcUrlService.scanUrl();
        } catch (Exception ex) {
            logger.error("执行URL地址扫描发生异常错误！" + ex.getMessage());
        }

        //将地址信息同步到Esb
        syncToEsb();
    }

    /**
     * 将地址信息同步到Esb
     */
    private void syncToEsb() {
        if (SyncToEsbMark.getSyncUrl()) {
            try {
                //同步url到ESB
                releaseSvcUrl();
                //关闭同步开关
                SyncToEsbMark.setSyncUrl(false);
            } catch (Exception ex) {
                logger.error("将URL地址同步到ESB发生异常错误！" + ex.getMessage());
            }
        }
    }

    private void releaseSvcUrl() {
        String url = configsService.getByCode(ConfigsCodeConst.ESBUrl).getValue();
        String esbType = configsService.getByCode(ConfigsCodeConst.ESBType).getValue();
        IMiddlewareService middlewareService = esbMap.get(esbType);
        middlewareService.releaseUrl(url);
    }
}