package com.winning.monitor.supervisor.core.timer;

import com.winning.monitor.agent.logging.message.MessageTree;
import com.winning.monitor.data.storage.api.ITransactionDataStorage;
import com.winning.monitor.supervisor.core.message.handle.DefaultMessageHandlerManager;
import com.winning.monitor.supervisor.core.message.handle.MessageHandlerManager;
import com.winning.monitor.supervisor.core.timer.mongo.CleanJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * @author Lemod
 * @version 2017/5/8
 */
@Component
public class TimerManager {

    @Autowired
    private CleanJob cleanJob;
    @Autowired
    private ITransactionDataStorage transactionDataStorage;
    @Autowired
    private MessageHandlerManager messageHandlerManager;

    private static final Logger logger = LoggerFactory.getLogger(TimerManager.class);
    private volatile boolean isDoing = false;

    @Scheduled(cron = "0 0-5 1 1 * ?")
    public void clean() {
        logger.info("删除任务开始执行！");

        try {
            long period = cleanJob.getClearDate();
            if (period > 0) {
                if (!isDoing) {
                    List<String> domains = getCollectionName();
                    isDoing = true;
                    for (String domain : domains) {
                        int removeCount = cleanJob.remove(period, domain);
                        logger.info(domain + "-删除数据条数：" + removeCount);
                    }
                    isDoing = false;
                    logger.info("删除任务执行成功！");
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 每日凌晨清除前一天垃圾数据
     */
    @Scheduled(cron = "0 30 0 * * ?")
    public void removeErrorLoggingEntity(){
        cleanJob.removeErrorLoggingEntity();
    }

    /**
     * 检查索引；
     * xuehao 2018-03-29：改为每天凌晨2点5分执行一次
     */
    @Scheduled(cron = "0 5 2 * * ?")
    public void updateIndex(){
        cleanJob.ensureIndex();
    }

    @Scheduled(cron = "0/2 * * * * ?")
    public void storeTopDuration(){
        if (messageHandlerManager instanceof DefaultMessageHandlerManager){
            Map<Long, MessageTree> treeMap =
                    ((DefaultMessageHandlerManager) messageHandlerManager).getTreeMap();

            if(treeMap!=null && treeMap.size()>0) {
                cleanJob.storeTopDuration(treeMap);
            }
        }
    }

    private List<String> getCollectionName() {
        List<String> domains = new ArrayList<>();

        LinkedHashSet<String> domainSet = transactionDataStorage.queryAllSystems("BI", "domain");
        for (String domain : domainSet) {
            domains.add("Messages-" + domain);
        }
        return domains;
    }

}
