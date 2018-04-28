package com.winning.monitor.data.transaction.test;

import com.winning.monitor.data.api.IPatientTrackService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * Created by nicholasyan on 16/9/14.
 */
@ContextConfiguration(locations = {"classpath*:META-INF/spring/*-context.xml"})
public class MongodbTransactionQueryUT extends
        AbstractJUnit4SpringContextTests {

    @Autowired
    private IPatientTrackService transactionDataQuery;

    @Test
    public void testQuery() {

    }

    @Test
    public void testQueryType() {

    }

}
