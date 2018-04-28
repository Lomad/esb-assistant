package com.winning.monitor.agent.config.collector.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.winning.monitor.agent.config.collector.CollectorConfig;
import com.winning.monitor.agent.config.collector.CollectorConfigs;
import com.winning.monitor.agent.config.collector.ICollectorConfigFactory;
import com.winning.monitor.agent.config.utils.ClassHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Created by nicholasyan on 16/9/5.
 */
public class CollectorJsonConfigFactory implements ICollectorConfigFactory {

    private static final String COLLECTOR_JSON_FILENAME = "META-INF/collectors.json";

    private static final Logger logger = LoggerFactory.getLogger(CollectorJsonConfigFactory.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private CollectorConfigs collectorConfigs;

    public CollectorJsonConfigFactory() {
        try {
            InputStream stream = ClassHelper.getClassLoader().getResourceAsStream(COLLECTOR_JSON_FILENAME);
            this.collectorConfigs = this.objectMapper.readValue(stream, CollectorConfigs.class);
        } catch (Throwable e) {
            logger.warn("初始化收集器配置时发生错误", e);
        }
    }

    @Override
    public CollectorConfig[] getCollectorConfigs() {
        if (this.collectorConfigs == null) {
            return new CollectorConfig[0];
        }
        return this.collectorConfigs.getCollectors();
    }
}
