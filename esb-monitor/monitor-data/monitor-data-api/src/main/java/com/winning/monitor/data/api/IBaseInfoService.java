package com.winning.monitor.data.api;

import java.util.List;
import java.util.Map;

/**
 * Created by xuehao on 17/12/18.
 */
public interface IBaseInfoService {
    /**
     * 从实时表查询近两个小时（或两天）所有的业务系统，并增加到服务管理中
     * @param timeType  时间类型：取值只支持“Calendar.DAY_OF_MONTH、Calendar.HOUR_OF_DAY”两种
     */
    void loopAppFromRealtimeReport(int timeType);

    /**
     * 从实时表查询近两个小时（或两天）所有的服务信息，并增加到服务管理中
     * @param timeType  时间类型：取值只支持“Calendar.DAY_OF_MONTH、Calendar.HOUR_OF_DAY”两种
     */
    void loopSvcFromRealtimeReport(int timeType);

    /**
     * 从实时表查询当天所有的提供方
     */
    List<String> loopProviderToday();
    /**
     * 从实时表查询所有的提供方
     */
    List<String> loopProviderFromRealtimeReport(String group, String startTime, String endTime);
    /**
     * 从实时表查询当天所有的消费方
     */
    List<String> loopConsumerToday();
    /**
     * 从实时表查询所有的消费方
     */
    List<String> loopConsumerFromRealtimeReport(String group, String startTime, String endTime);

    /**
     * 从实时表查询当天所有的服务接口
     */
    List<String> loopSvcToday(List<String> appIds);
    /**
     * 从实时表查询所有的服务接口
     * @param appId 支持两个格式：String与List<String>
     * @return  返回Map说明：key - 系统代码(appId)， value - 服务代码列表
     */
    Map<String, List<String>> loopSvcFromRealtimeReport(String group, Object appId, String startTime, String endTime);
}