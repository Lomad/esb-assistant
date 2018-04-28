package com.winning.monitor.supervisor.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by nicholasyan on 16/9/9.
 */
public class SpringContainer {

    private static Logger logger = LoggerFactory.getLogger(SpringContainer.class);

    public String springConfig;
    private volatile boolean running = true;

    private ClassPathXmlApplicationContext applicationContext;

    public SpringContainer() {
        this("classpath*:META-INF/spring/*.xml");
    }

    public SpringContainer(String springConfig) {
        this.springConfig = springConfig;
    }

    public void start() {

        logger.info("监控系统正在启动.......");

        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(springConfig);

        applicationContext.start();
        applicationContext.registerShutdownHook();

        logger.info("监控系统启动完成!");
    }

    public void startAndWait() {
        this.start();
        this.addShutdownHook();

        synchronized (SpringContainer.class) {
            while (running) {
                try {
                    SpringContainer.class.wait();
                } catch (Throwable e) {
                }
            }
        }
    }

    public void shutdown() {
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
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                shutdown();
                synchronized (SpringContainer.class) {
                    running = false;
                    SpringContainer.class.notify();
                }
            }
        });
    }

}
