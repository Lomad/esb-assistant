package com.winning.monitor.agent;


import com.winning.monitor.agent.collector.sender.CollectDataSenderTaskManager;
import com.winning.monitor.agent.collector.task.CollectExecutorTaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by nicholasyan on 16/9/5.
 */
public class MonitorServer {

    public static final String DEFAULT_SPRING_CONFIG = "classpath*:META-INF/spring/*.xml";

    private static volatile boolean running = true;

    private static Logger logger = LoggerFactory.getLogger(MonitorServer.class);

    public static void main(String[] args) {

        logger.info("系统正在启动.......");

        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(DEFAULT_SPRING_CONFIG);

        applicationContext.start();

        CollectExecutorTaskManager collectExecutorTaskManager
                = applicationContext.getBean(CollectExecutorTaskManager.class);
        CollectDataSenderTaskManager collectDataSenderTaskManager
                = applicationContext.getBean(CollectDataSenderTaskManager.class);

        collectExecutorTaskManager.start();
        collectDataSenderTaskManager.start();

        addShutdownHook(applicationContext);

        logger.info("系统启动完成!");

        synchronized (MonitorServer.class) {
            while (running) {
                try {
                    MonitorServer.class.wait();
                } catch (Throwable e) {
                }
            }
        }
    }

    private static void addShutdownHook(final ClassPathXmlApplicationContext applicationContext) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    logger.info("系统正在关闭......");
                    //
                    //处理关闭
                    //
                    if (applicationContext != null) {
                        applicationContext.stop();
                        applicationContext.close();
                    }
                    logger.info("系统已安全关闭!");
                    Thread.sleep(1000);
                } catch (Throwable t) {
                    logger.error(t.getMessage(), t);
                }
                synchronized (MonitorServer.class) {
                    running = false;
                    MonitorServer.class.notify();
                }
            }
        });
    }
}
