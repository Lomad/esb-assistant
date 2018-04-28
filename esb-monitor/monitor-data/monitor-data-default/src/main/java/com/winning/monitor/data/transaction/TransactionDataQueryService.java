package com.winning.monitor.data.transaction;

import com.winning.esb.model.SvcInfoModel;
import com.winning.esb.service.IAppInfoService;
import com.winning.esb.service.ISvcInfoService;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.MapUtils;
import com.winning.esb.utils.StringUtils;
import com.winning.monitor.agent.logging.message.LogMessage;
import com.winning.monitor.agent.logging.message.MessageTree;
import com.winning.monitor.agent.logging.transaction.DefaultTransaction;
import com.winning.monitor.data.api.ITransactionDataQueryService;
import com.winning.monitor.data.api.base.*;
import com.winning.monitor.data.api.enums.DateType;
import com.winning.monitor.data.api.enums.QueryParameterKeys;
import com.winning.monitor.data.api.transaction.domain.*;
import com.winning.monitor.data.api.transaction.vo.*;
import com.winning.monitor.data.api.vo.Range2;
import com.winning.monitor.data.storage.api.ITransactionDataStorage;
import com.winning.monitor.data.storage.api.MessageTreeStorage;
import com.winning.monitor.data.storage.api.entity.MessageTreeList;
import com.winning.monitor.data.transaction.builder.TransactionCallTimesMerger;
import com.winning.monitor.data.transaction.builder.TransactionNameServerStatisticDataMerger;
import com.winning.monitor.data.transaction.builder.TransactionStatisticDataClientMerger;
import com.winning.monitor.data.transaction.builder.TransactionStatisticDataMerger;
import com.winning.monitor.data.transaction.utils.DataUtils;
import com.winning.monitor.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.winning.monitor.data.storage.Utils.ConvertUtils.GetEndTimeLongWithTimeGral;
import static com.winning.monitor.data.storage.Utils.ConvertUtils.GetStartTimeLongWithTimeGral;
import static com.winning.monitor.utils.DateUtils.*;

/**
 * Created by nicholasyan on 16/10/20.
 */
@Service
public class TransactionDataQueryService implements ITransactionDataQueryService {
    @Autowired
    private ITransactionDataStorage transactionDataStorage;
    @Autowired
    private MessageTreeStorage messageTreeStorage;
    @Autowired
    private IAppInfoService appInfoService;
    @Autowired
    private ISvcInfoService svcInfoService;


    /**
     * 获取所有的应用服务系统对应的IP地址
     *
     * @param group  系统类别
     * @param domain
     * @return
     */
    @Override
    public LinkedHashSet<String> getAllServerIpAddress(String group, String domain) {
        return transactionDataStorage.findAllServerIpAddress(group, domain);
    }

    @Override
    public TransactionStatisticReport queryLastHourTransactionTypeReportByServer(String group, String serverAppName, String clientAppName) {
        Map<String, Object> map = new HashMap<>();
        map.put("domain", serverAppName);
        map.put("startTime", getStartTime(DAY_TYPE.CURRENT));
        map.put("clientAppName", clientAppName);

        List<TransactionReportVO> reports =
                this.transactionDataStorage.queryRealtimeTransactionReportsBySOC(group, map);

        DataUtils.removeUnmatched(reports, clientAppName);

        TransactionStatisticDataMerger merger = new TransactionStatisticDataMerger(serverAppName,
                TransactionStatisticDataMerger.TransactionLevel.TransactionType,
                TransactionStatisticDataMerger.StatisticGroupType.Server);

        for (TransactionReportVO report : reports) {
            merger.add(report);
        }

        TransactionStatisticReport report = merger.toTransactionStatisticReport();
        return report;
    }

    /**
     * 获取最近一小时的TransactionName服务步骤统计结果不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机
     * @return 统计数据结果集
     */
    @Override
    public TransactionStatisticReport queryLastHourTransactionNameReportByServer(String group,
                                                                                 String serverAppName,
                                                                                 String transactionTypeName,
                                                                                 String serverIpAddress,
                                                                                 String clientAppName) {
        Map<String, Object> map = new HashMap<>();
        map.put("transactionType", transactionTypeName);

        if (!StringUtils.isEmpty(serverIpAddress)) {
            map.put("serverIp", serverIpAddress);
        }

        if (!StringUtils.isEmpty(clientAppName)) {
            map.put("clientAppName", clientAppName);
        }
        if (!StringUtils.isEmpty(serverAppName)) {
            map.put("domain", serverAppName);
        }
        map.put("startTime", getStartTime(DAY_TYPE.CURRENT));
        map.put("endTime", getStartTime(DAY_TYPE.NOW));

        //获取指定时间的实时数据
        List<TransactionReportVO> reports =
                this.transactionDataStorage.queryRealtimeTransactionReportsBySOC(group, map);

        DataUtils.removeUnmatched(reports, clientAppName);

        TransactionNameServerStatisticDataMerger merger = new TransactionNameServerStatisticDataMerger(serverAppName, clientAppName,
                serverIpAddress, transactionTypeName);

        for (TransactionReportVO report : reports) {
            merger.add(report);
        }

        TransactionStatisticReport report = merger.toTransactionStatisticReport();
        return report;
    }

    /**
     * 获取指定小时的TransactionName服务步骤统计结果不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param hour                指定小时,格式为 yyyy-MM-dd HH:mm:ss
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机
     * @return 统计数据结果集
     */
    @Override
    public TransactionStatisticReport queryHourTransactionNameReportByServer(String group,
                                                                             String serverAppName,
                                                                             String hour,
                                                                             String transactionTypeName,
                                                                             String serverIpAddress,
                                                                             String clientAppName) {
        Map<String, Object> map = new HashMap<>();
        map.put("transactionType", transactionTypeName);

        if (!StringUtils.isEmpty(serverIpAddress)) {
            map.put("serverIp", serverIpAddress);
        }

        if (!StringUtils.isEmpty(clientAppName)) {
            map.put("clientAppName", clientAppName);
        }


        String startTime = hour.replace(hour.substring(14, 19), "00:00");
        String endTime = hour.substring(0, 14) + "59:59";

        //获取指定时间的实时数据
        List<TransactionReportVO> reports =
                this.transactionDataStorage.queryHistoryTransactionReports(group,
                        serverAppName, startTime,
                        endTime, TransactionReportType.HOURLY, map);

        DataUtils.removeUnmatched(reports, clientAppName);

        TransactionNameServerStatisticDataMerger merger = new TransactionNameServerStatisticDataMerger(serverAppName, clientAppName,
                serverIpAddress, transactionTypeName);

        for (TransactionReportVO report : reports) {
            merger.add(report);
        }

        TransactionStatisticReport report = merger.toTransactionStatisticReport();
        return report;
    }


    /**
     * 获取当天的TransactionName服务步骤统计结果不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机
     * @return 统计数据结果集
     */
    @Override
    public TransactionStatisticReport queryTodayTransactionNameReportByServer(String group,
                                                                              String serverAppName,
                                                                              String transactionTypeName,
                                                                              String serverIpAddress,
                                                                              String clientAppName) {
        Map<String, Object> map = new HashMap<>();
        map.put("transactionType", transactionTypeName);

        if (!StringUtils.isEmpty(serverIpAddress)) {
            map.put("serverIp", serverIpAddress);
        }

        if (!StringUtils.isEmpty(clientAppName)) {
            map.put("clientAppName", clientAppName);
        }
        if (!StringUtils.isEmpty(serverAppName)) {
            map.put("domain", serverAppName);
        }
        map.put("startTime", getStartTime(DAY_TYPE.TODAY));
        map.put("endTime", getStartTime(DAY_TYPE.NOW));

        //获取指定时间的实时数据
        List<TransactionReportVO> reports =
                this.transactionDataStorage.queryRealtimeTransactionReportsBySOC(group, map);

        DataUtils.removeUnmatched(reports, clientAppName);


        TransactionNameServerStatisticDataMerger merger = new TransactionNameServerStatisticDataMerger(serverAppName, clientAppName,
                serverIpAddress, transactionTypeName);

        for (TransactionReportVO report : reports) {
            merger.add(report);
        }

        TransactionStatisticReport report = merger.toTransactionStatisticReport();
        return report;
    }

