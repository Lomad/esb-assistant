package com.winning.monitor.agent.collector.machine.test;

import com.winning.monitor.agent.collector.api.CollectorExecutorFactory;
import com.winning.monitor.agent.collector.api.entity.CollectData;
import com.winning.monitor.agent.collector.machine.core.SystemDynamicCollector;
import com.winning.monitor.agent.collector.machine.core.SystemInfoCollector;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by nicholasyan on 16/8/26.
 */
public class SystemCollectorUT {

    @Test
    public void testSystemInfoCollector() throws InterruptedException {
        SystemInfoCollector systemCollector = new SystemInfoCollector();
        CollectData data = systemCollector.collect();
        Assert.assertNotNull(data);
    }


    @Test
    public void testSystemDynamicCollect() throws InterruptedException {
        SystemDynamicCollector systemCollector = new SystemDynamicCollector();

        while (true) {
            systemCollector.collect();
            Thread.sleep(1000);
        }
    }

    @Test
    public void testExecutorServiceInit() {
        CollectorExecutorFactory dataCollectorExecutorService = new CollectorExecutorFactory();
        org.junit.Assert.assertNotNull(dataCollectorExecutorService);
    }

}
