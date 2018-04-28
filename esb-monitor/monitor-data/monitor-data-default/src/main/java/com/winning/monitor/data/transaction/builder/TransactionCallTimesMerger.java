package com.winning.monitor.data.transaction.builder;

import com.winning.monitor.data.api.transaction.TransactionReportMerger;
import com.winning.monitor.data.api.transaction.vo.TransactionClientVO;
import com.winning.monitor.data.api.transaction.vo.TransactionMachineVO;
import com.winning.monitor.data.api.transaction.vo.TransactionReportVO;
import com.winning.monitor.data.api.transaction.vo.TransactionTypeVO;
import com.winning.monitor.data.api.vo.Range2;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;

/**
 * Created by nicholasyan on 16/10/21.
 */
public class TransactionCallTimesMerger {

    private final static TransactionReportMerger transactionMerger = new TransactionReportMerger();
    private final String serverDomain;
    private final String serverIpAddress;
    private final String transactionTypeName;

    private LinkedHashMap<Integer, Range2> range2;

    public TransactionCallTimesMerger(String serverDomain,
                                      String serverIpAddress,
                                      String transactionTypeName) {
        this.serverDomain = serverDomain;
        this.serverIpAddress = serverIpAddress;
        this.transactionTypeName = transactionTypeName;
        this.range2 = new LinkedHashMap<>();
    }

    public void add(TransactionReportVO report) {
        for (TransactionMachineVO machine : report.getMachines()) {
            if (StringUtils.hasText(serverIpAddress) &&
                    !machine.getIp().equals(serverIpAddress)) {
                continue;
            }

            for (TransactionClientVO client : machine.getTransactionClients()) {
                for (TransactionTypeVO transactionType : client.getTransactionTypes()) {
                    if (StringUtils.hasText(transactionTypeName) &&
                            !transactionType.getName().equals(transactionTypeName)) {
                        continue;
                    }

                    for (Range2 otherRange2 : transactionType.getRange2s().values()) {
                        Range2 oldRange2 = range2.get(otherRange2.getValue());
                        if (oldRange2 == null) {
                            range2.put(otherRange2.getValue(), otherRange2);
                        } else {
                            transactionMerger.mergeRange2(oldRange2, otherRange2);
                        }
                    }
                }
            }
        }
    }

    public LinkedHashMap<Integer, Range2> getRange2s() {
        return this.range2;
    }


}
