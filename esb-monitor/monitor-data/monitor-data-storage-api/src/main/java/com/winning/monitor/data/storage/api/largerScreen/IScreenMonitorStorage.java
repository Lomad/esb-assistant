package com.winning.monitor.data.storage.api.largerScreen;

import com.winning.monitor.data.api.base.RunningStatusUnPTVO;
import com.winning.monitor.data.api.largerScreen.entity.ServiceCount;

import java.util.List;
import java.util.Map;

/**
 * @Author Lemod
 * @Version 2018/2/1
 */
public interface IScreenMonitorStorage {

    List<ServiceCount> queryServiceCountList(String startTime, String status);

    List<RunningStatusUnPTVO> querySystemList(String startTime);

    List<ServiceCount> queryServiceList(String startTime,
                                        Map<String, Object> params);
}
