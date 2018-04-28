package com.winning.monitor.data.transaction.builder;

import com.winning.monitor.data.api.transaction.TransactionReportMerger;
import com.winning.monitor.data.api.transaction.domain.TransactionStatisticData;
import com.winning.monitor.data.api.transaction.domain.TransactionStatisticReport;
import com.winning.monitor.data.api.transaction.vo.TransactionClientVO;
import com.winning.monitor.data.api.transaction.vo.TransactionMachineVO;
import com.winning.monitor.data.api.transaction.vo.TransactionReportVO;
import com.winning.monitor.data.api.transaction.vo.TransactionTypeVO;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by nicholasyan on 16/10/21.
 */
public class ClientCallTransactionTypeStatisticDataMerger {

    private final static TransactionReportMerger transactionMerger = new TransactionReportMerger();
    private Map<String, Map<String, TransactionTypeVO>> transactionTypeMap;
    private String clientDomain;


    public ClientCallTransactionTypeStatisticDataMerger(String clientDomain) {
        this.clientDomain = clientDomain;
        this.transactionTypeMap = new LinkedHashMap<>();

    }

    public void add(TransactionReportVO report) {
        for (TransactionMachineVO machine : report.getMachines()) {
            for (TransactionClientVO client : machine.getTransactionClients()) {
                if (StringUtils.isEmpty(client.getDomain()) || !clientDomain.equals(client.getDomain())) {
                    continue;
                }

                Map<String, TransactionTypeVO> serverDomainMap = this.transactionTypeMap.get(report.getDomain());
                    if (serverDomainMap == null) {
                        serverDomainMap = new LinkedHashMap<>();
                        this.transactionTypeMap.put(report.getDomain(), serverDomainMap);
                }

                for (TransactionTypeVO transactionType : client.getTransactionTypes()) {
                    //如果是统计TransactionType数据
                    TransactionTypeVO oldTransactionType =
                            serverDomainMap.get(transactionType.getName());

                    //当前AppClientDomain不存在,则加入当前的AppClientDomain
                    if (oldTransactionType == null) {
                        oldTransactionType = new TransactionTypeVO();
                        oldTransactionType.setId(transactionType.getId());
                        oldTransactionType.setName(transactionType.getName());
                        serverDomainMap.put(transactionType.getName(), oldTransactionType);
                    }

                    //合并TransactionType
                    transactionMerger.mergeType(oldTransactionType, transactionType);
                }
            }
        }
    }


    public TransactionStatisticReport toTransactionStatisticReport() {
        TransactionStatisticReport transactionStatisticReport = new TransactionStatisticReport();

        for (Map.Entry<String, Map<String, TransactionTypeVO>> entry : this.transactionTypeMap.entrySet()) {
            String serverAppName = entry.getKey();
            Map<String, TransactionTypeVO> transactionTypeMap = entry.getValue();

            for (Map.Entry<String, TransactionTypeVO> callerEntry : transactionTypeMap.entrySet()) {
                TransactionTypeVO transactionTypeVO = callerEntry.getValue();
                TransactionStatisticData statisticData =
                        this.toTransactionStatisticData(serverAppName, transactionTypeVO);
                transactionStatisticReport.addTransactionStatisticData(statisticData);
            }
        }
        transactionStatisticReport.setTotalSize(transactionStatisticReport.getTransactionStatisticDatas().size());
        return transactionStatisticReport;
    }


    private TransactionStatisticData toTransactionStatisticData(String serverDomain,
                                                                TransactionTypeVO transactionType) {

        TransactionStatisticData statisticData = new TransactionStatisticData();
        statisticData.setServerAppId(serverDomain);
        statisticData.setServerAppName(serverDomain);
        statisticData.setClientAppId(this.clientDomain);
        statisticData.setClientAppName(this.clientDomain);
        statisticData.setTransactionTypeId(transactionType.getName());
        statisticData.setTransactionTypeName(transactionType.getName());

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

}
