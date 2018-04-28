package com.winning.monitor.agent.config.collector.test;

import com.winning.monitor.agent.config.collector.impl.CollectorJsonConfigFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by nicholasyan on 16/9/5.
 */
public class CollectorJsonConfigFactoryUT {

    @Test
    public void testInitCollectorJsonConfigFactory() {
        CollectorJsonConfigFactory collectorJsonConfigFactory = new CollectorJsonConfigFactory();
        Assert.assertNotNull(collectorJsonConfigFactory.getCollectorConfigs());
    }


}
