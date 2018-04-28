package com.winning.esb.simulator.utils.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @Author Lemod
 * @Version 2016/11/26
 */
@XmlRootElement(name = "loggingEntity")
public class LoggingEntity {

    private String sourceProvider;

    private String providerAddress;

    private String providerHostName;

    private RemoteCaller remoteCaller;

    private TransactionCopy transactionCopy;

    @XmlElement
    public String getSourceProvider() {
        return sourceProvider;
    }

    public void setSourceProvider(String sourceProvider) {
        this.sourceProvider = sourceProvider;
    }

    @XmlElement
    public String getProviderAddress() {
        return providerAddress;
    }

    public void setProviderAddress(String providerAddress) {
        this.providerAddress = providerAddress;
    }

    @XmlElement
    public String getProviderHostName() {
        return providerHostName;
    }

    public void setProviderHostName(String providerHostName) {
        this.providerHostName = providerHostName;
    }

    @XmlElement(name = "transactionCopy")
    public TransactionCopy getTransactionCopy() {
        return transactionCopy;
    }

    public void setTransactionCopy(TransactionCopy transactionCopy) {
        this.transactionCopy = transactionCopy;
    }

    @XmlElement(name = "remoteCaller")
    public RemoteCaller getRemoteCaller() {

        return remoteCaller;
    }

    public void setRemoteCaller(RemoteCaller remoteCaller) {
        this.remoteCaller = remoteCaller;
    }

}
