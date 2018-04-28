package com.winning.monitor.data.api;

import com.winning.monitor.data.api.base.*;
import com.winning.monitor.data.api.transaction.domain.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by nicholasyan on 16/10/20.
 */
public interface ITransactionDataQueryService {

    /**
     * 获取所有的应用服务系统对应的IP地址
     *
     * @param group               系统类别
     * @return
     */
    LinkedHashSet<String> getAllServerIpAddress(String group, String domain);

    /** yql-2017.5.31
     * 指定服务方名字和消费方名字，查询两者之间的服务调用情况
     * @param group which the server belongs to
     * @param serverAppName server name
     * @param clientAppName client name
     * @return {@link TransactionStatisticReport}
     */
    TransactionStatisticReport queryLastHourTransactionTypeReportByServer(String group,String serverAppName,String clientAppName);

    /**
     * 获取当天的TransactionType服务统计结果,根据服务端IP进行分组,不进行分页
     *
     * @param group               系统类别
     * @param serverAppName 服务系统名称
     * @return 统计数据结果集
     */
    TransactionStatisticReport queryTodayTransactionTypeReportByServer(String group, String serverAppName,String status,String clientAppName);

    /**
     * 获取指定小时的TransactionType服务统计结果,根据服务端IP进行分组,不进行分页
     *
     * @param group               系统类别
     * @param serverAppName 应用服务系统名称
     * @param hour          指定小时,格式为 yyyy-MM-dd HH:mm:ss
     * @return 统计数据结果集
     */
    TransactionStatisticReport queryHourTransactionTypeReportByServer(String group,
                                                                      String serverAppName,
                                                                      String hour,
                                                                      String clientAppName);

    /**
     * 获取指定日期的TransactionType服务统计结果,根据服务端IP进行分组,不进行分页
     *
     * @param group               系统类别
     * @param serverAppName 应用服务系统名称
     * @param date          指定日期,格式为 yyyy-MM-dd
     * @return 统计数据结果集
     */
    TransactionStatisticReport queryDayTransactionTypeReportByServer(String group,
                                                                     String serverAppName,
                                                                     String date,
                                                                     String status,
                                                                     String clientAppName);


    /**
     * 获取指定周的TransactionType服务统计结果,根据服务端IP进行分组,不进行分页
     *
     * @param group               系统类别
     * @param serverAppName 应用服务系统名称
     * @param week          指定周的第一天日期,格式为 yyyy-MM-dd
     * @return 统计数据结果集
     */
    TransactionStatisticReport queryWeekTransactionTypeReportByServer(String group,
                                                                      String serverAppName,
                                                                      String week,
                                                                      String status,
                                                                      String clientAppName);


    /**
     * 获取指定月的TransactionType服务统计结果,根据服务端IP进行分组,不进行分页
     *
     * @param group               系统类别
     * @param serverAppName 应用服务系统名称
     * @param month         指定月份的第一条日期,格式为 yyyy-MM-dd
     * @return 统计数据结果集
     */
    TransactionStatisticReport queryMonthTransactionTypeReportByServer(String group,
                                                                       String serverAppName,
                                                                       String month,
                                                                       String clientAppName);


    /**
     * 获取最近一小时的TransactionName服务步骤统计结果不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机
     * @return 统计数据结果集
     */
    TransactionStatisticReport queryLastHourTransactionNameReportByServer(String group,
                                                                          String serverAppName,
                                                                          String transactionTypeName,
                                                                          String serverIpAddress,
                                                                          String clientAppName);

    /**
     * 获取当天的TransactionName服务步骤统计结果不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机
     * @return 统计数据结果集
     */
    TransactionStatisticReport queryTodayTransactionNameReportByServer(String group,
                                                                       String serverAppName,
                                                                          String transactionTypeName,
                                                                          String serverIpAddress,
                                                                          String clientAppName);

    /**
     * 获取指定天的TransactionName服务步骤统计结果不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param date          指定日期,格式为 yyyy-MM-dd
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机
     * @return 统计数据结果集
     */
    TransactionStatisticReport queryDayTransactionNameReportByServer(String group,
                                                                     String serverAppName,
                                                                         String date,
                                                                         String transactionTypeName,
                                                                         String serverIpAddress,
                                                                        String clientAppName);

