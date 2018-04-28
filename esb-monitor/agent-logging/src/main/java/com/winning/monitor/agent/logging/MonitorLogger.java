package com.winning.monitor.agent.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.winning.monitor.agent.config.utils.ConfigUtils;
import com.winning.monitor.agent.config.utils.Properties;
import com.winning.monitor.agent.logging.entity.ConfigManager;
import com.winning.monitor.agent.logging.message.MessageManager;
import com.winning.monitor.agent.logging.message.internal.DefaultMessageManager;
import com.winning.monitor.agent.logging.message.internal.MessageProducer;
import com.winning.monitor.agent.logging.storage.MessageTreeStorage;
import com.winning.monitor.agent.logging.task.MessageTreeSenderTaskManager;
import com.winning.monitor.agent.logging.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by nicholasyan on 16/9/7.
 */
public class MonitorLogger {

    private static final Logger logger = LoggerFactory.getLogger(MonitorLogger.class);

    private static volatile boolean s_init = false;
    private static MonitorLogger s_instance = new MonitorLogger();
    private static ObjectMapper objectMapper = new ObjectMapper();

    private MessageProducer messageProducer;
    private MessageManager messageManager;
    private MessageTreeStorage messageTreeStorage;
    private ConfigManager configContainer;
    private MessageTreeSenderTaskManager messageTreeSenderTaskManager;

    //xuehao 2017-05-19: 压缩类型（0-不压缩，1-deflater，2-gzip），默认0
    public static byte compressType = 48;   //“0”对应的ASCII为“48”

    private MonitorLogger() {
    }

    public static void checkAndInitialize() {
        if (!s_init) {
            synchronized (s_instance) {
                if (!s_init) {
                    initialize(new File(getHome(), "client.json"));
                    s_init = true;
                }
            }
        }
    }


    private static String getHome() {
        String homePath = Properties.forString().fromEnv().fromSystem().getProperty("MONITOR_HOME", "/data/winning-monitor");
        return homePath;
    }

    private static boolean isInitialized() {
        boolean checkMonitor = true;
        try {
            checkAndInitialize();
        }catch (Exception e){
            e.printStackTrace();
            checkMonitor = false;
        }
        return checkMonitor;
    }

    private static void initialize(File configFile) {
        logger.warn("config file path:" + configFile.toURI().toASCIIString());
        Map<String, Map> jsonMap = null;
        try {
            jsonMap = objectMapper.readValue(configFile, Map.class);

            //xuehao 2017-05-19: 压缩类型
            try {
                String keyName = "compressType";
                if(jsonMap.get("sender").containsKey(keyName)) {
                    compressType = String.valueOf(jsonMap.get("sender").get(keyName)).getBytes()[0];
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (jsonMap == null) {
            throw new RuntimeException("监控平台 初始化失败,未找到配置文件");
        }

        s_instance.configContainer = new ConfigManager(jsonMap);

        s_instance.messageTreeStorage = new MessageTreeStorage(5000);
        s_instance.messageTreeStorage.initialize();

        s_instance.messageManager = new DefaultMessageManager(
                s_instance.configContainer,
                s_instance.messageTreeStorage);
        s_instance.messageManager.initialize();

        s_instance.messageProducer = new MessageProducer(
                s_instance.configContainer,
                s_instance.messageManager);
        s_instance.messageProducer.initialize();


        s_instance.messageTreeSenderTaskManager = new MessageTreeSenderTaskManager(
                s_instance.configContainer,
                s_instance.messageTreeStorage);

        s_instance.messageTreeSenderTaskManager.initialize();
        s_instance.messageTreeSenderTaskManager.start();


        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    s_instance.messageTreeStorage.shutdown();
                    s_instance.messageTreeSenderTaskManager.shutdown();
                } catch (Throwable t) {
                }
            }
        });
    }

    public static void setInputDomain(String domain){
        checkAndInitialize();
        if (domain != null && !domain.isEmpty()){
            s_instance.configContainer.getDomain().setId(domain);
        }else {
            java.util.Properties properties =
                    ConfigUtils.loadProperties("META-INF/app.properties", false, false);
            String appName = properties.getProperty("app.name");
            s_instance.configContainer.getDomain().setId(appName);
        }
    }

    public static Transaction newTransaction(String type, String name) {
        return MonitorLogger.getMessageProducer().newTransaction(type, name);
    }

    public static Transaction beginTransactionType(String type) {
        if (isInitialized()) {
            return MonitorLogger.getMessageProducer().newTransaction(type, "");
        }else {
            return null;
        }
    }

    public static Transaction beginTransactionName(Transaction type, String name) {
        return MonitorLogger.getMessageProducer().newTransaction(type.getType(), name);
    }

    public static void setCaller(String callerName, String callerIP, String callerType) {
        MonitorLogger.getMessageProducer().setCaller(callerName, callerIP, callerType);
    }

    private static MessageProducer getMessageProducer() {
        checkAndInitialize();
        return s_instance.messageProducer;
    }

}
