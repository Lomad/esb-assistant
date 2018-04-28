package com.winning.monitor.data.api.transaction.vo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nicholasyan on 16/10/19.
 */
public class TransactionClientVO {

    private String id;
    private String ip;
    private String domain;
    private String type;

    private List<TransactionTypeVO> transactionTypes = new ArrayList<>();
    private Map<String, TransactionTypeVO> typeMap = new LinkedHashMap<String, TransactionTypeVO>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<TransactionTypeVO> getTransactionTypes() {
        return transactionTypes;
    }

    public void setTransactionTypes(List<TransactionTypeVO> transactionTypes) {
        this.transactionTypes = transactionTypes;
        this.typeMap.clear();
        if (transactionTypes == null) {
            return;
        }
        for (TransactionTypeVO transactionType : transactionTypes) {
            this.typeMap.put(transactionType.getId(), transactionType);
        }
    }

    public void addTransactionType(TransactionTypeVO transactionType) {
        this.transactionTypes.add(transactionType);
        this.typeMap.put(transactionType.getId(), transactionType);
    }

    public TransactionTypeVO getTransactionType(String id) {
        return this.typeMap.get(id);
    }

}
