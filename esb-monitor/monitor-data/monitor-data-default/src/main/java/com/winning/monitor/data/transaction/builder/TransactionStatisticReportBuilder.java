package com.winning.monitor.data.transaction.builder;

import com.winning.monitor.data.api.transaction.TransactionReportMerger;

/**
 * Created by nicholasyan on 16/10/21.
 */
public class TransactionStatisticReportBuilder {

    private final TransactionReportMerger transactionReportMerger = new TransactionReportMerger();
    private final ReportType reportType;

    public TransactionStatisticReportBuilder(ReportType reportType) {
        this.reportType = reportType;

    }


    public enum ReportType {
        Server,
        TransactionType
    }

}
