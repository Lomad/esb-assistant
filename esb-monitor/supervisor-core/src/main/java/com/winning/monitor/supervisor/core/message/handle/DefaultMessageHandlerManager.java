package com.winning.monitor.supervisor.core.message.handle;

import com.winning.monitor.agent.logging.message.MessageTree;
import com.winning.monitor.agent.logging.transaction.Transaction;
import com.winning.monitor.message.Message;
import com.winning.monitor.superisor.consumer.api.analysis.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by nicholasyan on 16/9/9.
 */
public class DefaultMessageHandlerManager implements MessageHandlerManager, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMessageHandlerManager.class);

    private HashMap<String, MessageHandler> messageHandlerHashMap = new HashMap<>();
    private ApplicationContext applicationContext;

    private volatile Map<Long, MessageTree> treeMap = new ConcurrentHashMap<>();

    public Map<Long, MessageTree> getTreeMap() {
        return treeMap;
    }

    @PostConstruct
    public void initialize() {
        Map<String, MessageHandler> beansOfType = this.applicationContext.getBeansOfType(MessageHandler.class);
        for (MessageHandler messageHandler : beansOfType.values()) {
            this.messageHandlerHashMap.put(messageHandler.getMessageTypeName(), messageHandler);
        }
    }

    @Override
    public String handle(Message message) {
        try {
            MessageHandler messageHandler = this.messageHandlerHashMap.get(message.getMessageType());
            String result = messageHandler.handleMessage(message);
            if (result.equals("true")) {
                long duration = ((Transaction) ((MessageTree) message).getMessage())
                        .getDurationInMicros();
                if (treeMap.size() < 10) {
                    treeMap.put(duration, (MessageTree) message);
                } else {
                    List<Map.Entry<Long, MessageTree>> entryList
                            = new ArrayList<>(treeMap.entrySet());
                    entryList.sort(Comparator.comparingLong(Map.Entry::getKey));
                    Map.Entry<Long, MessageTree> minus = entryList.get(0);//最小耗时
                    if (duration > minus.getKey()) {
                        treeMap.put(Long.MAX_VALUE, minus.getValue());
                        treeMap.remove(minus.getKey());
                        treeMap.put(duration, (MessageTree) message);
                    }
                }
                return null;
            }else {
                return result;
            }
        } catch (Throwable e) {
            logger.error("处理消息时发生错误!", e);
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
