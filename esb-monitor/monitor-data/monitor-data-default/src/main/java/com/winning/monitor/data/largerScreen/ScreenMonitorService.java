package com.winning.monitor.data.largerScreen;

import com.winning.monitor.data.api.base.RunningStatusUnPTVO;
import com.winning.monitor.data.api.largerScreen.IScreenMonitorService;
import com.winning.monitor.data.api.largerScreen.entity.ServiceCount;
import com.winning.monitor.data.storage.api.largerScreen.IScreenMonitorStorage;
import com.winning.monitor.data.transaction.utils.DataUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author Lemod
 * @Version 2018/2/1
 */
@Service
public class ScreenMonitorService implements IScreenMonitorService {

    @Autowired
    IScreenMonitorStorage monitorStorage;

    @Override
    public List<ServiceCount> getServiceCountList(String status) {
        String todayStartTime = getTodayStartTime();
        List<ServiceCount> serviceCounts = monitorStorage
                .queryServiceCountList(todayStartTime, status);
        if (status.equals("Error")) {
            serviceCounts = DataUtils.removeZeroCount(serviceCounts);
        }
        return serviceCounts;
    }

    @Override
    public List<RunningStatusUnPTVO> getSystemList() {
        String todayStartTime = getTodayStartTime();

        return monitorStorage.querySystemList(todayStartTime);
    }

    @Override
    public List<ServiceCount> getServiceList(Map<String, Object> params) {
        String todayStartTime = getTodayStartTime(),
                status = (String) params.get("status");

        List<ServiceCount> serviceCountList = monitorStorage
                .queryServiceList(todayStartTime, params);

        if (status.equals("Error")) {
            serviceCountList = DataUtils.removeZeroCount(serviceCountList);
        }
        return serviceCountList;
    }

    //Calendar 获取当天零点
    private String getTodayStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(calendar.getTime());
    }
}