    /**
     * 获取指定周的TransactionName服务步骤统计结果不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param week                指定周的第一天日期,格式为 yyyy-MM-dd
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机
     * @return 统计数据结果集
     */
     TransactionStatisticReport queryWeekTransactionNameReportByServer(String group,
                                                                       String serverAppName,
                                                                             String week,
                                                                             String transactionTypeName,
                                                                             String serverIpAddress,
                                                                             String clientAppName);


    /**
     * 获取指定月的TransactionType调用次数的结果集,不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param month               指定月份的第一条日期,格式为 yyyy-MM-dd
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机总和
     * @return 调用次数结果集, 返回对象中durations的总长度为60, Key值为0-59,表示一小时从第0分钟到第59分钟的每分钟调用次数
     */
     TransactionCallTimesReport queryMonthTransactionTypeCallTimesReportByServer(String group,
                                                                                 String serverAppName,
                                                                                       String month,
                                                                                       String transactionTypeName,
                                                                                       String serverIpAddress);


    /**
     * 获取指定月的TransactionName服务步骤统计结果不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param month               指定月份的第一条日期,格式为 yyyy-MM-dd
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机
     * @return 统计数据结果集
     */
    TransactionStatisticReport queryMonthTransactionNameReportByServer(String group,
                                                                       String serverAppName,
                                                                              String month,
                                                                              String transactionTypeName,
                                                                              String serverIpAddress,
                                                                              String clientAppName);


    /**
     * 获取指定小时的TransactionName服务步骤统计结果不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param transactionTypeName 服务大类名称
     * @param hour                指定小时,格式为 yyyy-MM-dd HH:mm:ss
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机
     * @return 统计数据结果集
     */
    TransactionStatisticReport queryHourTransactionNameReportByServer(String group,
                                                                      String serverAppName,
                                                                          String hour,
                                                                          String transactionTypeName,
                                                                          String serverIpAddress,
                                                                          String clientAppName);


    /**
     * 获取最近一小时的TransactionType服务对应的消费者统计结果,根据客户端应用名称进行分组,不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机
     * @return 统计数据结果集
     */
    TransactionStatisticReport queryLastHourTransactionTypeReportByClient(String group,
                                                                          String serverAppName,
                                                                          String transactionTypeName,
                                                                          String serverIpAddress,
                                                                          String clientAppName);

    /**
     * 获取指定小时的TransactionType服务对应的者统计结果,根据客户端应用名称进行分组,不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param hour          指定小时,格式为 yyyy-MM-dd HH:mm:ss
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机
     * @return 统计数据结果集
     */
    TransactionStatisticReport queryHourTransactionTypeReportByClient(String group,
                                                                      String serverAppName,
                                                                      String hour,
                                                                      String transactionTypeName,
                                                                      String serverIpAddress,
                                                                      String clientAppName);

       /**
     * 获取最近一小时的TransactionType调用次数的结果集,不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机总和
     * @return 调用次数结果集, 返回对象中durations的总长度为60, Key值为0-59,表示一小时从第0分钟到第59分钟的每分钟调用次数
     */
    TransactionCallTimesReport queryLastHourTransactionTypeCallTimesReportByServer(String group,
                                                                                   String serverAppName,
                                                                                   String transactionTypeName,
                                                                                   String serverIpAddress,
                                                                                   String clientAppName);

    /**
     * 获取最近一小时的TransactionType调用次数的结果集,不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param hour                指定小时,格式为 yyyy-MM-dd HH:mm:ss
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机总和
     * @return 调用次数结果集, 返回对象中durations的总长度为60, Key值为0-59,表示一小时从第0分钟到第59分钟的每分钟调用次数
     */
    TransactionCallTimesReport queryHourTransactionTypeCallTimesReportByServer(String group,
                                                                               String serverAppName,
                                                                               String hour,
                                                                               String transactionTypeName,
                                                                               String serverIpAddress,
                                                                               String clientAppName);


    /**
     * 获取最近一小时的TransactionType调用次数的结果集,不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param date                指定日期,格式为 yyyy-MM-dd
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机总和
     * @return 调用次数结果集, 返回对象中durations的总长度为60, Key值为0-59,表示一小时从第0分钟到第59分钟的每分钟调用次数
     */

     TransactionCallTimesReport queryDayTransactionTypeCallTimesReportByServer(String group,
                                                                               String serverAppName,
                                                                                     String date,
                                                                                     String transactionTypeName,
                                                                                     String serverIpAddress);



