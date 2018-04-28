package com.winning.monitor.agent.collector.test;

import com.winning.monitor.agent.collector.api.CollectorExecutorFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by nicholasyan on 16/9/5.
 */
public class ExecutorServiceUT {

    @Test
    public void testExecutorServiceInit() {
        CollectorExecutorFactory dataCollectorExecutorService = new CollectorExecutorFactory();
        Assert.assertNotNull(dataCollectorExecutorService);
    }

}
