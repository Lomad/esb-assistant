package com.winning.esb.simulator.utils;

import com.google.gson.Gson;

/**
 * @Author Lemod
 * @Version 2018/3/7
 */
public class GlobalConstant {

    public static Gson GSON = new Gson();

    public static final String MILLISFORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final String COLLECTION_REALTIME = "TransactionRealtimeReports";

    public static final String MACHINES = "machines";
    public static final String CLIENTS = "machines.transactionClients";
    public static final String TYPES = "machines.transactionClients.transactionTypes";

    public static final String MESSAGE_TREE_DURATION = "messageTree.message.durationInMicro";
    public static final String MESSAGE_TREE_TIMESTAMP = "messageTree.message.timestampInMillis";
}