    /**
     * 获取当天的TransactionType服务对应的消费者统计结果,根据客户端应用名称进行分组,不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机
     * @return 统计数据结果集
     */
    TransactionStatisticReport queryTodayTransactionTypeReportByClient(String group,
                                                                       String serverAppName,
                                                                       String transactionTypeName,
                                                                       String serverIpAddress,
                                                                       String clientAppName);

    /**
     * 获取指定周的TransactionType调用次数的结果集,不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param week                指定周的第一天日期,格式为 yyyy-MM-dd
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机总和
     * @return 调用次数结果集, 返回对象中durations的总长度为60, Key值为0-59,表示一小时从第0分钟到第59分钟的每分钟调用次数
     */
    TransactionCallTimesReport queryWeekTransactionTypeCallTimesReportByServer(String group,
                                                                               String serverAppName,
                                                                                      String week,
                                                                                      String transactionTypeName,
                                                                                      String serverIpAddress);


    /**
     * 获取指定日期的TransactionType服务对应的消费者统计结果,根据客户端应用名称进行分组,不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param date                指定日期,格式为 yyyy-MM-dd
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机
     * @return 统计数据结果集
     */
     TransactionStatisticReport queryDayTransactionTypeReportByClient(String group,
                                                                      String serverAppName,
                                                                            String date,
                                                                            String transactionTypeName,
                                                                            String serverIpAddress,
                                                                      String clientAppName);

    /**
     * 获取指定周的TransactionType服务对应的消费者统计结果,根据客户端应用名称进行分组,不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param week          指定周的第一天日期,格式为 yyyy-MM-dd
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机
     * @return 统计数据结果集
     */
    TransactionStatisticReport queryWeekTransactionTypeReportByClient(String group,
                                                                      String serverAppName,
                                                                      String week,
                                                                      String transactionTypeName,
                                                                      String serverIpAddress,
                                                                      String clientAppName);

    /**
     * 获取指定月的TransactionType服务对应的消费者统计结果,根据客户端应用名称进行分组,不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param month         指定月份的第一条日期,格式为 yyyy-MM-dd
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机
     * @return 统计数据结果集
     */
     TransactionStatisticReport queryMonthTransactionTypeReportByClient(String group,
                                                                        String serverAppName,
                                                                        String month,
                                                                        String transactionTypeName,
                                                                        String serverIpAddress,
                                                                        String clientAppName);


    /**
     * 获取最近一小时的TransactionType调用次数的结果集,不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机总和
     * @return 调用次数结果集, 返回对象中durations的总长度为24, Key值为0-23,表示一天从0点到23点的每小时调用次数
     */
    TransactionCallTimesReport queryTodayTransactionTypeCallTimesReportByServer(String group,
                                                                                String serverAppName,
                                                                                String transactionTypeName,
                                                                                String serverIpAddress,
                                                                                String clientAppName);


    /**
 * 获取最近一小时内的调用消息明细记录
 *
 * @param group               系统类别
 * @param serverAppName       应用服务系统名称,非空
 * @param transactionTypeName 服务大类名称,非空
 * @param transactionName     服务名称,可选
 * @param serverIpAddress     服务端系统IP地址,可选,不填表示所有服务端主机
 * @param clientAppName       客户端系统名称,可选,不填表示所有客户端系统
 * @param clientIpAddress     客户端系统IP地址,可选,不填表示所有客户端主机
 * @param status              过滤消息状态,可选,可填成功或失败,不填表示所有状态记录,可传入"成功"或"失败"
 * @param startIndex          分页起始位置,非空
 * @param pageSize            分页每页的条数,非空
 * @param orderBy             排序参数, key表示需要排序的字段,value表示排序顺序,DESC或ASC,且要按照顺序,不填则不进行排序
 *                            可传入的Key:
 *                            time      根据时间排序
 *                            duration  根据用时排序
 *                            status    根据状态排序
 * @return 详细调用Transaction的明细清单
 */
TransactionMessageList queryLastHourTransactionMessageList(String group,
                                                           String serverAppName,
                                                           String transactionTypeName,
                                                           String transactionName,
                                                           String serverIpAddress,
                                                           String clientAppName,
                                                           String clientIpAddress,
                                                           String status,
                                                           String keyWords,
                                                           String limitStartTime,
                                                           String limitEndTime,
                                                           int startIndex,
                                                           int pageSize,
                                                           String durationTop,
                                                           Map<String, Object> orderBy);

