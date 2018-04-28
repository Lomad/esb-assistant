package com.winning.monitor.data.transaction.builder;

import com.winning.monitor.agent.logging.message.Caller;
import com.winning.monitor.data.api.transaction.TransactionReportMerger;
import com.winning.monitor.data.api.transaction.domain.TransactionStatisticData;
import com.winning.monitor.data.api.transaction.domain.TransactionStatisticReport;
import com.winning.monitor.data.api.transaction.vo.*;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by nicholasyan on 16/10/21.
 */
public class TransactionStatisticDataClientMerger {

    private final static TransactionReportMerger transactionMerger = new TransactionReportMerger();
    private final static String ALL_MACHINE = "ALL";
    private final String serverDomain;
    private final String serverIpAddress;
    private final String transactionTypeName;
    private final TransactionLevel transactionLevel;
    private Map<String, Map<Object, TransactionTypeVO>> transactionTypeMap;
    private Map<String, Map<Object, TransactionNameVO>> transactionNameMap;
    private Map<Object, TransactionTypeVO> allMachineTransactionTypes;
    private Map<Object, TransactionNameVO> allMachineTransactionNames;

    public TransactionStatisticDataClientMerger(String serverDomain,
                                                TransactionLevel transactionLevel,
                                                String serverIpAddress,
                                                String transactionTypeName) {
        this.serverDomain = serverDomain;
        this.transactionLevel = transactionLevel;
        this.serverIpAddress = serverIpAddress;
        this.transactionTypeName = transactionTypeName;

        if (this.transactionLevel == TransactionLevel.TransactionName) {
            this.transactionNameMap = new LinkedHashMap<>();
//            this.allMachineTransactionNames = new LinkedHashMap<>();
//            this.transactionNameMap.put(ALL_MACHINE, this.allMachineTransactionNames);
        } else {
            this.transactionTypeMap = new LinkedHashMap<>();
//            this.allMachineTransactionTypes = new LinkedHashMap<>();
//            this.transactionTypeMap.put(ALL_MACHINE, this.allMachineTransactionTypes);
        }
    }

    public void add(TransactionReportVO report) {
        for (TransactionMachineVO machine : report.getMachines()) {
            String serverIp = machine.getIp();
            if (StringUtils.hasText(this.serverIpAddress) &&
                    !this.serverIpAddress.equals(serverIp)) {
                continue;
            }

            for (TransactionClientVO client : machine.getTransactionClients()) {
                Caller caller = new Caller();
                if (StringUtils.isEmpty(client.getDomain()) || "null".equals(client.getDomain())) {
                    caller.setName("未知");
                } else {
                    caller.setName(client.getDomain());
                }
                if (StringUtils.isEmpty(client.getIp()) || "null".equals(client.getIp())) {
                    caller.setIp("未知");
                } else {
                    caller.setIp(client.getIp());
                }
                if (StringUtils.isEmpty(client.getType()) || "null".equals(client.getType())) {
                    caller.setType("未知");
                } else {
                    caller.setType(client.getType());
                }

                for (TransactionTypeVO transactionType : client.getTransactionTypes()) {
                    if (!transactionType.getName().equals(transactionTypeName)) {
                        continue;
                    }

                    //如果是统计TransactionType数据
                    if (this.transactionLevel == TransactionLevel.TransactionType) {
                        //首先判断是否存在当前的AppClientDomain
                        Map<Object, TransactionTypeVO> transactionTypes =
                                this.transactionTypeMap.get(caller.getName());

                        //当前AppClientDomain不存在,则加入当前的AppClientDomain
                        if (transactionTypes == null) {
                            transactionTypes = new LinkedHashMap<>();
                            this.transactionTypeMap.put(caller.getName(), transactionTypes);
                        }

                        //合并当前AppClientDomain的transactionTypes
                        this.mergeTransactionType(transactionTypes, caller, transactionType);

                    } else {
                        for (TransactionNameVO transactionName : transactionType.getTransactionNames()) {

                        }
                    }
                }
            }
        }
    }