    /**
     * 获取指定天的TransactionName服务步骤统计结果不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param date                指定日期,格式为 yyyy-MM-dd
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机
     * @return 统计数据结果集
     */
    @Override
    public TransactionStatisticReport queryDayTransactionNameReportByServer(String group,
                                                                            String serverAppName,
                                                                            String date,
                                                                            String transactionTypeName,
                                                                            String serverIpAddress,
                                                                            String clientAppName) {
        String startTime = date + " " + "00:00:00";
        String endTime = date + " " + "23:59:59";

        Map<String, Object> map = new HashMap<>();
        map.put("transactionType", transactionTypeName);

        if (!StringUtils.isEmpty(serverIpAddress)) {
            map.put("serverIp", serverIpAddress);
        }

        if (!StringUtils.isEmpty(clientAppName)) {
            map.put("clientAppName", clientAppName);
        }

        //获取指定时间的实时数据
        List<TransactionReportVO> reports =
                this.transactionDataStorage.queryHistoryTransactionReports(group, serverAppName, startTime, endTime,
                        TransactionReportType.DAILY, map);

        TransactionNameServerStatisticDataMerger merger = new TransactionNameServerStatisticDataMerger(serverAppName, clientAppName,
                serverIpAddress, transactionTypeName);

        for (TransactionReportVO report : reports) {
            merger.add(report);
        }

        TransactionStatisticReport report = merger.toTransactionStatisticReport();
        return report;
    }


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
    @Override
    public TransactionStatisticReport queryWeekTransactionNameReportByServer(String group,
                                                                             String serverAppName,
                                                                             String week,
                                                                             String transactionTypeName,
                                                                             String serverIpAddress,
                                                                             String clientAppName) {
        HashMap<String, String> weeklyTime = getWeeklyDay(week);

        String startTime = weeklyTime.get("Monday");
        String endTime = weeklyTime.get("Sunday");

        Map<String, Object> map = new HashMap<>();
        map.put("transactionType", transactionTypeName);

        if (!StringUtils.isEmpty(serverIpAddress)) {
            map.put("serverIp", serverIpAddress);
        }

        if (!StringUtils.isEmpty(clientAppName)) {
            map.put("clientAppName", clientAppName);
        }

        List<TransactionReportVO> reports =
                this.transactionDataStorage.queryHistoryTransactionReports(group, serverAppName, startTime, endTime,
                        TransactionReportType.WEEKLY, map);

        TransactionNameServerStatisticDataMerger merger = new TransactionNameServerStatisticDataMerger(serverAppName, clientAppName,
                serverIpAddress, transactionTypeName);

        for (TransactionReportVO report : reports) {
            merger.add(report);
        }

        TransactionStatisticReport report = merger.toTransactionStatisticReport();
        return report;
    }

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
    @Override
    public TransactionStatisticReport queryMonthTransactionNameReportByServer(String group,
                                                                              String serverAppName,
                                                                              String month,
                                                                              String transactionTypeName,
                                                                              String serverIpAddress,
                                                                              String clientAppName) {

        String startTime = getMonthFirstDay(month) + " 00:00:00";
        String endTime = getMonthLastDay(month) + " 23:59:59";

        Map<String, Object> map = new HashMap<>();
        map.put("transactionType", transactionTypeName);

        if (!StringUtils.isEmpty(serverIpAddress)) {
            map.put("serverIp", serverIpAddress);
        }

        if (!StringUtils.isEmpty(clientAppName)) {
            map.put("clientAppName", clientAppName);
        }

        //获取指定时间的实时数据
        List<TransactionReportVO> reports =
                this.transactionDataStorage.queryHistoryTransactionReports(group, serverAppName, startTime, endTime,
                        TransactionReportType.MONTHLY, map);

        TransactionNameServerStatisticDataMerger merger = new TransactionNameServerStatisticDataMerger(serverAppName, clientAppName,
                serverIpAddress, transactionTypeName);

        for (TransactionReportVO report : reports) {
            merger.add(report);
        }

        TransactionStatisticReport report = merger.toTransactionStatisticReport();
        return report;
    }


    /**
     * 获取当天的TransactionType服务统计结果,根据服务端IP进行分组,不进行分页
     *
     * @param group         系统类别
     * @param serverAppName 服务系统名称
     * @return 统计数据结果集
     */
    @Override
    public TransactionStatisticReport queryTodayTransactionTypeReportByServer(String group,
                                                                              String serverAppName, String status,
                                                                              String clientAppName) {

        Map<String, Object> map = new HashMap<>();
        map.put("domain", serverAppName);
        map.put("startTime", getStartTime(DAY_TYPE.TODAY));
        map.put("endTime", getEndTime(DAY_TYPE.TODAY));
        map.put("clientAppName", clientAppName);

        List<TransactionReportVO> reports =
                this.transactionDataStorage.queryRealtimeTransactionReportsBySOC(group, map);

        DataUtils.removeUnmatched(reports, clientAppName);

        TransactionStatisticDataMerger merger = new TransactionStatisticDataMerger(serverAppName,
                TransactionStatisticDataMerger.TransactionLevel.TransactionType,
                TransactionStatisticDataMerger.StatisticGroupType.Server);

        for (TransactionReportVO report : reports) {
            merger.add(report);
        }

        TransactionStatisticReport report = merger.toTransactionStatisticReport();
        DataUtils.checkForStatus(report, status);
        return report;
    }

    /**
     * 获取指定小时的TransactionType服务统计结果,根据服务端IP进行分组,不进行分页
     *
     * @param group         系统类别
     * @param serverAppName 应用服务系统名称
     * @param hour          指定小时,格式为 yyyy-MM-dd HH:mm:ss
     * @return 统计数据结果集
     */
    @Override
    public TransactionStatisticReport queryHourTransactionTypeReportByServer(String group,
                                                                             String serverAppName,
                                                                             String hour,
                                                                             String clientAppName) {
        // 需要从Mongodb的TransactionHourlyReports中获取
        // TransactionHourlyReports格式和TransactionRealtimeReports格式一样

        String startTime = hour.replace(hour.substring(14, 19), "00:00");
        String endTime = hour.substring(0, 14) + "59:59";

        Map<String, Object> map = new HashMap<>();
        map.put("domain", serverAppName);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        map.put("clientAppName", clientAppName);

        List<TransactionReportVO> reports =
                this.transactionDataStorage.querySpecifiedHourTransactionReports(group, map);

        DataUtils.removeUnmatched(reports, clientAppName);

        TransactionStatisticDataMerger merger = new TransactionStatisticDataMerger(serverAppName,
                TransactionStatisticDataMerger.TransactionLevel.TransactionType,
                TransactionStatisticDataMerger.StatisticGroupType.Server);

        for (TransactionReportVO report : reports) {
            merger.add(report);
        }

        TransactionStatisticReport report = merger.toTransactionStatisticReport();
        return report;

    }

    /**
     * 获取指定日期的TransactionType服务统计结果,根据服务端IP进行分组,不进行分页
     *
     * @param group         系统类别
     * @param serverAppName 应用服务系统名称
     * @param date          指定日期,格式为 yyyy-MM-dd
     * @return 统计数据结果集
     */
    @Override
    public TransactionStatisticReport queryDayTransactionTypeReportByServer(String group,
                                                                            String serverAppName,
                                                                            String date,
                                                                            String status,
                                                                            String clientAppName) {

        String startTime = date + " " + "00:00:00";
        String endTime = date + " " + "23:59:59";

        List<TransactionReportVO> reports =
                this.transactionDataStorage.queryHistoryTransactionReports(group, serverAppName, clientAppName, startTime, endTime,
                        TransactionReportType.DAILY);

        DataUtils.removeUnmatched(reports, clientAppName);

        TransactionStatisticDataMerger merger = new TransactionStatisticDataMerger(serverAppName,
                TransactionStatisticDataMerger.TransactionLevel.TransactionType,
                TransactionStatisticDataMerger.StatisticGroupType.Server);

        for (TransactionReportVO report : reports) {
            merger.add(report);
        }

        TransactionStatisticReport report = merger.toTransactionStatisticReport();
        DataUtils.checkForStatus(report, status);
        return report;

    }

    /**
     * 获取指定周的TransactionType服务统计结果,根据服务端IP进行分组,不进行分页
     *
     * @param group         系统类别
     * @param serverAppName 应用服务系统名称
     * @param week          指定周的第一天日期,格式为 yyyy-MM-dd
     * @return 统计数据结果集
     */
    @Override
    public TransactionStatisticReport queryWeekTransactionTypeReportByServer(String group,
                                                                             String serverAppName,
                                                                             String week,
                                                                             String status,
                                                                             String clientAppName) {
        HashMap<String, String> weeklyTime = getWeeklyDay(week);

        String startTime = weeklyTime.get("Monday");
        String endTime = weeklyTime.get("Sunday");
        List<TransactionReportVO> reports =
                this.transactionDataStorage.queryHistoryTransactionReports(group, serverAppName, clientAppName, startTime, endTime,
                        TransactionReportType.WEEKLY);

        DataUtils.removeUnmatched(reports, clientAppName);

        TransactionStatisticDataMerger merger = new TransactionStatisticDataMerger(serverAppName,
                TransactionStatisticDataMerger.TransactionLevel.TransactionType,
                TransactionStatisticDataMerger.StatisticGroupType.Server);

        for (TransactionReportVO report : reports) {
            merger.add(report);
        }

        TransactionStatisticReport report = merger.toTransactionStatisticReport();
        DataUtils.checkForStatus(report, status);
        return report;

    }

    /**
     * 获取指定月的TransactionType服务统计结果,根据服务端IP进行分组,不进行分页
     *
     * @param group         系统类别
     * @param serverAppName 应用服务系统名称
     * @param month         指定月份的第一条日期,格式为 yyyy-MM-dd
     * @return 统计数据结果集
     */
    @Override
    public TransactionStatisticReport queryMonthTransactionTypeReportByServer(String group,
                                                                              String serverAppName,
                                                                              String month,
                                                                              String clientAppName) {


        String startTime = getMonthFirstDay(month) + " 00:00:00";
        String endTime = getMonthLastDay(month) + " 23:59:59";
        List<TransactionReportVO> reports =
                this.transactionDataStorage.queryHistoryTransactionReports(group, serverAppName, clientAppName, startTime, endTime,
                        TransactionReportType.MONTHLY);

        DataUtils.removeUnmatched(reports, clientAppName);

        TransactionStatisticDataMerger merger = new TransactionStatisticDataMerger(serverAppName,
                TransactionStatisticDataMerger.TransactionLevel.TransactionType,
                TransactionStatisticDataMerger.StatisticGroupType.Server);

        for (TransactionReportVO report : reports) {
            merger.add(report);
        }

        TransactionStatisticReport report = merger.toTransactionStatisticReport();
        return report;

    }