    /**
     * 获取指定小时内的调用消息明细记录
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称,非空
     * @param hour                指定小时,格式为 yyyy-MM-dd HH:mm:ss
     * @param transactionTypeName 服务大类名称,非空
     * @param transactionName     服务名称,可选
     * @param serverIpAddress     服务端系统IP地址,可选,不填表示所有服务端主机
     * @param clientAppName       客户端系统名称,可选,不填表示所有客户端系统
     * @param clientIpAddress     客户端系统IP地址,可选,不填表示所有客户端主机
     * @param status              过滤消息状态,可选,可填成功或失败,不填表示所有状态记录,可传入"成功"或"失败"
     * @param startIndex          分页起始位置,非空
     * @param pageSize            分页每页的条数,非空
     * @param orderBy             排序参数, key表示需要排序的字段,value表示排序顺序,DESC或ASC,且要按照顺序,不填则不进行排序
     *                            可传入的Key:
     *                            time      根据时间排序
     *                            duration  根据用时排序
     *                            status    根据状态排序
     * @return 详细调用Transaction的明细清单
     */
    TransactionMessageList queryHourTransactionMessageList(String group,
                                                           String serverAppName,
                                                               String hour,
                                                               String transactionTypeName,
                                                               String transactionName,
                                                               String serverIpAddress,
                                                               String clientAppName,
                                                               String clientIpAddress,
                                                               String status,
                                                                String keyWords,
                                                               int startIndex,
                                                               int pageSize,
                                                               String durationTop,
                                                               Map<String, Object> orderBy);


    /**
     * 获取当天内的调用消息明细记录
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称,非空
     * @param transactionTypeName 服务大类名称,非空
     * @param transactionName     服务名称,可选
     * @param serverIpAddress     服务端系统IP地址,可选,不填表示所有服务端主机
     * @param clientAppName       客户端系统名称,可选,不填表示所有客户端系统
     * @param clientIpAddress     客户端系统IP地址,可选,不填表示所有客户端主机
     * @param status              过滤消息状态,可选,可填成功或失败,不填表示所有状态记录,可传入"成功"或"失败"
     * @param startIndex          分页起始位置,非空
     * @param pageSize            分页每页的条数,非空
     * @param keyWords             排序参数, key表示需要排序的字段,value表示排序顺序,DESC或ASC,且要按照顺序,不填则不进行排序
     *                            可传入的Key:
     *                            time      根据时间排序
     *                            duration  根据用时排序
     *                            status    根据状态排序
     * @return 详细调用Transaction的明细清单
     */
    TransactionMessageList queryTodayTransactionMessageList(String group,
                                                            String serverAppName,
                                                            String transactionTypeName,
                                                            String transactionName,
                                                            String serverIpAddress,
                                                            String clientAppName,
                                                            String clientIpAddress,
                                                            String status, String keyWords, String inputIP,
                                                            String limitStartTime, String limitEndTime,
                                                            int startIndex, int pageSize, String durationTop,
                                                            Map<String, Object> previousIndexes);

    /**
     * 获取指定日期内的调用消息明细记录
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称,非空
     * @param date                指定日期,格式为 yyyy-MM-dd
     * @param transactionTypeName 服务大类名称,非空
     * @param transactionName     服务名称,可选
     * @param serverIpAddress     服务端系统IP地址,可选,不填表示所有服务端主机
     * @param clientAppName       客户端系统名称,可选,不填表示所有客户端系统
     * @param clientIpAddress     客户端系统IP地址,可选,不填表示所有客户端主机
     * @param status              过滤消息状态,可选,可填成功或失败,不填表示所有状态记录
     * @param startIndex          分页起始位置,非空
     * @param pageSize            分页每页的条数,非空
     * @param orderBy             排序参数, key表示需要排序的字段,value表示排序顺序,DESC或ASC,且要按照顺序,不填则不进行排序
     * @return 详细调用Transaction的明细清单
     */
    TransactionMessageList queryDayTransactionMessageList(String group,
                                                          String serverAppName,
                                                          String date,
                                                          String transactionTypeName,
                                                          String transactionName,
                                                          String serverIpAddress,
                                                          String clientAppName,
                                                          String clientIpAddress,
                                                          String status,
                                                          String keyWords,
                                                          int startIndex, int pageSize,
                                                          Map<String, Object> orderBy);

