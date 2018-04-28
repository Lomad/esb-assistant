package com.winning.monitor.data.api.transaction.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nicholasyan on 16/10/20.
 */
public class TransactionStatisticReport {

    private List<TransactionStatisticData> transactionStatisticDatas = new ArrayList<>();

    private long totalSize;

    public List<TransactionStatisticData> getTransactionStatisticDatas() {
        return transactionStatisticDatas;
    }

    public void setTransactionStatisticDatas(List<TransactionStatisticData> transactionStatisticDatas) {
        this.transactionStatisticDatas = transactionStatisticDatas;
    }

    public void addTransactionStatisticData(TransactionStatisticData transactionStatisticData) {
        this.transactionStatisticDatas.add(transactionStatisticData);
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

}