    /**
     * 获取最近一小时的TransactionType服务对应的消费者统计结果,根据客户端应用名称进行分组,不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机
     * @return 统计数据结果集
     */
    @Override
    public TransactionStatisticReport queryLastHourTransactionTypeReportByClient(String group,
                                                                                 String serverAppName,
                                                                                 String transactionTypeName,
                                                                                 String serverIpAddress,
                                                                                 String clientAppName) {
        Map<String, Object> map = new HashMap<>();
        map.put("domain", serverAppName);
        map.put("startTime", getStartTime(DAY_TYPE.CURRENT));
        map.put("transactionType", transactionTypeName);

        if (!StringUtils.isEmpty(serverIpAddress)) {
            map.put("serverIp", serverIpAddress);
        }
        if (!StringUtils.isEmpty(clientAppName)) {
            map.put("clientAppName", clientAppName);
        }

        //获取当前一小时的实时数据
        List<TransactionReportVO> reports =
                this.transactionDataStorage.queryRealtimeTransactionReportsBySOC(group, map);

        DataUtils.removeUnmatched(reports, clientAppName);

        TransactionStatisticDataClientMerger merger = new TransactionStatisticDataClientMerger(serverAppName,
                TransactionStatisticDataClientMerger.TransactionLevel.TransactionType,
                serverIpAddress, transactionTypeName);

        for (TransactionReportVO report : reports) {
            merger.add(report);
        }

        TransactionStatisticReport report = merger.toTransactionStatisticReport();
        convertAppName(report);
        return report;
    }

    /**
     * 服务管理平台-转换系统名称
     * @param report
     */
    private void convertAppName(TransactionStatisticReport report) {
        List<String> appIds = new ArrayList<>();
        List<TransactionStatisticData> dataList = report.getTransactionStatisticDatas();
        for (TransactionStatisticData statisticData : dataList) {
            appIds.add(statisticData.getClientAppId());
        }
        Map<String, String> appIdNameMap =
                ListUtils.isEmpty(appIds) ? null : appInfoService.mapAppIdName(appIds);

        for (TransactionStatisticData statisticData : dataList){
            String key = statisticData.getClientAppId();
            statisticData.setClientAppName(appIdNameMap.get(key));
        }
    }


    /**
     * 获取指定小时的TransactionType服务对应的消费者统计结果,根据客户端应用名称进行分组,不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param hour                指定小时,格式为 yyyy-MM-dd HH:mm:ss
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机
     * @return 统计数据结果集
     */
    @Override
    public TransactionStatisticReport queryHourTransactionTypeReportByClient(String group,
                                                                             String serverAppName,
                                                                             String hour,
                                                                             String transactionTypeName,
                                                                             String serverIpAddress,
                                                                             String clientAppName) {
        String startTime = hour.replace(hour.substring(14, 19), "00:00");
        String endTime = hour.substring(0, 14) + "59:59";

        Map<String, Object> map = new HashMap<>();

        map.put("transactionType", transactionTypeName);

        if (!StringUtils.isEmpty(serverIpAddress)) {
            map.put("serverIp", serverIpAddress);
        }
        if (!StringUtils.isEmpty(clientAppName)) {
            map.put("clientAppName", clientAppName);
        }

        //获取当前一小时的实时数据
        List<TransactionReportVO> reports =
                this.transactionDataStorage.queryHistoryTransactionReports(group, serverAppName, startTime, endTime, TransactionReportType.HOURLY, map);

        DataUtils.removeUnmatched(reports, clientAppName);

        TransactionStatisticDataClientMerger merger = new TransactionStatisticDataClientMerger(serverAppName,
                TransactionStatisticDataClientMerger.TransactionLevel.TransactionType,
                serverIpAddress, transactionTypeName);

        for (TransactionReportVO report : reports) {
            merger.add(report);
        }

        TransactionStatisticReport report = merger.toTransactionStatisticReport();
        convertAppName(report);
        return report;
    }

    /**
     * 获取当天的TransactionType服务对应的消费者统计结果,根据客户端应用名称进行分组,不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机
     * @return 统计数据结果集
     */
    @Override
    public TransactionStatisticReport queryTodayTransactionTypeReportByClient(String group,
                                                                              String serverAppName,
                                                                              String transactionTypeName,
                                                                              String serverIpAddress,
                                                                              String clientAppName) {
        Map<String, Object> map = new HashMap<>();
        map.put("transactionType", transactionTypeName);
        if (!StringUtils.isEmpty(serverIpAddress)) {
            map.put("serverIp", serverIpAddress);
        }
        if (!StringUtils.isEmpty(serverAppName)) {
            map.put("domain", serverAppName);
        }
        if (!StringUtils.isEmpty(clientAppName)) {
            map.put("clientAppName", clientAppName);
        }
        map.put("startTime", getStartTime(DAY_TYPE.TODAY));
        map.put("endTime", getEndTime(DAY_TYPE.TODAY));

        //获取指定时间的实时数据
        List<TransactionReportVO> reports =
                this.transactionDataStorage.queryRealtimeTransactionReportsBySOC(group, map);

        DataUtils.removeUnmatched(reports, clientAppName);

        TransactionStatisticDataClientMerger merger = new TransactionStatisticDataClientMerger(serverAppName,
                TransactionStatisticDataClientMerger.TransactionLevel.TransactionType,
                serverIpAddress, transactionTypeName);

        for (TransactionReportVO report : reports) {
            merger.add(report);
        }

        TransactionStatisticReport report = merger.toTransactionStatisticReport();
        convertAppName(report);
        return report;
    }

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
    @Override
    public TransactionStatisticReport queryDayTransactionTypeReportByClient(String group,
                                                                            String serverAppName,
                                                                            String date,
                                                                            String transactionTypeName,
                                                                            String serverIpAddress,
                                                                            String clientAppName) {

        String startTime = date + " " + "00:00:00";
        String endTime = date + " " + "23:59:59";

        Map<String, Object> map = new HashMap<>();
        map.put("transactionType", transactionTypeName);

        if (!StringUtils.isEmpty(serverIpAddress)) {
            map.put("serverIp", serverIpAddress);
        }
        if (!StringUtils.isEmpty(clientAppName)) {
            map.put("clientAppName", clientAppName);
        }

        //获取当前一小时的实时数据
        List<TransactionReportVO> reports =
                this.transactionDataStorage.queryHistoryTransactionReports(group, serverAppName, startTime, endTime,
                        TransactionReportType.DAILY, map);

        DataUtils.removeUnmatched(reports, clientAppName);

        TransactionStatisticDataClientMerger merger = new TransactionStatisticDataClientMerger(serverAppName,
                TransactionStatisticDataClientMerger.TransactionLevel.TransactionType,
                serverIpAddress, transactionTypeName);

        for (TransactionReportVO report : reports) {
            merger.add(report);
        }

        TransactionStatisticReport report = merger.toTransactionStatisticReport();
        convertAppName(report);
        return report;
    }


    /**
     * 获取指定周的TransactionType服务对应的消费者统计结果,根据客户端应用名称进行分组,不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param week                指定周的第一天日期,格式为 yyyy-MM-dd
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机
     * @return 统计数据结果集
     */
    @Override
    public TransactionStatisticReport queryWeekTransactionTypeReportByClient(String group,
                                                                             String serverAppName,
                                                                             String week,
                                                                             String transactionTypeName,
                                                                             String serverIpAddress,
                                                                             String clientAppName) {

        HashMap<String, String> weeklyTime = getWeeklyDay(week);

        String startTime = weeklyTime.get("Monday");
        String endTime = weeklyTime.get("Sunday");

        Map<String, Object> map = new HashMap<>();
        map.put("transactionType", transactionTypeName);

        if (!StringUtils.isEmpty(serverIpAddress)) {
            map.put("serverIp", serverIpAddress);
        }
        if (!StringUtils.isEmpty(clientAppName)) {
            map.put("clientAppName", clientAppName);
        }

        //获取当前一小时的实时数据
        List<TransactionReportVO> reports =
                this.transactionDataStorage.queryHistoryTransactionReports(group, serverAppName, startTime, endTime,
                        TransactionReportType.WEEKLY, map);

        DataUtils.removeUnmatched(reports, clientAppName);

        TransactionStatisticDataClientMerger merger = new TransactionStatisticDataClientMerger(serverAppName,
                TransactionStatisticDataClientMerger.TransactionLevel.TransactionType,
                serverIpAddress, transactionTypeName);

        for (TransactionReportVO report : reports) {
            merger.add(report);
        }

        TransactionStatisticReport report = merger.toTransactionStatisticReport();
        convertAppName(report);
        return report;
    }


