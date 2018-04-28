package com.winning.monitor.agent.collector.api;

import com.winning.monitor.agent.collector.api.executor.DataCollectExecutor;
import com.winning.monitor.agent.collector.api.factory.IDataCollectExecutorCreator;
import com.winning.monitor.agent.config.collector.CollectorConfig;
import com.winning.monitor.agent.config.utils.ConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Properties;

/**
 * Created by nicholasyan on 16/9/5.
 */
public class CollectorExecutorFactory {

    private static final Logger logger = LoggerFactory.getLogger(CollectorExecutorFactory.class);

    private Properties properties;
    private HashMap<String, IDataCollectExecutorCreator> collectorMap = new HashMap<>();

    public CollectorExecutorFactory() {
        this.properties = ConfigUtils.loadProperties("META-INF/winning/com.winning.monitor.collectors", true, true);
        if (this.properties == null || this.properties.size() == 0) {
            return;
        }
        for (String key : this.properties.stringPropertyNames()) {
            String clazzName = this.properties.getProperty(key);
            try {
                IDataCollectExecutorCreator factory = (IDataCollectExecutorCreator) Class.forName(clazzName).newInstance();
                collectorMap.put(key, factory);
            } catch (Exception e) {
                String error = "初始化收集器" + key + "[" + clazzName + "]时发生错误";
                logger.warn(error, e);
            }
        }
    }

    public DataCollectExecutor createDataCollectExecutor(CollectorConfig collectorConfig) {
        String collectName = collectorConfig.getCollectorName();
        IDataCollectExecutorCreator creator = this.collectorMap.get(collectName);
        return creator.createDataCollector(collectorConfig);
    }


}