    public TransactionStatisticReport toTransactionStatisticReport() {
        TransactionStatisticReport transactionStatisticReport = new TransactionStatisticReport();

        if (this.transactionLevel == TransactionLevel.TransactionType) {
            Map<String, TransactionTypeVO> domainTransactionTypes = new HashMap<>();
            Map<String, List<TransactionStatisticData>> transactionStatisticDataMap = new HashMap<>();

            //遍历所有的Caller进行合并
            for (Map.Entry<String, Map<Object, TransactionTypeVO>> entry : this.transactionTypeMap.entrySet()) {
                String appClientDomain = entry.getKey();
                Map<Object, TransactionTypeVO> clientMap = entry.getValue();

                for (Map.Entry<Object, TransactionTypeVO> callerEntry : clientMap.entrySet()) {
                    Caller caller = (Caller) callerEntry.getKey();
                    TransactionTypeVO transactionTypeVO = callerEntry.getValue();
                    TransactionTypeVO appDomainTransactionType = domainTransactionTypes.get(appClientDomain);
                    List<TransactionStatisticData> transactionStatisticDatas =
                            transactionStatisticDataMap.get(appClientDomain);

                    if (appDomainTransactionType == null) {
                        appDomainTransactionType = new TransactionTypeVO();
                        appDomainTransactionType.setId(transactionTypeVO.getId());
                        appDomainTransactionType.setName(transactionTypeVO.getName());
                        domainTransactionTypes.put(appClientDomain, appDomainTransactionType);
                        transactionStatisticDatas = new ArrayList<>();
                        transactionStatisticDataMap.put(appClientDomain, transactionStatisticDatas);
                    }

                    transactionMerger.mergeType(appDomainTransactionType, transactionTypeVO);
                    TransactionStatisticData statisticData =
                            this.toTransactionStatisticData(serverIpAddress, caller, transactionTypeVO);
                    transactionStatisticDatas.add(statisticData);
                }
            }

            for (String appDomain : domainTransactionTypes.keySet()) {
                TransactionTypeVO transactionTypeVO = domainTransactionTypes.get(appDomain);
                Caller caller = new Caller();
                caller.setName(appDomain);
                TransactionStatisticData statisticData =
                        this.toTransactionStatisticData(serverIpAddress, caller, transactionTypeVO);
                List<TransactionStatisticData> details = transactionStatisticDataMap.get(appDomain);
                statisticData.setTransactionStatisticDataDetails(details);
                transactionStatisticReport.addTransactionStatisticData(statisticData);
            }
        } else {

        }

        transactionStatisticReport.setTotalSize(transactionStatisticReport.getTransactionStatisticDatas().size());
        return transactionStatisticReport;
    }

    private TransactionStatisticData toTransactionStatisticData(String serverIp, Caller caller,
                                                                TransactionTypeVO transactionType) {

        TransactionStatisticData statisticData = new TransactionStatisticData();
        statisticData.setServerAppId(serverDomain);
        statisticData.setServerAppName(serverDomain);
        statisticData.setTransactionTypeId(transactionType.getName());
        statisticData.setTransactionTypeName(transactionType.getName());

        if (serverIp != null) {
            statisticData.setServerIpAddress(serverIp);
        }
        if (caller != null) {
            statisticData.setClientAppId(caller.getName());
            statisticData.setClientAppName(caller.getName());
            statisticData.setClientIpAddress(caller.getIp());
            statisticData.setClientType(caller.getType());
        }

        statisticData.setTotalCount(transactionType.getTotalCount());
        statisticData.setFailCount(transactionType.getFailCount());
        statisticData.setFailPercent(Math.ceil(transactionType.getFailPercent()));
        statisticData.setMin(transactionType.getMin());
        statisticData.setMax(transactionType.getMax());
        statisticData.setSum(transactionType.getSum());

        statisticData.setAvg(Math.round(transactionType.getAvg() * 100) / 100.0);
        statisticData.setStd(Math.ceil(transactionType.getStd()));
        statisticData.setTps(Math.round(transactionType.getTps() * 100) / 100.0);
        statisticData.setLine95Value(Math.round(transactionType.getLine95Value() * 100) / 100.0);
        statisticData.setLine99Value(Math.round(transactionType.getLine99Value() * 100) / 100.0);

        return statisticData;
    }


    private void mergeTransactionType(Map<Object, TransactionTypeVO> transactionTypes,
                                      Caller caller,
                                      TransactionTypeVO transactionType) {

        TransactionTypeVO oldTransactionTypeVO = transactionTypes.get(caller);
        //如果当前是第一个transactionType
        if (oldTransactionTypeVO == null) {
            oldTransactionTypeVO = new TransactionTypeVO();
            oldTransactionTypeVO.setId(transactionType.getId());
            oldTransactionTypeVO.setName(transactionType.getName());
            transactionTypes.put(caller, oldTransactionTypeVO);
        }

        //合并TransactionType
        transactionMerger.mergeType(oldTransactionTypeVO, transactionType);
    }


    public enum TransactionLevel {
        TransactionType, TransactionName
    }

    public enum StatisticGroupType {
        Server, Client
    }


}
