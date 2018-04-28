package com.winning.monitor.data.storage.mongodb.po.transaction;

import com.winning.monitor.data.api.transaction.vo.TransactionClientVO;
import com.winning.monitor.data.api.transaction.vo.TransactionTypeVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nicholasyan on 16/10/19.
 */
public class TransactionClientPO {

    private String ip;
    private String id;
    private String domain;
    private String type;

    private List<TransactionTypePO> transactionTypes = new ArrayList<>();

    public TransactionClientPO() {

    }

    public TransactionClientPO(TransactionClientVO transactionClientVO) {
        this.ip = transactionClientVO.getIp();
        this.id = transactionClientVO.getId();
        this.domain = transactionClientVO.getDomain();
        this.type = transactionClientVO.getType();

        if (transactionClientVO.getTransactionTypes() != null) {
            for (TransactionTypeVO transactionTypeVO : transactionClientVO.getTransactionTypes()) {
                this.transactionTypes.add(new TransactionTypePO(transactionTypeVO));
            }
        }
    }


    public TransactionClientVO toTransactionClientVO() {
        TransactionClientVO transactionClientVO = new TransactionClientVO();
        transactionClientVO.setIp(this.ip);
        transactionClientVO.setId(this.id);
        transactionClientVO.setDomain(this.domain);
        transactionClientVO.setType(this.type);

        List<TransactionTypeVO> transactionTypeVOs = new ArrayList<>();
        if (this.getTransactionTypes() != null) {
            for (TransactionTypePO transactionTypePO : this.getTransactionTypes()) {
                transactionTypeVOs.add(transactionTypePO.toTransactionTypeVO());
            }
        }
        transactionClientVO.setTransactionTypes(transactionTypeVOs);
        return transactionClientVO;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public List<TransactionTypePO> getTransactionTypes() {
        return transactionTypes;
    }

    public void setTransactionTypes(List<TransactionTypePO> transactionTypes) {
        this.transactionTypes = transactionTypes;
    }

}
