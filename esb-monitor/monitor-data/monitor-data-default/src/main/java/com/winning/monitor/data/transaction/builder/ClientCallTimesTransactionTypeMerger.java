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
 * Created by sao something on 2016/11/4.
 */
public class ClientCallTimesTransactionTypeMerger {
    private final static TransactionReportMerger transactionMerger = new TransactionReportMerger();
    private final String clientDomain;
    private final String transactionTypeName;

    private LinkedHashMap<Integer, Range2> range2;

    public ClientCallTimesTransactionTypeMerger(String clientDomain,
                                                String transactionTypeName) {
        this.clientDomain = clientDomain;
        this.transactionTypeName = transactionTypeName;
        this.range2 = new LinkedHashMap<>();
    }

    public void add(TransactionReportVO report) {
        for (TransactionMachineVO machine : report.getMachines()) {
                for (TransactionClientVO client : machine.getTransactionClients()) {
                    if (StringUtils.isEmpty(client.getDomain()) || !clientDomain.equals(client.getDomain())) {
                        continue;
                    }

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
