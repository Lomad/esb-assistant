package com.winning.monitor.data.api.transaction;

import com.winning.monitor.data.api.transaction.vo.TransactionNameVO;
import com.winning.monitor.data.api.transaction.vo.TransactionTypeVO;

/**
 * Created by nicholasyan on 16/9/27.
 */
public class HistoryTransactionReportMerger extends TransactionReportMerger {

    public double days = 1;
    public double hours = 0;

    public HistoryTransactionReportMerger(int days, int hours) {
        this.days = days;
        this.hours = hours;
    }

    @Override
    public void mergeName(TransactionNameVO old, TransactionNameVO other) {
        super.mergeName(old, other);
        old.setTps(old.getTotalCount() / ((days * 24 + hours) * 3600));
    }

    @Override
    public void mergeType(TransactionTypeVO old, TransactionTypeVO other) {
        super.mergeType(old, other);
        old.setTps(old.getTotalCount() / ((days * 24 + hours) * 3600));
    }

}