    /**
     * 获取指定周的TransactionType服务对应的消费者统计结果,根据客户端应用名称进行分组,不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param month               指定月份的第一条日期,格式为 yyyy-MM-dd
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机
     * @return 统计数据结果集
     */
    @Override
    public TransactionStatisticReport queryMonthTransactionTypeReportByClient(String group,
                                                                              String serverAppName,
                                                                              String month,
                                                                              String transactionTypeName,
                                                                              String serverIpAddress,
                                                                              String clientAppName) {


        String startTime = getMonthFirstDay(month) + " 00:00:00";
        String endTime = getMonthLastDay(month) + " 23:59:59";

        Map<String, Object> map = new HashMap<>();
        map.put("transactionType", transactionTypeName);

        if (!StringUtils.isEmpty(serverIpAddress)) {
            map.put("serverIp", serverIpAddress);
        }
        if (!StringUtils.isEmpty(clientAppName)) {
            map.put("clientAppName", clientAppName);
        }

        //获取当前一小时的实时数据
        List<TransactionReportVO> reports =
                this.transactionDataStorage.queryHistoryTransactionReports(group, serverAppName, startTime, endTime,
                        TransactionReportType.MONTHLY, map);

        DataUtils.removeUnmatched(reports, clientAppName);

        TransactionStatisticDataClientMerger merger = new TransactionStatisticDataClientMerger(serverAppName,
                TransactionStatisticDataClientMerger.TransactionLevel.TransactionType,
                serverIpAddress, transactionTypeName);

        for (TransactionReportVO report : reports) {
            merger.add(report);
        }

        TransactionStatisticReport report = merger.toTransactionStatisticReport();
        convertAppName(report);
        return report;
    }


    /**
     * 获取最近一小时的TransactionType调用次数的结果集,不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机总和
     * @return 调用次数结果集, 返回对象中durations的总长度为60, Key值为0-59,表示一小时从第0分钟到第59分钟的每分钟调用次数
     */
    @Override
    public TransactionCallTimesReport queryLastHourTransactionTypeCallTimesReportByServer(String group,
                                                                                          String serverAppName,
                                                                                          String transactionTypeName,
                                                                                          String serverIpAddress,
                                                                                          String clientAppName) {
        Map<String, Object> map = new HashMap<>();
        map.put("domain", serverAppName);
        map.put("startTime", getStartTime(DAY_TYPE.CURRENT));
        map.put("endTime", getEndTime(DAY_TYPE.CURRENT));
        map.put("transactionType", transactionTypeName);

        if (!StringUtils.isEmpty(serverIpAddress)) {
            map.put("serverIp", serverIpAddress);
        }
        if (!StringUtils.isEmpty(clientAppName)) {
            map.put("clientAppName", clientAppName);
        }

        //获取当前一小时的实时数据
        List<TransactionReportVO> reports =
                this.transactionDataStorage.queryRealtimeTransactionReportsBySOC(group, map);

        DataUtils.removeUnmatched(reports, clientAppName);

        TransactionCallTimesMerger transactionCallTimesMerger =
                new TransactionCallTimesMerger(serverAppName, serverIpAddress, transactionTypeName);

        for (TransactionReportVO report : reports) {
            transactionCallTimesMerger.add(report);
        }

        TransactionCallTimesReport transactionCallTimesReport = new TransactionCallTimesReport();
        LinkedHashMap<String, Long> durations = new LinkedHashMap<>();
        LinkedHashMap<Integer, Range2> range2sMap = transactionCallTimesMerger.getRange2s();
        transactionCallTimesReport.setDurations(durations);

        for (int i = 0; i < 60; i++) {
            Range2 range2 = range2sMap.get(i);
            if (range2 == null) {
                durations.put(String.valueOf(i), 0L);
            } else {
                durations.put(String.valueOf(i), (long) range2.getCount());
            }
        }

        return transactionCallTimesReport;
    }


    /**
     * 获取指定小时的TransactionType调用次数的结果集,不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param hour                指定小时,格式为 yyyy-MM-dd HH:mm:ss
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机总和
     * @return 调用次数结果集, 返回对象中durations的总长度为60, Key值为0-59,表示一小时从第0分钟到第59分钟的每分钟调用次数
     */
    @Override
    public TransactionCallTimesReport queryHourTransactionTypeCallTimesReportByServer(String group,
                                                                                      String serverAppName,
                                                                                      String hour,
                                                                                      String transactionTypeName,
                                                                                      String serverIpAddress,
                                                                                      String clientAppName) {


        String startTime = hour.replace(hour.substring(14, 19), "00:00");
        String endTime = hour.substring(0, 14) + "59:59";


        Map<String, Object> map = new HashMap<>();
        map.put("transactionType", transactionTypeName);

        if (!StringUtils.isEmpty(serverIpAddress)) {
            map.put("serverIp", serverIpAddress);
        }
        if (!StringUtils.isEmpty(clientAppName)) {
            map.put("clientAppName", clientAppName);
        }

        //获取当前一小时的实时数据
        List<TransactionReportVO> reports =
                this.transactionDataStorage.queryHistoryTransactionReports(group, serverAppName, startTime, endTime, TransactionReportType.HOURLY, map);

        DataUtils.removeUnmatched(reports, clientAppName);

        TransactionCallTimesMerger transactionCallTimesMerger =
                new TransactionCallTimesMerger(serverAppName, serverIpAddress, transactionTypeName);

        for (TransactionReportVO report : reports) {
            transactionCallTimesMerger.add(report);
        }

        TransactionCallTimesReport transactionCallTimesReport = new TransactionCallTimesReport();
        LinkedHashMap<String, Long> durations = new LinkedHashMap<>();
        LinkedHashMap<Integer, Range2> range2sMap = transactionCallTimesMerger.getRange2s();
        transactionCallTimesReport.setDurations(durations);

        for (int i = 0; i < 60; i++) {
            Range2 range2 = range2sMap.get(i);
            if (range2 == null) {
                durations.put(String.valueOf(i), 0L);
            } else {
                durations.put(String.valueOf(i), (long) range2.getCount());
            }
        }

        return transactionCallTimesReport;
    }

