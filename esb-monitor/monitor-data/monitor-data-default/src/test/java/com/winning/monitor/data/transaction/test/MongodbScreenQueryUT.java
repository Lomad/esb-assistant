package com.winning.monitor.data.transaction.test;

import com.winning.monitor.data.api.base.RunningStatusUnPTVO;
import com.winning.monitor.data.api.largerScreen.IScreenMonitorService;
import com.winning.monitor.data.api.largerScreen.entity.ServiceCount;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Lemod
 * @Version 2018/2/1
 */
@ContextConfiguration(locations = {"classpath*:META-INF/spring/*-context.xml"})
public class MongodbScreenQueryUT extends AbstractJUnit4SpringContextTests {

    @Autowired
    private IScreenMonitorService monitorService;

    @Test
    public void testQueryServiceCount() {
        List<ServiceCount> mapList = monitorService.getServiceCountList("ALL");
        for (ServiceCount entity : mapList) {
            System.out.println(entity.getServiceName());
        }
    }

    @Test
    public void testQuerySystemList() {
        List<RunningStatusUnPTVO> voList = monitorService.getSystemList();
        for (RunningStatusUnPTVO vo : voList) {
            System.out.println(vo.getServer() + vo.getClient());
        }
    }

    @Test
    public void testQueryServiceList(){
        Map<String,Object> params = new HashMap<>();
        params.put("provider","HIP0501");
        params.put("consumer","HIP0105");
        params.put("status","Total");
        List<ServiceCount> serviceList = monitorService.getServiceList(params);

        for (ServiceCount service : serviceList){
            System.out.println(service.getServiceName()+service.getCount());
        }
    }

}
