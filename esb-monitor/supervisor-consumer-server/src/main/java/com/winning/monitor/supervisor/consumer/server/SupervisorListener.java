package com.winning.monitor.supervisor.consumer.server;

import com.winning.monitor.supervisor.core.SpringContainer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by nicholasyan on 16/9/27.
 */
public class SupervisorListener implements ServletContextListener {

    private SpringContainer springContainer;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        this.springContainer = new SpringContainer("classpath*:META-INF/spring/*.xml");
        this.springContainer.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        this.springContainer.shutdown();
    }
}
