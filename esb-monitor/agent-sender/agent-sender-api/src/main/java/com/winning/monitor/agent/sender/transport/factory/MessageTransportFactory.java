package com.winning.monitor.agent.sender.transport.factory;

import com.winning.monitor.agent.config.sender.SenderConfig;
import com.winning.monitor.agent.config.utils.ConfigUtils;
import com.winning.monitor.agent.sender.transport.IMessageTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Properties;

/**
 * Created by nicholasyan on 16/9/7.
 */
public class MessageTransportFactory {

    private static final Logger logger = LoggerFactory.getLogger(MessageTransportFactory.class);

    private Properties properties;
    private HashMap<String, IMessageTransportCreator> messageTransportMap = new HashMap<>();

    public MessageTransportFactory() {
        this.properties = ConfigUtils.loadProperties("META-INF/winning/com.winning.monitor.sender", true, true);
        if (this.properties == null || this.properties.size() == 0) {
            return;
        }
        for (String key : this.properties.stringPropertyNames()) {
            String clazzName = this.properties.getProperty(key);
            try {
                IMessageTransportCreator factory = (IMessageTransportCreator) Class.forName(clazzName).newInstance();
                messageTransportMap.put(key, factory);
            } catch (Exception e) {
                String error = "初始化传输器" + key + "[" + clazzName + "]时发生错误";
                logger.warn(error, e);
            }
        }
    }

    public IMessageTransport createMessageTransport(SenderConfig senderConfig) {
        String senderName = senderConfig.getSender();
        IMessageTransportCreator creator = this.messageTransportMap.get(senderName);
        return creator.createMessageTransport(senderConfig);
    }


}
