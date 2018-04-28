package com.winning.monitor.superisor.consumer.logging.asynchronous;

import com.winning.monitor.data.api.base.common.CommonObject;
import com.winning.monitor.data.storage.api.IAsynchronousLogMessageStorage;
import com.winning.monitor.superisor.consumer.logging.asynchronous.merge.IMessageTreeMerge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Lemod
 * @Version 2017/10/11
 */
//@Component
public class AsynchronousMessageManager implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(AsynchronousMessageManager.class);

    private List<CommonObject> messageIdList = new ArrayList<>();
    private Thread m_thread;
    private volatile boolean running = true;

    @Autowired
    protected IAsynchronousLogMessageStorage logMessageStorage;
    @Autowired
    private IMessageTreeMerge treeMerge;

    @PostConstruct
    private void start(){
        if (m_thread != null) {
            return;
        }
        m_thread = new Thread(this);
        m_thread.setDaemon(true);
        m_thread.start();

        logger.info("异步消息合并线程开启...");
    }

    @PreDestroy
    private void shutdown(){
        this.running = false;
    }

    @Override
    public void run() {
        while (running){
            List<String> mergedMessageIDs = null;
            try {
                messageIdList = logMessageStorage.distinctPatentMessageID();
                if (messageIdList.size() > 0) {
                    mergedMessageIDs = treeMerge.mergeMessageTree(messageIdList);
                }
            } catch (Exception e) {
                logger.error("合并线程执行出错！", e);
            } finally {
                if (mergedMessageIDs != null && mergedMessageIDs.size() > 0){
                    int removed = 0;
                    for (String id : mergedMessageIDs) {
                        removed += logMessageStorage.removeDiscardedMessageTree(id);
                    }
                    logger.info("合并成功，删除Temporary-Messages中无效数据："+removed+"条！");
                }
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
