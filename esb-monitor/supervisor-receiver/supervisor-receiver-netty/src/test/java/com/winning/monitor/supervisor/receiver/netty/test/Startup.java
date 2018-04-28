package com.winning.monitor.supervisor.receiver.netty.test;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by nicholasyan on 16/9/9.
 */
public class Startup {

    public static final String DEFAULT_SPRING_CONFIG = "classpath*:META-INF/spring/*.xml";

    @Test
    public void testStart() throws InterruptedException {

        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(DEFAULT_SPRING_CONFIG);

        applicationContext.start();

        Thread.sleep(Integer.MAX_VALUE);
    }


}
