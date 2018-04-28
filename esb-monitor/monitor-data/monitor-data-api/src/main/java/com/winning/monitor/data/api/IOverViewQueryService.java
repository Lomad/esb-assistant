package com.winning.monitor.data.api;

import com.winning.monitor.data.api.base.DayCountWithServers;
import com.winning.monitor.data.api.base.ServerCountWithType;
import com.winning.monitor.data.api.base.ServiceDurationStatisticVO;
import com.winning.monitor.data.api.transaction.domain.TransactionMessageList;

import java.util.List;
import java.util.Map;

/**
 * Created by nicholasyan on 16/10/20.
 */
public interface IOverViewQueryService {
    Map<String, Object> init();

    Map<String, Object> countByOverView();

    List<Map> queryTrendChartData(String appId, int type);

    /**
     * 【用于IndexProject页面】
     */
    List<Map> indexProect_queryTrendChartData(int type);

    List<Map<String, Object>> queryClientTypeChartData(int type);

    List<Map<String, Object>> queryGroupByAppInfo(Map datas);

    /**
     * 根据系统代码统计提供方、消费方角色时服务信息以及服务的调用次数与错误次数
     *
     * @param appId              系统代码，如果为空，则表示统计ESB类型的系统
     * @param showAllProvidedSvc 是否显示全部提供的服务：false-隐藏未调用的服务，true-显示全部
     * @return 返回Map，结构如下：
     * "server": [{
     * "serviceCode": "服务代码",
     * "serviceName": "服务名称",
     * "totalCount": 调用次数,
     * "failCount": 错误次数
     * }],
     * "consumer": [{
     * "serviceCode": "服务代码",
     * "serviceName": "服务名称",
     * "totalCount": 调用次数,
     * "failCount": 错误次数
     * }]
     */
    Map queryDetailsByAppInfo(String appId, boolean showAllProvidedSvc);

    /**
     * 统计监控中服务的历史调用总数
     */
    long totalHistory();

    /**
     * 概览界面异常调用统计，返回异常调用次数最多的Top5消费方
     *
     * @return a list of {@link ServerCountWithType}
     */
    List<ServerCountWithType> queryErrorConsumers();

    List<ServiceDurationStatisticVO> queryServiceDuration();

    TransactionMessageList queryDetailsDuration();

    DayCountWithServers queryHistoryCountStatistic(String targetDate);
}