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
public class TransactionNameServerStatisticDataMerger {

    private final static TransactionReportMerger transactionMerger = new TransactionReportMerger();

    private final String serverDomain;
    private final String clientAppName;
    private final String serverIpAddress;
    private final String transactionTypeName;

    private Map<String, TransactionNameVO> transactionNames;

    public TransactionNameServerStatisticDataMerger(String serverDomain,
                                                    String clientAppName,
                                                    String serverIpAddress,
                                                    String transactionTypeName) {
        this.serverDomain = serverDomain;
        this.clientAppName = clientAppName;
        this.serverIpAddress = serverIpAddress;
        this.transactionTypeName = transactionTypeName;
        this.transactionNames = new LinkedHashMap<>();
    }

    public void add(TransactionReportVO report) {
        for (TransactionMachineVO machine : report.getMachines()) {
            String serverIp = machine.getIp();

            //如果不是指定的IP地址
            if (StringUtils.hasText(this.serverIpAddress) &&
                    !this.serverIpAddress.equals(serverIp)) {
                continue;
            }

            for (TransactionClientVO client : machine.getTransactionClients()) {
                String clientDomain = client.getDomain();
                if (StringUtils.hasText(this.clientAppName) &&
                        !this.clientAppName.equals(clientDomain)) {
                    continue;
                }
                for (TransactionTypeVO transactionType : client.getTransactionTypes()) {
                    if (!transactionType.getName().equals(transactionTypeName)) {
                        continue;
                    }

                    for (TransactionNameVO transactionName : transactionType.getTransactionNames()) {
                        String name = transactionName.getName();

                        TransactionNameVO oldTransactionNameVO = transactionNames.get(name);
                        if (oldTransactionNameVO == null) {
                            oldTransactionNameVO = new TransactionNameVO();
                            oldTransactionNameVO.setId(transactionName.getId());
                            oldTransactionNameVO.setName(transactionName.getName());
                            transactionNames.put(name, oldTransactionNameVO);
                        }

                        transactionMerger.mergeName(oldTransactionNameVO, transactionName);
                    }
                }
            }
        }
    }


    public TransactionStatisticReport toTransactionStatisticReport() {
        TransactionStatisticReport transactionStatisticReport = new TransactionStatisticReport();

        for (Map.Entry<String, TransactionNameVO> entry : this.transactionNames.entrySet()) {

            TransactionStatisticData statisticData =
                    this.toTransactionStatisticData(serverIpAddress,clientAppName, null, entry.getValue());

            transactionStatisticReport.addTransactionStatisticData(statisticData);
        }

        transactionStatisticReport.setTotalSize(transactionStatisticReport.getTransactionStatisticDatas().size());
        return transactionStatisticReport;
    }

    private TransactionStatisticData toTransactionStatisticData(String serverIp,String clientAppName, Caller caller,
                                                                TransactionNameVO transactionName) {

        TransactionStatisticData statisticData = new TransactionStatisticData();

        statisticData.setServerAppName(serverDomain);
        statisticData.setTransactionTypeName(transactionTypeName);
        statisticData.setTransactionName(transactionName.getName());

        if (serverIp != null) {
            statisticData.setServerIpAddress(serverIp);
        }
        if (clientAppName != null) {
            statisticData.setClientAppName(clientAppName);
        }
        if (caller != null) {
            statisticData.setClientAppName(caller.getName());
            statisticData.setClientIpAddress(caller.getIp());
            statisticData.setClientType(caller.getType());
        }

        statisticData.setTotalCount(transactionName.getTotalCount());
        statisticData.setFailCount(transactionName.getFailCount());
        statisticData.setFailPercent(Math.ceil(transactionName.getFailPercent()));
        statisticData.setMin(transactionName.getMin());
        statisticData.setMax(transactionName.getMax());
        statisticData.setSum(transactionName.getSum());

        statisticData.setAvg(Math.round(transactionName.getAvg() * 100) / 100.0);
        statisticData.setStd(Math.ceil(transactionName.getStd()));
        statisticData.setTps(Math.round(transactionName.getTps() * 100) / 100.0);
        statisticData.setLine95Value(Math.round(transactionName.getLine95Value() * 100) / 100.0);
        statisticData.setLine99Value(Math.round(transactionName.getLine99Value() * 100) / 100.0);

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
