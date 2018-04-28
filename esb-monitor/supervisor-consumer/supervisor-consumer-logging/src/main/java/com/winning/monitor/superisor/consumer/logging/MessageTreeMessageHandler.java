package com.winning.monitor.superisor.consumer.logging;

import com.winning.monitor.agent.logging.message.MessageTree;
import com.winning.monitor.superisor.consumer.api.analysis.MessageAnalyzer;
import com.winning.monitor.superisor.consumer.api.analysis.MessageHandler;
import com.winning.monitor.supervisor.core.period.Period;
import com.winning.monitor.supervisor.core.period.PeriodManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Service
public class MessageTreeMessageHandler implements MessageHandler<MessageTree> {

    public static final long MINUTE = 60 * 1000L;

    public static final long HOUR = 60 * MINUTE;


    private static Logger logger = LoggerFactory.getLogger(MessageTreeMessageHandler.class);

    @Autowired
    private PeriodManager m_periodManager;

    @Override
    public String getMessageTypeName() {
        return MessageTree.MESSAGE_TYPE;
    }

    @Override
    public String handleMessage(MessageTree tree) {
        String domain = tree.getDomain();
        String ip = tree.getIpAddress();

        long timestamp = tree.getMessage().getTimestamp();
        Period period = m_periodManager.findPeriod(timestamp);

        if (period != null) {
           return String.valueOf(period.distribute(tree));
        } else {
            logger.error("时间初始化发生错误");
            return "时间初始化发生错误，已超过一小时有效时间！";
        }
    }

    private long getCurrentStartTime() {
        long now = System.currentTimeMillis();
        long time = now - now % HOUR;
        return time;
    }

    @PreDestroy
    public void doCheckpoint() {
//        m_logger.info("starting do checkpoint.");
//        MessageProducer cat = Cat.getProducer();
//        Transaction t = cat.newTransaction("Checkpoint", getClass().getSimpleName());
        logger.info("正在关闭消息分析服务...");
        try {
            long currentStartTime = getCurrentStartTime();
            Period period = m_periodManager.findPeriod(currentStartTime);
//            period.showTaskQueueSize();

            for (MessageAnalyzer analyzer : period.getAnalzyers()) {
                try {
                    analyzer.doCheckpoint(false);
                } catch (Exception e) {
                    //Cat.logError(e);
                }
            }

            try {
                // wait dump analyzer store completed
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e) {
                // ignore
            }

            logger.info("消息分析服务已全部关闭!");
//            t.setStatus(Message.SUCCESS);
        } catch (RuntimeException e) {
//            cat.logError(e);
//            t.setStatus(e);
        } finally {
//            t.complete();
        }
//        m_logger.info("end do checkpoint.");
    }

}
