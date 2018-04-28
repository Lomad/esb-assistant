package com.winning.monitor.data.api.enums;

/**
 * Created by xuehao on 2017/8/1.
 * 主要用于存放一些常量以及枚举值
 */
public class TransactionReportsEnum {
    public static final String GROUP_BI = "BI";
    public static final String COLLECTION_REALTIME = "TransactionRealtimeReports";
    public static final String COLLECTION_DAILY = "TransactionDailyReports";

    public static final String MACHINES = "machines";
    public static final String CLIENTS = "machines.transactionClients";
    public static final String TYPES = "machines.transactionClients.transactionTypes";

    public static final String MESSAGE_TREE_DURATION = "messageTree.message.durationInMicro";
    public static final String MESSAGE_TREE_TIMESTAMP = "messageTree.message.timestampInMillis";

    /*2018/2/7 YQL  新增表名*/
    public static final String TOP_DURATION = "MessageTree-TopDuration";
}