    /**
     * 获取当天的TransactionType调用次数的结果集,不进行分页
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称
     * @param transactionTypeName 服务大类名称
     * @param serverIpAddress     应用服务端的IP地址,如果传空,表示所有主机总和
     * @return 调用次数结果集, 返回对象中durations的总长度为24, Key值为0-23,表示一天从0点到23点的每小时调用次数
     */
    @Override
    public TransactionCallTimesReport queryTodayTransactionTypeCallTimesReportByServer(String group,
                                                                                       String serverAppName,
                                                                                       String transactionTypeName,
                                                                                       String serverIpAddress,
                                                                                       String clientAppName) {
        Map<String, Object> map = new HashMap<>();
        map.put("transactionType", transactionTypeName);
        if (!StringUtils.isEmpty(serverIpAddress)) {
            map.put("serverIp", serverIpAddress);
        }
        if (!StringUtils.isEmpty(serverAppName)) {
            map.put("domain", serverAppName);
        }
        if (!StringUtils.isEmpty(clientAppName)) {
            map.put("clientAppName", clientAppName);
        }

        map.put("startTime", getStartTime(DAY_TYPE.TODAY));
        map.put("endTime", getEndTime(DAY_TYPE.TODAY));

        List<TransactionReportVO> reports =
                this.transactionDataStorage.queryRealtimeTransactionReportsBySOC(group, map);

        DataUtils.removeUnmatched(reports, clientAppName);

        TransactionCallTimesMerger merger = new TransactionCallTimesMerger(serverAppName, serverIpAddress, transactionTypeName);

        for (TransactionReportVO report : reports) {
            merger.add(report);
        }

        LinkedHashMap<String, Long> durations = new LinkedHashMap<>();
        for (int i = 0; i < 24; i++) {
            durations.put(String.valueOf(i), 0L);
        }

        for (TransactionReportVO report : reports) {
            int hour = Integer.parseInt(report.getStartTime().substring(11, 13));
            for (TransactionMachineVO machine : report.getMachines()) {
                if (!StringUtils.isEmpty(serverIpAddress) &&
                        !machine.getIp().equals(serverIpAddress)) {
                    continue;
                }
                for (TransactionClientVO client : machine.getTransactionClients()) {
                    for (TransactionTypeVO transactionType : client.getTransactionTypes()) {
                        if (!StringUtils.isEmpty(transactionTypeName) &&
                                !transactionType.getName().equals(transactionTypeName)) {
                            continue;
                        }

                        Long count = durations.get(String.valueOf(hour));
                        durations.put(String.valueOf(hour), transactionType.getTotalCount() + count);
                    }
                }
            }
        }

        TransactionCallTimesReport report = new TransactionCallTimesReport();
        report.setDurations(durations);

        return report;
    }

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
    @Override
    public TransactionCallTimesReport queryDayTransactionTypeCallTimesReportByServer(String group,
                                                                                     String serverAppName,
                                                                                     String date,
                                                                                     String transactionTypeName,
                                                                                     String serverIpAddress) {
        String startTime = date + " " + "00:00:00";
        String endTime = date + " " + "23:59:59";

        Map<String, Object> map = new HashMap<>();
        map.put("startTime", startTime);
        map.put("transactionType", transactionTypeName);

        if (!StringUtils.isEmpty(serverIpAddress)) {
            map.put("serverIp", serverIpAddress);
        }

        //获取当前一小时的实时数据
        List<TransactionReportVO> reports =
                this.transactionDataStorage.queryHistoryTransactionReports(group, serverAppName, startTime, endTime,
                        TransactionReportType.HOURLY, map);

        TransactionCallTimesMerger transactionCallTimesMerger =
                new TransactionCallTimesMerger(serverAppName, serverIpAddress, transactionTypeName);

        for (TransactionReportVO report : reports) {
            transactionCallTimesMerger.add(report);
        }


        LinkedHashMap<String, Long> durations = new LinkedHashMap<>();


        for (int i = 0; i < 24; i++) {
            durations.put(String.valueOf(i), 0L);
        }

        for (TransactionReportVO report : reports) {
            int hour = Integer.parseInt(report.getStartTime().substring(11, 13));
            for (TransactionMachineVO machine : report.getMachines()) {
                if (!StringUtils.isEmpty(serverIpAddress) &&
                        !machine.getIp().equals(serverIpAddress)) {
                    continue;
                }
                for (TransactionClientVO client : machine.getTransactionClients()) {
                    for (TransactionTypeVO transactionType : client.getTransactionTypes()) {
                        if (!StringUtils.isEmpty(transactionTypeName) &&
                                !transactionType.getName().equals(transactionTypeName)) {
                            continue;
                        }

                        Long count = durations.get(String.valueOf(hour));
                        durations.put(String.valueOf(hour), transactionType.getTotalCount() + count);
                    }
                }
            }
        }

        TransactionCallTimesReport report = new TransactionCallTimesReport();
        report.setDurations(durations);

        return report;
    }


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
    @Override
    public TransactionCallTimesReport queryWeekTransactionTypeCallTimesReportByServer(String group,
                                                                                      String serverAppName,
                                                                                      String week,
                                                                                      String transactionTypeName,
                                                                                      String serverIpAddress) {
        HashMap<String, String> weeklyTime = getWeeklyDay(week);

        String startTime = weeklyTime.get("Monday");
        String endTime = weeklyTime.get("Sunday");

        Map<String, Object> map = new HashMap<>();

        map.put("transactionType", transactionTypeName);

        if (!StringUtils.isEmpty(serverIpAddress)) {
            map.put("serverIp", serverIpAddress);
        }

        List<TransactionReportVO> reports =
                this.transactionDataStorage.queryHistoryTransactionReports(group, serverAppName, startTime, endTime,
                        TransactionReportType.DAILY, map);

        TransactionCallTimesMerger transactionCallTimesMerger =
                new TransactionCallTimesMerger(serverAppName, serverIpAddress, transactionTypeName);

        for (TransactionReportVO report : reports) {
            transactionCallTimesMerger.add(report);
        }


        LinkedHashMap<String, Long> durations = new LinkedHashMap<>();


        for (int i = 1; i < 8; i++) {
            durations.put(String.valueOf(i), 0L);
        }

        for (TransactionReportVO report : reports) {
            Calendar calendar = Calendar.getInstance();
            boolean isFirstSunday = (calendar.getFirstDayOfWeek() == Calendar.SUNDAY);
            calendar.set(Calendar.YEAR, Integer.parseInt(report.getStartTime().substring(0, 4)));
            calendar.set(Calendar.MONTH, Integer.parseInt(report.getStartTime().substring(5, 7)) - 1);
            calendar.set(Calendar.DATE, Integer.parseInt(report.getStartTime().substring(8, 10)));
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            if (isFirstSunday) {
                day = day - 1;
                if (day == 0) {
                    day = 7;
                }
            }
            for (TransactionMachineVO machine : report.getMachines()) {
                if (!StringUtils.isEmpty(serverIpAddress) &&
                        !machine.getIp().equals(serverIpAddress)) {
                    continue;
                }
                for (TransactionClientVO client : machine.getTransactionClients()) {
                    for (TransactionTypeVO transactionType : client.getTransactionTypes()) {
                        if (!StringUtils.isEmpty(transactionTypeName) &&
                                !transactionType.getName().equals(transactionTypeName)) {
                            continue;
                        }

                        Long count = durations.get(String.valueOf(day));
                        durations.put(String.valueOf(day), transactionType.getTotalCount() + count);
                    }
                }
            }
        }

        TransactionCallTimesReport report = new TransactionCallTimesReport();
        report.setDurations(durations);
        return report;
    }


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
    @Override
    public TransactionCallTimesReport queryMonthTransactionTypeCallTimesReportByServer(String group,
                                                                                       String serverAppName,
                                                                                       String month,
                                                                                       String transactionTypeName,
                                                                                       String serverIpAddress) {

        String startTime = getMonthFirstDay(month) + " 00:00:00";
        String endTime = getMonthLastDay(month) + " 23:59:59";

        Map<String, Object> map = new HashMap<>();

        map.put("transactionType", transactionTypeName);

        if (!StringUtils.isEmpty(serverIpAddress)) {
            map.put("serverIp", serverIpAddress);
        }

        //获取当前一小时的实时数据
        List<TransactionReportVO> reports =
                this.transactionDataStorage.queryHistoryTransactionReports(group, serverAppName, startTime, endTime,
                        TransactionReportType.DAILY, map);

        TransactionCallTimesMerger transactionCallTimesMerger =
                new TransactionCallTimesMerger(serverAppName, serverIpAddress, transactionTypeName);

        for (TransactionReportVO report : reports) {
            transactionCallTimesMerger.add(report);
        }


        LinkedHashMap<String, Long> durations = new LinkedHashMap<>();

        for (TransactionReportVO report : reports) {
            int day = Integer.parseInt(report.getStartTime().substring(8, 10));
            for (TransactionMachineVO machine : report.getMachines()) {
                if (!StringUtils.isEmpty(serverIpAddress) &&
                        !machine.getIp().equals(serverIpAddress)) {
                    continue;
                }
                for (TransactionClientVO client : machine.getTransactionClients()) {
                    for (TransactionTypeVO transactionType : client.getTransactionTypes()) {
                        if (!StringUtils.isEmpty(transactionTypeName) &&
                                !transactionType.getName().equals(transactionTypeName)) {
                            continue;
                        }

                        durations.put(String.valueOf(day), transactionType.getTotalCount());
                    }
                }
            }
        }
        int days = Integer.parseInt(endTime.substring(8, 10).trim());
        for (int i = 1; i <= days; i++) {
            if (durations.containsKey(String.valueOf(i))) {
                continue;
            } else {
                durations.put(String.valueOf(i), (long) 0);
            }
        }
        TransactionCallTimesReport report = new TransactionCallTimesReport();
        report.setDurations(durations);
        return report;
    }

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
     * @param status              过滤消息状态,可选,可填成功或失败,不填表示所有状态记录
     * @param startIndex          分页起始位置,非空
     * @param pageSize            分页每页的条数,非空
     * @param previousIndexes     排序参数, key表示需要排序的字段,value表示排序顺序,DESC或ASC,且要按照顺序,不填则不进行排序
     * @return 详细调用Transaction的明细清单
     */
    @Override
    public TransactionMessageList queryLastHourTransactionMessageList(String group, String serverAppName,
                                                                      String transactionTypeName,
                                                                      String transactionName,
                                                                      String serverIpAddress,
                                                                      String clientAppName,
                                                                      String clientIpAddress,
                                                                      String status, String keyWords,
                                                                      String limitStartTime, String limitEndTime,
                                                                      int startIndex, int pageSize, String durationTop,
                                                                      Map<String, Object> previousIndexes) {

        Map<String, Object> map = this.getArgumentMap(transactionTypeName, transactionName,
                serverIpAddress, clientAppName, clientIpAddress, status);

        map.put("keyWords", keyWords);
        map.put("limitStartTime", limitStartTime);
        map.put("limitEndTime", limitEndTime);
        map.put("durationTop", durationTop);

        MessageTreeList messageList = this.messageTreeStorage.queryMessageTree(group, serverAppName, TransactionReportType.REALTIME, null, map,
                startIndex, pageSize, previousIndexes);

        TransactionMessageList transactionMessageList = new TransactionMessageList();
        transactionMessageList.setTotalSize(messageList.getTotalSize());

        //获取业务系统的系统代码与名称的映射
        Map<String, String> appIdNameConsumerMap;
        Map<String, SvcInfoModel> svcCodeObjMap;
        if (messageList.getTotalSize() > 0) {
            List<String> appIdProviders = new ArrayList<>();
            List<String> appIdConsumers = new ArrayList<>();
            for (MessageTree messageTree : messageList.getMessageTrees()) {
                //获取提供方业务系统信息
                if(!appIdProviders.contains(messageTree.getDomain())) {
                    appIdProviders.add(messageTree.getDomain());
                }
                //获取消费方业务系统信息
                if (messageTree.getCaller() != null && !StringUtils.isEmpty(messageTree.getCaller().getName())
                        && !appIdConsumers.contains(messageTree.getCaller().getName())) {
                    appIdConsumers.add(messageTree.getCaller().getName());
                }
            }
            //获取消费方系统信息
            appIdNameConsumerMap = ListUtils.isEmpty(appIdConsumers) ? null : appInfoService.mapAppIdName(appIdConsumers);

            //获取提供方系统包含的服务信息
            List<SvcInfoModel> svcInfoModels = svcInfoService.getByAppId(appIdProviders);
            svcCodeObjMap = svcInfoService.listToMapCodeObject(svcInfoModels);
        } else {
            appIdNameConsumerMap = null;
            svcCodeObjMap = null;
        }

        for (MessageTree messageTree : messageList.getMessageTrees()) {
            TransactionMessage transactionMessage = this.toTransactionMessage(messageTree,appIdNameConsumerMap,svcCodeObjMap);
            transactionMessageList.addTransactionMessage(transactionMessage);
        }

        return transactionMessageList;
    }


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
     * @param status              过滤消息状态,可选,可填成功或失败,不填表示所有状态记录
     * @param startIndex          分页起始位置,非空
     * @param pageSize            分页每页的条数,非空
     * @param orderBy             排序参数, key表示需要排序的字段,value表示排序顺序,DESC或ASC,且要按照顺序,不填则不进行排序
     * @return 详细调用Transaction的明细清单
     */
    @Override
    public TransactionMessageList queryHourTransactionMessageList(String group, String serverAppName,
                                                                  String hour, String transactionTypeName,
                                                                  String transactionName, String serverIpAddress,
                                                                  String clientAppName, String clientIpAddress,
                                                                  String status, String keyWords,
                                                                  int startIndex, int pageSize, String durationTop,
                                                                  Map<String, Object> orderBy) {

        Map<String, Object> map = this.getArgumentMap(transactionTypeName, transactionName,
                serverIpAddress, clientAppName, clientIpAddress, status);
        map.put("keyWords", keyWords);
        map.put("durationTop", durationTop);

        MessageTreeList messageList = this.messageTreeStorage.queryMessageTree(group, serverAppName,
                TransactionReportType.HOUR_IN_TODAY, hour, map, startIndex, pageSize, orderBy);

        TransactionMessageList transactionMessageList = new TransactionMessageList();
        transactionMessageList.setTotalSize(messageList.getTotalSize());

        //获取业务系统的系统代码与名称的映射
        Map<String, String> appIdNameConsumerMap;
        Map<String, SvcInfoModel> svcCodeObjMap;
        if (messageList.getTotalSize() > 0) {
            List<String> appIdProviders = new ArrayList<>();
            List<String> appIdConsumers = new ArrayList<>();
            for (MessageTree messageTree : messageList.getMessageTrees()) {
                //获取提供方业务系统信息
                if(!appIdProviders.contains(messageTree.getDomain())) {
                    appIdProviders.add(messageTree.getDomain());
                }
                //获取消费方业务系统信息
                if (messageTree.getCaller() != null && !StringUtils.isEmpty(messageTree.getCaller().getName())
                        && !appIdConsumers.contains(messageTree.getCaller().getName())) {
                    appIdConsumers.add(messageTree.getCaller().getName());
                }
            }
            //获取消费方系统信息
            appIdNameConsumerMap = ListUtils.isEmpty(appIdConsumers) ? null : appInfoService.mapAppIdName(appIdConsumers);

            //获取提供方系统包含的服务信息
            List<SvcInfoModel> svcInfoModels = svcInfoService.getByAppId(appIdProviders);
            svcCodeObjMap = svcInfoService.listToMapCodeObject(svcInfoModels);
        } else {
            appIdNameConsumerMap = null;
            svcCodeObjMap = null;
        }

        for (MessageTree messageTree : messageList.getMessageTrees()) {
            TransactionMessage transactionMessage = this.toTransactionMessage(messageTree,appIdNameConsumerMap,svcCodeObjMap);
            transactionMessageList.addTransactionMessage(transactionMessage);
        }

        return transactionMessageList;
    }

