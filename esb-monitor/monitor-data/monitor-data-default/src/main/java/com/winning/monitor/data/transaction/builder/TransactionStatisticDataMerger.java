package com.winning.monitor.data.transaction.builder;

import com.winning.monitor.agent.logging.message.Caller;
import com.winning.monitor.data.api.transaction.TransactionReportMerger;
import com.winning.monitor.data.api.transaction.domain.TransactionStatisticData;
import com.winning.monitor.data.api.transaction.domain.TransactionStatisticReport;
import com.winning.monitor.data.api.transaction.vo.*;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by nicholasyan on 16/10/21.
 */
public class TransactionStatisticDataMerger {

    private final static TransactionReportMerger transactionMerger = new TransactionReportMerger();
    private final static String ALL_MACHINE = "ALL";
    private final String serverDomain;
    private final TransactionLevel transactionLevel;
    private final StatisticGroupType groupType;
    private Map<Object, Map<String, TransactionTypeVO>> transactionTypeMap;
    private Map<Object, Map<String, TransactionNameVO>> transactionNameMap;
    private Map<String, TransactionTypeVO> allMachineTransactionTypes;
    private Map<String, TransactionNameVO> allMachineTransactionNames;

    public TransactionStatisticDataMerger(String serverDomain,
                                          TransactionLevel transactionLevel,
                                          StatisticGroupType groupType) {
        this.serverDomain = serverDomain;
        this.transactionLevel = transactionLevel;
        this.groupType = groupType;

        if (this.transactionLevel == TransactionLevel.TransactionName) {
            this.transactionNameMap = new LinkedHashMap<>();
            this.allMachineTransactionNames = new LinkedHashMap<>();
            this.transactionNameMap.put(ALL_MACHINE, this.allMachineTransactionNames);
        } else {
            this.transactionTypeMap = new LinkedHashMap<>();
            this.allMachineTransactionTypes = new LinkedHashMap<>();
            this.transactionTypeMap.put(ALL_MACHINE, this.allMachineTransactionTypes);
        }
    }

    public void add(TransactionReportVO report) {
        for (TransactionMachineVO machine : report.getMachines()) {
            String serverIp = machine.getIp();
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
                    //如果是统计TransactionType数据
                    if (this.transactionLevel == TransactionLevel.TransactionType) {
                        //如果是根据服务器地址进行分组
                        if (this.groupType == StatisticGroupType.Server) {
                            //首先判断是否存在当前Server的IP
                            Map<String, TransactionTypeVO> transactionTypes =
                                    this.transactionTypeMap.get(serverIp);
                            //当前IP不存在,则加入当前的IP地址
                            if (transactionTypes == null) {
                                transactionTypes = new LinkedHashMap<>();
                                this.transactionTypeMap.put(serverIp, transactionTypes);
                            }
                            //合并当前IP的transactionTypes
                            this.mergeTransactionType(transactionTypes, transactionType);
                            //合并所有Machine的transactionTypes
                            this.mergeTransactionType(this.allMachineTransactionTypes, transactionType);
                        }
                        //如果是根据客户端进行分组
                        else {
                            //首先判断是否存在当前的AppClientDomain
                            Map<String, TransactionTypeVO> transactionTypes =
                                    this.transactionTypeMap.get(caller);

                            //当前AppClientDomain不存在,则加入当前的AppClientDomain
                            if (transactionTypes == null) {
                                transactionTypes = new LinkedHashMap<>();
                                this.transactionTypeMap.put(caller, transactionTypes);
                            }
                            //合并当前AppClientDomain的transactionTypes
                            this.mergeTransactionType(transactionTypes, transactionType);
                            //合并所有Machine的transactionTypes
                            this.mergeTransactionType(this.allMachineTransactionTypes, transactionType);
                        }
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
            for (TransactionTypeVO allMachineTransactionType : this.allMachineTransactionTypes.values()) {
                String transactionTypeName = allMachineTransactionType.getName();
                //所有机器的总的统计数据合计
                TransactionStatisticData allStatisticData =
                        this.toTransactionStatisticData(null, null, allMachineTransactionType);

                transactionStatisticReport.addTransactionStatisticData(allStatisticData);

                for (Map.Entry<Object, Map<String, TransactionTypeVO>> entry : this.transactionTypeMap.entrySet()) {
                    TransactionTypeVO transactionType = entry.getValue().get(transactionTypeName);
                    if (transactionType == null) {
                        continue;
                    }

                    TransactionStatisticData statisticData;

                    if (groupType == StatisticGroupType.Server) {
                        String serverIp = (String) entry.getKey();
                        if (ALL_MACHINE.equals(serverIp)) {
                            continue;
                        }

                        statisticData = this.toTransactionStatisticData(serverIp, null, transactionType);
                    } else {
                        Caller caller = (Caller) entry.getKey();
                        statisticData = this.toTransactionStatisticData(null, caller, transactionType);
                    }

                    allStatisticData.addTransactionStatisticData(statisticData);
                }
            }
        } else {

        }

        transactionStatisticReport.setTotalSize(transactionStatisticReport.getTransactionStatisticDatas().size());
        return transactionStatisticReport;
    }

    private TransactionStatisticData toTransactionStatisticData(String serverIp, Caller caller,
                                                                TransactionTypeVO transactionType) {
        TransactionStatisticData statisticData = new TransactionStatisticData();
        statisticData.setServerAppName(serverDomain);
        statisticData.setTransactionTypeName(transactionType.getName());
        statisticData.setTransactionTypeId(transactionType.getName());

        if (serverIp != null) {
            statisticData.setServerIpAddress(serverIp);
        }
        if (caller != null) {
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


    private void mergeTransactionType(Map<String, TransactionTypeVO> transactionTypes, TransactionTypeVO transactionType) {
        TransactionTypeVO oldTransactionTypeVO = transactionTypes.get(transactionType.getName());
        //如果当前是第一个transactionType
        if (oldTransactionTypeVO == null) {
            oldTransactionTypeVO = new TransactionTypeVO();
            oldTransactionTypeVO.setId(transactionType.getId());
            oldTransactionTypeVO.setName(transactionType.getName());
            transactionTypes.put(oldTransactionTypeVO.getName(), oldTransactionTypeVO);
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
