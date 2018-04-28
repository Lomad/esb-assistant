package com.winning.monitor.data.storage.api;

import java.util.List;
import java.util.Map;

public interface IBaseInfoStorage {
    /**
     * 从实时表查询所有的提供方
     */
    List<String> loopProviderFromRealtimeReport(String group, String startTime, String endTime);
    /**
     * 从实时表查询所有的消费方
     */
    List<String> loopConsumerFromRealtimeReport(String group, String startTime, String endTime);

    /**
     * 从实时表查询所有的服务接口
     * @param appId 支持两个格式：String与List<String>
     * @return  返回Map说明：key - 系统代码(appId)， value - 服务代码列表
     */
    Map<String, List<String>> loopSvcFromRealtimeReport(String group, Object appId, String startTime, String endTime);
}