    /**
     * 获取当天的调用消息明细记录
     *
     * @param group               系统类别
     * @param serverAppName       应用服务系统名称,非空
     * @param transactionTypeName 服务大类名称,非空
     * @param transactionName     服务名称,可选
     * @param serverIpAddress     服务端系统IP地址,可选,不填表示所有服务端主机
     * @param clientAppName       客户端系统名称,可选,不填表示所有客户端系统
     * @param clientIpAddress     客户端系统IP地址,可选,不填表示所有客户端主机
     * @param status              过滤消息状态,可选,可填成功或失败,不填表示所有状态记录
     * @param startIndex          分页起始位置,非空
     * @param pageSize            分页每页的条数,非空
     * @param previousIndexes     排序参数, key表示需要排序的字段,value表示排序顺序,DESC或ASC,且要按照顺序,不填则不进行排序
     * @return 详细调用Transaction的明细清单
     */
    @Override
    public TransactionMessageList queryTodayTransactionMessageList(String group,
                                                                   String serverAppName,
                                                                   String transactionTypeName,
                                                                   String transactionName,
                                                                   String serverIpAddress,
                                                                   String clientAppName,
                                                                   String clientIpAddress,
                                                                   String status, String keyWords, String inputIP,
                                                                   String limitStartTime, String limitEndTime,
                                                                   int startIndex, int pageSize, String durationTop,
                                                                   Map<String, Object> previousIndexes) {

        Map<String, Object> map = this.getArgumentMap(transactionTypeName, transactionName,
                serverIpAddress, clientAppName, clientIpAddress, status);

        map.put("keyWords", keyWords);
        map.put("inputIP", inputIP);
        map.put("limitStartTime", limitStartTime);
        map.put("limitEndTime", limitEndTime);
        map.put("durationTop", durationTop);

        MessageTreeList messageList = this.messageTreeStorage.queryMessageTree(group, serverAppName,
                TransactionReportType.TODAY, null, map, startIndex, pageSize, previousIndexes);

        //获取业务系统的系统代码与名称的映射
        Map<String, String> appIdNameConsumerMap;
        Map<String, SvcInfoModel> svcCodeObjMap;
        if (messageList.getTotalSize() > 0) {
            List<String> appIdProviders = new ArrayList<>();
            List<String> appIdConsumers = new ArrayList<>();
            for (MessageTree messageTree : messageList.getMessageTrees()) {
                //获取提供方业务系统信息
                if(!appIdProviders.contains(messageTree.getDomain())) {
                    appIdProviders.add(messageTree.getDomain());
                }
                //获取消费方业务系统信息
                if (messageTree.getCaller() != null && !StringUtils.isEmpty(messageTree.getCaller().getName())
                        && !appIdConsumers.contains(messageTree.getCaller().getName())) {
                    appIdConsumers.add(messageTree.getCaller().getName());
                }
            }
            //获取消费方系统信息
            appIdNameConsumerMap = ListUtils.isEmpty(appIdConsumers) ? null : appInfoService.mapAppIdName(appIdConsumers);

            //获取提供方系统包含的服务信息
            List<SvcInfoModel> svcInfoModels = svcInfoService.getByAppId(appIdProviders);
            svcCodeObjMap = svcInfoService.listToMapCodeObject(svcInfoModels);
        } else {
            appIdNameConsumerMap = null;
            svcCodeObjMap = null;
        }

        //生成返回对象
        TransactionMessageList transactionMessageList = new TransactionMessageList();
        transactionMessageList.setTotalSize(messageList.getTotalSize());

        for (MessageTree messageTree : messageList.getMessageTrees()) {
            TransactionMessage transactionMessage = this.toTransactionMessage(messageTree, appIdNameConsumerMap, svcCodeObjMap);
            transactionMessageList.addTransactionMessage(transactionMessage);
        }

        return transactionMessageList;
    }

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
    @Override
    public TransactionMessageList queryDayTransactionMessageList(String group,
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
                                                                 Map<String, Object> orderBy) {

        Map<String, Object> map = this.getArgumentMap(transactionTypeName, transactionName,
                serverIpAddress, clientAppName, clientIpAddress, status);
        map.put("keyWords", keyWords);

        MessageTreeList messageList = this.messageTreeStorage.queryMessageTree(group, serverAppName, TransactionReportType.DAILY, date, map,
                startIndex, pageSize, orderBy);

        //获取业务系统的系统代码与名称的映射
        Map<String, String> appIdNameConsumerMap;
        Map<String, SvcInfoModel> svcCodeObjMap;
        if (messageList.getTotalSize() > 0) {
            List<String> appIdProviders = new ArrayList<>();
            List<String> appIdConsumers = new ArrayList<>();
            for (MessageTree messageTree : messageList.getMessageTrees()) {
                //获取提供方业务系统信息
                if(!appIdProviders.contains(messageTree.getDomain())) {
                    appIdProviders.add(messageTree.getDomain());
                }
                //获取消费方业务系统信息
                if (messageTree.getCaller() != null && !StringUtils.isEmpty(messageTree.getCaller().getName())
                        && !appIdConsumers.contains(messageTree.getCaller().getName())) {
                    appIdConsumers.add(messageTree.getCaller().getName());
                }
            }
            //获取消费方系统信息
            appIdNameConsumerMap = ListUtils.isEmpty(appIdConsumers) ? null : appInfoService.mapAppIdName(appIdConsumers);

            //获取提供方系统包含的服务信息
            List<SvcInfoModel> svcInfoModels = svcInfoService.getByAppId(appIdProviders);
            svcCodeObjMap = svcInfoService.listToMapCodeObject(svcInfoModels);
        } else {
            appIdNameConsumerMap = null;
            svcCodeObjMap = null;
        }

        //生成返回对象
        TransactionMessageList transactionMessageList = new TransactionMessageList();
        transactionMessageList.setTotalSize(messageList.getTotalSize());

        for (MessageTree messageTree : messageList.getMessageTrees()) {
            TransactionMessage transactionMessage = this.toTransactionMessage(messageTree, appIdNameConsumerMap, svcCodeObjMap);
            transactionMessageList.addTransactionMessage(transactionMessage);
        }

        return transactionMessageList;
    }

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
    @Override
    public TransactionMessageList queryWeekTransactionMessageList(String group,
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
                                                                  Map<String, Object> orderBy) {

        Map<String, Object> map = this.getArgumentMap(transactionTypeName, transactionName,
                serverIpAddress, clientAppName, clientIpAddress, status);

        map.put("keyWords", keyWords);

        MessageTreeList messageList = this.messageTreeStorage.queryMessageTree(group, serverAppName, TransactionReportType.WEEKLY, week, map,
                startIndex, pageSize, orderBy);

        //获取业务系统的系统代码与名称的映射
        Map<String, String> appIdNameConsumerMap;
        Map<String, SvcInfoModel> svcCodeObjMap;
        if (messageList.getTotalSize() > 0) {
            List<String> appIdProviders = new ArrayList<>();
            List<String> appIdConsumers = new ArrayList<>();
            for (MessageTree messageTree : messageList.getMessageTrees()) {
                //获取提供方业务系统信息
                if(!appIdProviders.contains(messageTree.getDomain())) {
                    appIdProviders.add(messageTree.getDomain());
                }
                //获取消费方业务系统信息
                if (messageTree.getCaller() != null && !StringUtils.isEmpty(messageTree.getCaller().getName())
                        && !appIdConsumers.contains(messageTree.getCaller().getName())) {
                    appIdConsumers.add(messageTree.getCaller().getName());
                }
            }
            //获取消费方系统信息
            appIdNameConsumerMap = ListUtils.isEmpty(appIdConsumers) ? null : appInfoService.mapAppIdName(appIdConsumers);

            //获取提供方系统包含的服务信息
            List<SvcInfoModel> svcInfoModels = svcInfoService.getByAppId(appIdProviders);
            svcCodeObjMap = svcInfoService.listToMapCodeObject(svcInfoModels);
        } else {
            appIdNameConsumerMap = null;
            svcCodeObjMap = null;
        }

        //生成返回对象
        TransactionMessageList transactionMessageList = new TransactionMessageList();
        transactionMessageList.setTotalSize(messageList.getTotalSize());

        for (MessageTree messageTree : messageList.getMessageTrees()) {
            TransactionMessage transactionMessage = this.toTransactionMessage(messageTree, appIdNameConsumerMap, svcCodeObjMap);
            transactionMessageList.addTransactionMessage(transactionMessage);
        }

        return transactionMessageList;
    }

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
     * @param previous            排序参数, key表示需要排序的字段,value表示排序顺序,DESC或ASC,且要按照顺序,不填则不进行排序
     * @return 详细调用Transaction的明细清单
     */
    @Override
    public TransactionMessageList queryMonthTransactionMessageList(String group,
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
                                                                   Map<String, Object> previous) {

        Map<String, Object> map = this.getArgumentMap(transactionTypeName, transactionName,
                serverIpAddress, clientAppName, clientIpAddress, status);

        map.put("keyWords", keyWords);

        MessageTreeList messageList = this.messageTreeStorage.queryMessageTree(group, serverAppName, TransactionReportType.MONTHLY, month, map,
                startIndex, pageSize, previous);

        //获取业务系统的系统代码与名称的映射
        Map<String, String> appIdNameConsumerMap;
        Map<String, SvcInfoModel> svcCodeObjMap;
        if (messageList.getTotalSize() > 0) {
            List<String> appIdProviders = new ArrayList<>();
            List<String> appIdConsumers = new ArrayList<>();
            for (MessageTree messageTree : messageList.getMessageTrees()) {
                //获取提供方业务系统信息
                if(!appIdProviders.contains(messageTree.getDomain())) {
                    appIdProviders.add(messageTree.getDomain());
                }
                //获取消费方业务系统信息
                if (messageTree.getCaller() != null && !StringUtils.isEmpty(messageTree.getCaller().getName())
                        && !appIdConsumers.contains(messageTree.getCaller().getName())) {
                    appIdConsumers.add(messageTree.getCaller().getName());
                }
            }
            //获取消费方系统信息
            appIdNameConsumerMap = ListUtils.isEmpty(appIdConsumers) ? null : appInfoService.mapAppIdName(appIdConsumers);

            //获取提供方系统包含的服务信息
            List<SvcInfoModel> svcInfoModels = svcInfoService.getByAppId(appIdProviders);
            svcCodeObjMap = svcInfoService.listToMapCodeObject(svcInfoModels);
        } else {
            appIdNameConsumerMap = null;
            svcCodeObjMap = null;
        }

        //生成返回对象
        TransactionMessageList transactionMessageList = new TransactionMessageList();
        transactionMessageList.setTotalSize(messageList.getTotalSize());

        for (MessageTree messageTree : messageList.getMessageTrees()) {
            TransactionMessage transactionMessage = this.toTransactionMessage(messageTree, appIdNameConsumerMap, svcCodeObjMap);
            transactionMessageList.addTransactionMessage(transactionMessage);
        }

        return transactionMessageList;
    }

