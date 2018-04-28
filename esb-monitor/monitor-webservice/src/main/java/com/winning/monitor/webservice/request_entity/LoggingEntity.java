package com.winning.monitor.webservice.request_entity;

import org.xblink.annotation.XBlinkAlias;

/**
 * @Author Lemod
 * @Version 2016/11/26
 */
@XBlinkAlias(value = "MonitorLogging")
public class LoggingEntity {

    private String sourceProvider;

    private RemoteCaller remoteCaller;

    private TransactionCopy transactionCopy;

    public String getSourceProvider() {
        return sourceProvider;
    }

    public void setSourceProvider(String sourceProvider) {
        this.sourceProvider = sourceProvider;
    }

    public TransactionCopy getTransactionCopy() {
        return transactionCopy;
    }

    public void setTransactionCopy(TransactionCopy transactionCopy) {
        this.transactionCopy = transactionCopy;
    }

    public RemoteCaller getRemoteCaller() {

        return remoteCaller;
    }

    public void setRemoteCaller(RemoteCaller remoteCaller) {
        this.remoteCaller = remoteCaller;
    }

}
