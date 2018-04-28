package com.winning.monitor.supervisor.consumer.website.entity.request_entity;

import org.xblink.annotation.XBlinkAlias;

/**
 * @Author Lemod
 * @Version 2016/11/26
 */
@XBlinkAlias(value = "loggingEntity")
public class LoggingEntity {

    private String sourceProvider;

    private String providerAddress;

    private String providerHostName;

    private RemoteCaller remoteCaller;

    private TransactionCopy transactionCopy;

    public String getSourceProvider() {
        return sourceProvider;
    }

    public void setSourceProvider(String sourceProvider) {
        this.sourceProvider = sourceProvider;
    }

    public String getProviderAddress() {
        return providerAddress;
    }

    public void setProviderAddress(String providerAddress) {
        this.providerAddress = providerAddress;
    }

    public String getProviderHostName() {
        return providerHostName;
    }

    public void setProviderHostName(String providerHostName) {
        this.providerHostName = providerHostName;
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