    @Override
    public TransactionMessageListDetail queryTransactionMessageListDetails(String group,
                                                                           String messageId,
                                                                           int index,
                                                                           String serverAppName) {
        MessageTreeList messageList = this.messageTreeStorage.queryMessageTree(group, messageId, serverAppName);

        TransactionMessageListDetail detail = new TransactionMessageListDetail();
        TransactionMessage transactionMessage = new TransactionMessage();
        if (index == -1) {
            for (MessageTree messageTree : messageList.getMessageTrees()) {
                DefaultTransaction transaction = (DefaultTransaction) messageTree.getMessage();
                DataUtils.setTransactionMessage(transaction, transactionMessage);
                detail.setData(transactionMessage.getDatas());
            }
        } else {
            for (MessageTree messageTree : messageList.getMessageTrees()) {
                DefaultTransaction transaction = (DefaultTransaction) messageTree.getMessage();
                if (transaction.getChildren() != null) {

                    LogMessage logMessage = transaction.getChildren().get(index);
                    DefaultTransaction childTransaction = (DefaultTransaction) logMessage;
                    DataUtils.setTransactionMessage(childTransaction, transactionMessage);
                    detail.setData(transactionMessage.getDatas());
                }
            }
        }

        return detail;
    }

    @Override
    public List<ServiceShowVO> getServiceFlowShow(String serverAppName, String messageId) {
        //获取流程显示的系统信息
        Map<Integer, ServiceShowVO> showVOMap = this.transactionDataStorage.getServiceFlowShow(serverAppName, messageId);
        //转为List
        List<ServiceShowVO> voList = ListUtils.transferToList(showVOMap.values());
        //获取系统代码，用于查询系统名称
        if (!ListUtils.isEmpty(voList)) {
            String appId;
            List<String> appIdList = new ArrayList<>();
            for (ServiceShowVO showVO : voList) {
                appId = showVO.getDomain();
                if (!appIdList.contains(appId)) {
                    appIdList.add(appId);
                }

                if (showVO.getMessageTree() != null && showVO.getMessageTree().getCaller() != null) {
                    appId = showVO.getMessageTree().getCaller().getName();
                    if (!StringUtils.isEmpty(appId) && !appIdList.contains(appId)) {
                        appIdList.add(appId);
                    }
                }
            }
            //获取系统代码与名称的映射Map
            Map<String, String> appIdNameMap = appInfoService.mapAppIdName(appIdList);
            //转为系统名称
            if (!MapUtils.isEmpty(appIdNameMap)) {
                for (ServiceShowVO showVO : voList) {
                    appId = showVO.getDomain();
                    if (appIdNameMap.containsKey(appId)) {
                        showVO.setDomain(appIdNameMap.get(appId));
                    }

                    if (showVO.getMessageTree() != null && showVO.getMessageTree().getCaller() != null) {
                        appId = showVO.getMessageTree().getCaller().getName();
                        if (!StringUtils.isEmpty(appId) && appIdNameMap.containsKey(appId)) {
                            showVO.getMessageTree().getCaller().setName(appIdNameMap.get(appId));
                        }
                    }
                }
            }
        }

        return voList;
    }

    private TransactionMessage toTransactionMessage(MessageTree messageTree, Map<String, String> appIdNameConsumerMap,
                                                    Map<String, SvcInfoModel> svcCodeObjMap) {
        DefaultTransaction transaction = (DefaultTransaction) messageTree.getMessage();
        TransactionMessage transactionMessage = this.toTransactionMessage(transaction);
        //设置消费方信息
        if (messageTree.getCaller() != null) {
            //如果appIdNameMap不为空，则将系统代码转为系统名称
            String clientAppId = messageTree.getCaller().getName();
            if (!MapUtils.isEmpty(appIdNameConsumerMap) && !StringUtils.isEmpty(clientAppId)
                    && appIdNameConsumerMap.containsKey(clientAppId)) {
                transactionMessage.setClientAppName(appIdNameConsumerMap.get(clientAppId));
            } else {
                transactionMessage.setClientAppName(clientAppId);
            }
            transactionMessage.setClientIpAddress(messageTree.getCaller().getIp());
            transactionMessage.setClientType(messageTree.getCaller().getType());
        } else {
            transactionMessage.setClientAppName("");
            transactionMessage.setClientIpAddress("");
            transactionMessage.setClientType("");
        }

        //设置服务名称
        String svcCode = transactionMessage.getTransactionTypeName();
        if (!StringUtils.isEmpty(svcCode) && !MapUtils.isEmpty(svcCodeObjMap) && svcCodeObjMap.containsKey(svcCode)) {
            transactionMessage.setSvcName(svcCodeObjMap.get(svcCode).getName());
        } else {
            transactionMessage.setSvcName(svcCode);
        }

        transactionMessage.setServerAppName(messageTree.getDomain());
        transactionMessage.setServerIpAddress(messageTree.getIpAddress());
        transactionMessage.setMessageId(messageTree.getMessageId());

        return transactionMessage;
    }

