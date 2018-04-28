package com.winning.monitor.agent.config.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.winning.monitor.agent.config.utils.ClassHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Created by nicholasyan on 16/9/7.
 */
public class DefaultSenderConfigFactory implements ISenderConfigFactory {

    private static final String SENDER_JSON_FILENAME = "META-INF/sender.json";

    private static final Logger logger = LoggerFactory.getLogger(DefaultSenderConfigFactory.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private SenderConfig senderConfig;

    public DefaultSenderConfigFactory() {
        try {
            InputStream stream = ClassHelper.getClassLoader().getResourceAsStream(SENDER_JSON_FILENAME);
            this.senderConfig = this.objectMapper.readValue(stream, SenderConfig.class);
        } catch (Throwable e) {
            logger.warn("初始化发送器配置时发生错误", e);
        }
    }


    @Override
    public SenderConfig getSenderConfig() {
        return this.senderConfig;
    }
}
