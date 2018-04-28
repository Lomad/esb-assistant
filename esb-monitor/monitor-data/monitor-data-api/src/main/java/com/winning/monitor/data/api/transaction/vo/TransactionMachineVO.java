package com.winning.monitor.data.api.transaction.vo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nicholasyan on 16/9/12.
 */
public class TransactionMachineVO {

    private String ip;

    private List<TransactionClientVO> transactionClients = new ArrayList<>();
    private Map<String, TransactionClientVO> transactionClientMap = new LinkedHashMap<String, TransactionClientVO>();

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public List<TransactionClientVO> getTransactionClients() {
        return transactionClients;
    }

    public void setTransactionClients(List<TransactionClientVO> transactionClients) {
        this.transactionClients = transactionClients;
        this.transactionClientMap.clear();
        if (transactionClients == null) {
            return;
        }
        for (TransactionClientVO transactionClient : transactionClients) {
            this.transactionClientMap.put(transactionClient.getId(), transactionClient);
        }
    }

    public void addTransactionClient(TransactionClientVO transactionClient) {
        this.transactionClients.add(transactionClient);
        this.transactionClientMap.put(transactionClient.getId(), transactionClient);
    }

    public TransactionClientVO getTransactionClient(String id) {
        return this.transactionClientMap.get(id);
    }
}
