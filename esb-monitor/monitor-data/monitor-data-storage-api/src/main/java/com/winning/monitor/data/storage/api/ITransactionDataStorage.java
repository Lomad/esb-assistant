package com.winning.monitor.data.storage.api;

import com.winning.monitor.data.api.base.*;
import com.winning.monitor.data.api.transaction.domain.TransactionStatisticData;
import com.winning.monitor.data.api.transaction.vo.*;
import com.winning.monitor.data.storage.api.exception.StorageException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by nicholasyan on 16/9/14.
 */
public interface ITransactionDataStorage {

    LinkedHashSet<String> queryAllSystems(String group, String kind);

    LinkedHashSet<String> findAllTransactionClients(String group);

    LinkedHashSet<String> findAllServerIpAddress(String group, String domain);

    List<TransactionReportVO> queryRealtimeTransactionReports(String group, String domain, String startTime);

    List<TransactionReportVO> queryRealtimeClientTransactionReportsByServer(String group, String clientAppName, String serverAppName ,String startTime);

    List<TransactionReportVO> queryRealtimeTransactionReports(String group, String domain, String startTime, String endTime);

    /**
     * xuehao 2017-03-28: 支持多个domain筛选
     */
    List<TransactionReportVO> queryRealtimeTransactionReports(String group, List<String> domains, String startTime, String endTime);

    @Deprecated
    List<TransactionReportVO> queryRealtimeClientTransactionReports(String group, String domain, String startTime, String endTime);

    List<TransactionReportVO> queryRealtimeTransactionReportsBySOC(String group, Map<String, Object> map);

    @Deprecated
    List<TransactionReportVO> queryRealtimeClientTransactionReports(String group, Map<String, Object> map);

    @Deprecated
    List<TransactionReportVO> queryRealtimeTransactionReports(String group,String domain, String startTime, String endTime, Map<String, Object> map);

    List<TransactionReportVO> queryRealtimeClientTransactionReports(String group,String domain, String startTime, String endTime, Map<String, Object> map);

    List<TransactionReportVO> querySpecifiedHourTransactionReports(String group,Map map);

    List<TransactionReportVO> queryHistoryTransactionReports(String group, String domain, String clientAppName, String startTime, String endTime,
                                                             TransactionReportType type);

    List<TransactionReportVO> queryHistoryTransactionReports(String group, String domain, String startTime, String endTime,
                                                             TransactionReportType type, Map<String, Object> map);

    List<TransactionReportVO> queryTransactionReportsByType(String group, String domain, String startTime, String typeName,
                                                            TransactionReportType type);

    List<TransactionReportVO> queryHistoryClientTransactionReports(String group, String domain, String serverAppName, String startTime, String endTime, TransactionReportType type);

    List<TransactionReportVO> queryHistoryClientTransactionReports(String group,
                                                                   String domain,
                                                                   String startTime,
                                                                   String endTime,
                                                                   TransactionReportType type,
                                                                   Map<String, Object> map);

    void storeRealtimeTransactionReport(TransactionReportVO transactionReportVO) throws StorageException;

    void storeHistoryTransactionReport(TransactionReportVO transactionReportVO);

    /**
     * xuehao 2017-03-16：支持上海中医院设置历史查询的总数基数设置
     */
    void reviseStatisticsCount(List<TransactionStatisticData> tranList, String reportType);


    //非平台端对端
    List<RunningStatusUnPTVO> countUnPTRunningStatus(String startTime, String endTime, Map<String, Object> map);

    //异常服务次数Top10
    List<SumVO> countAllServiceSizeByTop(String startTime, String endTime, Map<String, Object> map);


    /**
     * get a flow of service with a list of {@link ServiceShowVO} by the specified messageId
     * @param serverAppName domain
     * @param messageId specified messageTree
     * @return map to list
     */
    Map<Integer, ServiceShowVO> getServiceFlowShow(String serverAppName, String messageId);


    List<ServiceStatisticVO> getServiceStatistic(String serviceId, String startTime);


//    /**
//     * query list of url by condition map
//     * @param map conditions
//     * @return {@link ServiceUrlVOList}
//     */
//    ServiceUrlVOList getUrls(Map map);
//
//    /**
//     * save url info which input from  view
//     * @param urlId unique
//     * @param svcType 0,1,2
//     * @param url address+port
//     */
//    void saveInputUrl(String urlId, String svcType, String url);
//
//    List<String> deleteUrl(String urlId);
//
//    List<String> queryPTApp();
}
