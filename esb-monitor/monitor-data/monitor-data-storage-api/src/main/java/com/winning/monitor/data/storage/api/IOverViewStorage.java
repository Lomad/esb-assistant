package com.winning.monitor.data.storage.api;

import com.winning.monitor.data.api.base.ServerCountWithType;
import com.winning.monitor.data.api.base.ServiceDurationStatisticVO;
import com.winning.monitor.data.storage.api.entity.MessageTreeList;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by nicholasyan on 16/9/30.
 */
public interface IOverViewStorage {
    public final String KEY_ID = "_id";
    public final String TOTAL_COUNT = "totalCount";
    public final String FAIL_COUNT = "failCount";

    /**
     * 按天和业务系统统计调用总量，今日异常
     *
     * @param dateDay     统计日期，格式：2018-03-27
     * @param svcCodeList 用于过滤未在服务管理注册的服务
     */
    List<ServerCountWithType> dayCountGroupBySys(String dateDay, List<String> svcCodeList);

    //统计服务访问量趋势图
    Map<String, Map<String, Object>> queryTrendChartData(String startTime, String endTime, String appId, int type);

    //统计服务访问量趋势图【用于IndexProject页面】
    Map<String, Map<String, Object>> indexProject_queryTrendChartData(String startTime, int type,
                                                                      List<String> svcCodeList);

    //统计客户端类型饼图图
    LinkedList<Map<String, Object>> queryClientTypeChartData(String startTime, String endTime);

//    //接入系统统计
//    Map<String, Map<String, Object>> queryDataByAppInfo(String appId, String startTime, String endTime);

    /**
     * 服务方统计，key - 系统代码(appId)， value - 统计数据（totalCount - 调用总数，failCount - 失败总数）
     *
     * @param svcCodeList 用于过滤未在服务管理注册的服务
     */
    Map<String, Map<String, Long>> queryDataByServer(List<String> svcCodeList, String startTime, String endTime);

    /**
     * 消费方统计，key - 系统代码(appId)， value - 统计数据（totalCount - 调用总数，failCount - 失败总数）
     *
     * @param svcCodeList 用于过滤未在服务管理注册的服务
     */
    Map<String, Map<String, Long>> queryDataByConsumer(List<String> svcCodeList, String startTime, String endTime);

    /**
     * 作为服务方的详细统计
     *
     * @param appId 支持String或List<String>
     */
    List<Map<String, Object>> queryDataDetailsByServer(Object appId, String startTime, String endTime);

    /**
     * 作为消费方的详细统计
     *
     * @param appId 支持String或List<String>
     */
    List<Map<String, Object>> queryDataDetailsByConsumer(Object appId, String startTime, String endTime);

    /**
     * 统计监控中服务的历史调用总数
     */
    long totalHistory();

    List<ServerCountWithType> queryErrorConsumers(String startTime);

    List<ServiceDurationStatisticVO> queryServiceDuration(String startTime);

    MessageTreeList queryDetailsDuration();
}