    private TransactionMessage toTransactionMessage(DefaultTransaction transaction) {
        TransactionMessage transactionMessage = new TransactionMessage();
        transactionMessage.setTimestamp(transaction.getTimestamp());
        transactionMessage.setStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new Date(transaction.getTimestamp())));
        transactionMessage.setTransactionTypeName(transaction.getType());
        transactionMessage.setTransactionName(transaction.getName());
        transactionMessage.setUseTime(transaction.getDurationInMillis());


        if (transaction.getData() != null) {
            for (Map.Entry<String, Object> entry : transaction.getData().entrySet()) {
                Object value = entry.getValue();
                if (value == null) {
                    transactionMessage.getDatas().put(entry.getKey(), "");
                } else {
                    transactionMessage.getDatas().put(entry.getKey(), value.toString());
                }
            }
        }

        if ("0".equals(transaction.getStatus())) {
            transactionMessage.setStatus("成功");
        } else {
            transactionMessage.setStatus("失败");
            transactionMessage.setErrorMessage(transactionMessage.getStatus());
        }

        if (transaction.getChildren() != null) {
            for (LogMessage logMessage : transaction.getChildren()) {
                DefaultTransaction childTransaction = (DefaultTransaction) logMessage;
                transactionMessage.addTransactionMessage(this.toTransactionMessage(childTransaction));
            }
        }

        return transactionMessage;
    }


    private Map<String, Object> getArgumentMap(String transactionTypeName,
                                               String transactionName,
                                               String serverIpAddress,
                                               String clientAppName,
                                               String clientIpAddress,
                                               String status) {

        Map<String, Object> map = new HashMap<>();
        if (!StringUtils.isEmpty(transactionTypeName)) {
            map.put("transactionTypeName", transactionTypeName);
        }
        if (!StringUtils.isEmpty(transactionName)) {
            map.put("transactionName", transactionName);
        }
        if (!StringUtils.isEmpty(serverIpAddress)) {
            map.put("serverIpAddress", serverIpAddress);
        }
        if (!StringUtils.isEmpty(clientAppName)) {
            map.put("clientAppName", clientAppName);
        }
        if (!StringUtils.isEmpty(clientIpAddress)) {
            map.put("clientIpAddress", clientIpAddress);
        }

        if ("执行成功".equals(status)) {
            map.put("status", "0");
        } else if ("执行失败".equals(status)) {
            map.put("status", "-1");
        } else {
            map.put("status", status);
        }

        return map;
    }


    @Override
    public List<RunningStatusUnPTVO> countUnPTRunningStatus(Map map) {
        if (map == null) {
            return null;
        }

        String startTime = null, endTime = null, time = null;
        if (map.containsKey(QueryParameterKeys.TIMETYPE.getKey())) {
            String timeType = map.get(QueryParameterKeys.TIMETYPE.getKey()).toString();
            if (map.containsKey("time")) {
                time = (String) map.get(QueryParameterKeys.TIME.getKey());
                /*if (o != null)
                    time = o.toString();*/
            }
            switch (timeType) {
                case "currentHour":
                    startTime = getStartTime(DAY_TYPE.CURRENT);
                    break;
                case "specifiedHour":
                    startTime = time.replace(time.substring(14, 19), "00:00");
                    endTime = time.substring(0, 14) + "59:59";
                    break;
                case "today":
                    Map<String, String> pointsTime = getPointsTimeByType(timeType);
                    startTime = pointsTime.get("startTime");
                    endTime = pointsTime.get("endTime");
                    break;
                case "day":
                    startTime = time + " " + "00:00:00";
                    endTime = time + " " + "23:59:59";
                    map.put("reportType", TransactionReportType.DAILY);
                    break;
                case "week":
                    HashMap<String, String> weeklyTime = getWeeklyDay(time);
                    startTime = weeklyTime.get("Monday");
                    endTime = weeklyTime.get("Sunday");
                    map.put("reportType", TransactionReportType.WEEKLY);
                    break;
                case "month":
                    startTime = getMonthFirstDay(time) + " 00:00:00";
                    endTime = getMonthLastDay(time) + " 23:59:59";
                    map.put("reportType", TransactionReportType.MONTHLY);
                    break;
            }
        }

        return this.transactionDataStorage.countUnPTRunningStatus(startTime, endTime, map);
    }

    @Override
    public List<ServerCountWithType> queryCommunicationStaticByServer(Map map) {
        List<RunningStatusUnPTVO> unPTVOS = countUnPTRunningStatus(map);
        String soc = (String) map.get("soc");
        List<RunningStatusUnPTVO> checkedVOS = new ArrayList<>();
        if ("CLIENT".equals(soc)) {
            checkedVOS.addAll(DataUtils.removeUnmatchedStatic(unPTVOS, (String) map.get("domain")));
        } else {
            checkedVOS.addAll(unPTVOS);
        }

        Map<String, ServerCountWithType> countWithTypeMap = new HashMap<>();
        for (RunningStatusUnPTVO unPTVO : checkedVOS) {
            String id = unPTVO.getServer() + "-" + unPTVO.getClient();
            if (!countWithTypeMap.containsKey(id)) {
                ServerCountWithType countWithType = new ServerCountWithType();
                if ("SERVER".equals(soc)) {
                    countWithType.setDomain(unPTVO.getClient());
                    countWithType.setType(unPTVO.getClient());
                } else {
                    countWithType.setDomain(unPTVO.getServer());
                    countWithType.setType(unPTVO.getServer());
                }
                countWithType.setTotalCount(unPTVO.getCount());
                countWithType.setFailCount(unPTVO.getFailCount());
                countWithTypeMap.put(id, countWithType);
            } else {
                ServerCountWithType priType = countWithTypeMap.get(id);
                priType.setTotalCount(priType.getTotalCount() + unPTVO.getCount());
                priType.setFailCount(priType.getFailCount() + unPTVO.getFailCount());
            }
        }

        return new ArrayList<>(countWithTypeMap.values());
    }

    @Override
    public List<SumVO> countAllServiceSizeByTop(String startTime, String endTime, Map<String, Object> map) {
        return this.transactionDataStorage.countAllServiceSizeByTop(startTime, endTime, map);
    }

    @Override
    public List<ServiceStatisticVO> getServiceStatistic(String serviceId) {
        String startTime = getStartTime(DAY_TYPE.TODAY);
        return transactionDataStorage.getServiceStatistic(serviceId, startTime);
    }

    /**
     * 通用的获取调用消息的明细记录，支持当前小时、指定小时、当天、历史等
     * xuehao 2018-03-25：新增
     * @param group 系统类别
     * @param map   筛选条件
     * @return 详细调用Transaction的明细清单
     */
    @Override
    public TransactionMessageList queryCommonTransactionMessageList(String group, Map<String, Object> map) {
        String serverAppName = (String) map.getOrDefault("serverAppName", "");
        int startIndex = Integer.parseInt(map.get("start").toString());
        int pageSize = Integer.parseInt(map.get("pageSize").toString());
        MessageTreeList messageList = this.messageTreeStorage.queryMessageTree(group, serverAppName, map, startIndex, pageSize);

        //获取业务系统的系统代码与名称的映射
        Map<String, String> appIdNameConsumerMap;
        Map<String, SvcInfoModel> svcCodeObjMap;
        if (messageList.getTotalSize() > 0) {
            List<String> appIdProviders = new ArrayList<>();
            List<String> appIdConsumers = new ArrayList<>();
            for (MessageTree messageTree : messageList.getMessageTrees()) {
                //获取提供方业务系统信息
                if(!appIdProviders.contains(messageTree.getDomain())) {
                    appIdProviders.add(messageTree.getDomain());
                }
                //获取消费方业务系统信息
                if (messageTree.getCaller() != null && !StringUtils.isEmpty(messageTree.getCaller().getName())
                        && !appIdConsumers.contains(messageTree.getCaller().getName())) {
                    appIdConsumers.add(messageTree.getCaller().getName());
                }
            }
            //获取消费方系统信息
            appIdNameConsumerMap = ListUtils.isEmpty(appIdConsumers) ? null : appInfoService.mapAppIdName(appIdConsumers);

            //获取提供方系统包含的服务信息
            List<SvcInfoModel> svcInfoModels = svcInfoService.getByAppId(appIdProviders);
            svcCodeObjMap = svcInfoService.listToMapCodeObject(svcInfoModels);
        } else {
            appIdNameConsumerMap = null;
            svcCodeObjMap = null;
        }

        //生成返回对象
        TransactionMessageList transactionMessageList = new TransactionMessageList();
        transactionMessageList.setTotalSize(messageList.getTotalSize());

        for (MessageTree messageTree : messageList.getMessageTrees()) {
            TransactionMessage transactionMessage = this.toTransactionMessage(messageTree, appIdNameConsumerMap, svcCodeObjMap);
            transactionMessageList.addTransactionMessage(transactionMessage);
        }

        return transactionMessageList;
    }

}