    /**
     * 获取指定周内的调用消息明细记录
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称,非空
     * @param week                指定周的第一天日期,格式为 yyyy-MM-dd
     * @param transactionTypeName 服务大类名称,非空
     * @param transactionName     服务名称,可选
     * @param serverIpAddress     服务端系统IP地址,可选,不填表示所有服务端主机
     * @param clientAppName       客户端系统名称,可选,不填表示所有客户端系统
     * @param clientIpAddress     客户端系统IP地址,可选,不填表示所有客户端主机
     * @param status              过滤消息状态,可选,可填成功或失败,不填表示所有状态记录
     * @param startIndex          分页起始位置,非空
     * @param pageSize            分页每页的条数,非空
     * @param orderBy             排序参数, key表示需要排序的字段,value表示排序顺序,DESC或ASC,且要按照顺序,不填则不进行排序
     * @return 详细调用Transaction的明细清单
     */
   TransactionMessageList queryWeekTransactionMessageList(String group,
                                                          String serverAppName,
                                                                  String week,
                                                                  String transactionTypeName,
                                                                  String transactionName,
                                                                  String serverIpAddress,
                                                                  String clientAppName,
                                                                  String clientIpAddress,
                                                                  String status,
                                                                  String keyWords,
                                                                  int startIndex, int pageSize,
                                                                  Map<String, Object> orderBy);

    /**
     * 获取指定月内的调用消息明细记录
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称,非空
     * @param month               指定月份的第一条日期,格式为 yyyy-MM-dd
     * @param transactionTypeName 服务大类名称,非空
     * @param transactionName     服务名称,可选
     * @param serverIpAddress     服务端系统IP地址,可选,不填表示所有服务端主机
     * @param clientAppName       客户端系统名称,可选,不填表示所有客户端系统
     * @param clientIpAddress     客户端系统IP地址,可选,不填表示所有客户端主机
     * @param status              过滤消息状态,可选,可填成功或失败,不填表示所有状态记录
     * @param startIndex          分页起始位置,非空
     * @param pageSize            分页每页的条数,非空
     * @param orderBy             排序参数, key表示需要排序的字段,value表示排序顺序,DESC或ASC,且要按照顺序,不填则不进行排序
     * @return 详细调用Transaction的明细清单
     */
     TransactionMessageList queryMonthTransactionMessageList(String group,
                                                             String serverAppName,
                                                                   String month,
                                                                   String transactionTypeName,
                                                                   String transactionName,
                                                                   String serverIpAddress,
                                                                   String clientAppName,
                                                                   String clientIpAddress,
                                                                   String status,
                                                                   String keyWords,
                                                                   int startIndex, int pageSize,
                                                                   Map<String, Object> orderBy);



    /**
     * 获取指定月内的调用消息明细记录
     *  @param group               系统类别
     * @param messageId           记录Id
     * @param  index                详情的位置,index=-1,表示服务的详情。
     * @param serverAppName       应用服务系统名称,非空
     */
    TransactionMessageListDetail queryTransactionMessageListDetails(String group,
                                                                    String messageId,
                                                                    int index,
                                                                    String serverAppName) ;


    List<ServiceShowVO> getServiceFlowShow(String serverAppName, String messageId);


    /** yql-2017.5.26
     * 非平台端对端服务调用统计
     * @param map queryOperations which params includes at least {@code domain is "ALL" to Overview,or appName},and {@code soc means server or client}
     * @return list of {@link RunningStatusUnPTVO}
     */
    List<RunningStatusUnPTVO> countUnPTRunningStatus(Map map);

    /** yql-2017.5.31
     * 指定服务系统和其各个调用方的交互统计
     * @param map map of params which at least includes server name
     * @return list of {@link ServerCountWithType},whose params:id means client name;totalCount means all communication counts;failCount means the counts of failure communication
     */
    List<ServerCountWithType> queryCommunicationStaticByServer(Map map);


    List<SumVO> countAllServiceSizeByTop(String startTime,String endTime,Map<String,Object> map);

    List<ServiceStatisticVO> getServiceStatistic(String serviceId);

    /**
     * 通用的获取调用消息的明细记录，支持当前小时、指定小时、当天、历史等
     * xuehao 2018-03-25：新增
     * @param group 系统类别
     * @param map   筛选条件
     * @return 详细调用Transaction的明细清单
     */
    TransactionMessageList queryCommonTransactionMessageList(String group, Map<String, Object> map);

}