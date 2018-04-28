package com.winning.monitor.data.api;

import com.winning.monitor.data.api.transaction.domain.TransactionCallTimesReport;
import com.winning.monitor.data.api.transaction.domain.TransactionStatisticReport;

import java.util.LinkedHashSet;

/**
 * Created by wangwh on 2016/11/2.
 */
public interface IClientTransactionDataQueryService {
    /**
     * 获取所有消费者务系统名称
     *
     * @param group               系统类别
     * @return
     */
    LinkedHashSet<String> getAllClientNames(String group);

    /**
     * 获取最近一小时的消费系统调用服务的统计结果,根据客户端IP进行分组,不进行分页
     *
     * @param group               系统类别
     * @param clientAppName   消费系统名称
     * @return 统计数据结果集
     */
    TransactionStatisticReport queryLastHourClientReportByClient(String group, String clientAppName, String serverAppName);

    /**
     * 获取当天的消费系统调用服务的统计结果,根据客户端IP进行分组,不进行分页
     *
     * @param group               系统类别
     * @param clientAppName   消费系统名称
     * @return 统计数据结果集
     */
    TransactionStatisticReport queryTodayClientTypeReportByClient(String group, String clientAppName, String status,String serverAppName);


    /**
     * 获取指定小时的消费系统调用服务的统计结果,根据客户端IP进行分组,不进行分页
     *
     * @param group               系统类别
     * @param clientAppName   消费系统名称
     * @param hour          指定小时,格式为 yyyy-MM-dd HH:mm:ss
     * @return 统计数据结果集
     */
    TransactionStatisticReport queryHourClientReportByClient(String group,
                                                                      String clientAppName,
                                                                      String hour,
                                                             String serverAppName);

    /**
     * 获取指定日期的TransactionType服务统计结果,根据服务端IP进行分组,不进行分页
     *
     * @param group               系统类别
     * @param clientAppName   消费系统名称
     * @param date          指定日期,格式为 yyyy-MM-dd
     * @return 统计数据结果集
     */
    TransactionStatisticReport queryDayClientReportByClient(String group,
                                                            String clientAppName,
                                                            String date,
                                                            String status,
                                                            String serverAppName);


    /**
     * 获取指定周的TransactionType服务统计结果,根据服务端IP进行分组,不进行分页
     *
     * @param group               系统类别
     * @param clientAppName   消费系统名称
     * @param week          指定周的第一天日期,格式为 yyyy-MM-dd
     * @return 统计数据结果集
     */
    TransactionStatisticReport queryWeekClientReportByClient(String group,
                                                             String clientAppName,
                                                             String week, String status,
                                                             String serverAppName);


    /**
     * 获取指定月的TransactionType服务统计结果,根据服务端IP进行分组,不进行分页
     *
     * @param group               系统类别
     * @param clientAppName   消费系统名称
     * @param month         指定月份的第一条日期,格式为 yyyy-MM-dd
     * @return 统计数据结果集
     */
    TransactionStatisticReport queryMonthClientReportByClient(String group,
                                                                       String clientAppName,
                                                                       String month,
                                                              String serverAppName);


    /**
     * 获取最近一小时的TransactionType调用次数的结果集,不进行分页
     *
     * @param group               系统类别
     * @param clientAppName       消费系统名称
     * @param serverAppName       应用服务系统名称
     * @param transactionTypeName 服务大类名称
     * @return 调用次数结果集, 返回对象中durations的总长度为60, Key值为0-59,表示一小时从第0分钟到第59分钟的每分钟调用次数
     */
    TransactionCallTimesReport queryLastHourTransactionTypeCallTimesReportByClient(String group,
                                                                                   String clientAppName,
                                                                                   String serverAppName,
                                                                                   String transactionTypeName);

    /**
     * 获取当天的TransactionType调用次数的结果集,不进行分页
     *
     * @param group               系统类别
     * @param clientAppName       消费系统名称
     * @param serverAppName       应用服务系统名称
     * @param transactionTypeName 服务大类名称
     * @return 调用次数结果集, 返回对象中durations的总长度为24, Key值为0-23,表示一天从0点到23点的每小时调用次数
     */
     TransactionCallTimesReport queryTodayTransactionTypeCallTimesReportByClient(String group,
                                                                                 String clientAppName,
                                                                                       String serverAppName,
                                                                                       String transactionTypeName);

    /**
     * 获取指定一小时的TransactionType调用次数的结果集,不进行分页
     *
     * @param group               系统类别
     * @param clientAppName       消费系统名称
     * @param serverAppName       应用服务系统名称
     * @param hour                指定小时,格式为 yyyy-MM-dd HH:mm:ss
     * @param transactionTypeName 服务大类名称
     * @return 调用次数结果集, 返回对象中durations的总长度为60, Key值为0-59,表示一小时从第0分钟到第59分钟的每分钟调用次数
     */
    TransactionCallTimesReport queryHourTransactionTypeCallTimesReportByClient(String group,
                                                                               String clientAppName,
                                                                               String serverAppName,
                                                                               String hour,
                                                                               String transactionTypeName);


    /**
     * 获取指定天的TransactionType调用次数的结果集,不进行分页
     *
     * @param group               系统类别
     * @param clientAppName       消费系统名称
     * @param serverAppName       应用服务系统名称
     * @param date                指定日期,格式为 yyyy-MM-dd
     * @param transactionTypeName 服务大类名称
     * @return 调用次数结果集, 返回对象中durations的总长度为60, Key值为0-59,表示一小时从第0分钟到第59分钟的每分钟调用次数
     */

    TransactionCallTimesReport queryDayTransactionTypeCallTimesReportByClient(String group,
                                                                              String clientAppName,
                                                                              String serverAppName,
                                                                              String date,
                                                                              String transactionTypeName);

    /**
     * 获取指定周的TransactionType调用次数的结果集,不进行分页
     *
     * @param group               系统类别
     * @param clientAppName       消费系统名称
     * @param serverAppName       应用服务系统名称
     * @param week                指定周的第一天日期,格式为 yyyy-MM-dd
     * @param transactionTypeName 服务大类名称
     * @return 调用次数结果集, 返回对象中durations的总长度为60, Key值为0-59,表示一小时从第0分钟到第59分钟的每分钟调用次数
     */
    TransactionCallTimesReport queryWeekTransactionTypeCallTimesReportByClient(String group,
                                                                               String clientAppName,
                                                                               String serverAppName,
                                                                               String week,
                                                                               String transactionTypeName);


    /**
     * 获取指定月的TransactionType调用次数的结果集,不进行分页
     *
     * @param group               系统类别
     * @param clientAppName       消费系统名称
     * @param serverAppName       应用服务系统名称
     * @param month               指定月份的第一条日期,格式为 yyyy-MM-dd
     * @param transactionTypeName 服务大类名称
     * @return 调用次数结果集, 返回对象中durations的总长度为60, Key值为0-59,表示一小时从第0分钟到第59分钟的每分钟调用次数
     */
    TransactionCallTimesReport queryMonthTransactionTypeCallTimesReportByClient(String group,
                                                                                String clientAppName,
                                                                                String serverAppName,
                                                                                String month,
                                                                                String transactionTypeName);




}
