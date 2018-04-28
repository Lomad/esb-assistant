package com.winning.monitor.data.storage.mongodb.po.transaction;

import com.winning.monitor.data.api.transaction.vo.TransactionClientVO;
import com.winning.monitor.data.api.transaction.vo.TransactionMachineVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nicholasyan on 16/9/18.
 */
public class TransactionMachinePO {

    private String ip;
    private List<TransactionClientPO> transactionClients = new ArrayList<>();

    public TransactionMachinePO() {

    }

    public TransactionMachinePO(TransactionMachineVO transactionMachineVO) {
        this.ip = transactionMachineVO.getIp();
        if (transactionMachineVO.getTransactionClients() != null) {
            for (TransactionClientVO transactionClientVO : transactionMachineVO.getTransactionClients()) {
                this.transactionClients.add(new TransactionClientPO(transactionClientVO));
            }
        }
    }

    public TransactionMachineVO toTransactionMachineVO() {
        TransactionMachineVO transactionMachineVO = new TransactionMachineVO();
        transactionMachineVO.setIp(this.ip);
        List<TransactionClientVO> transactionClientVOs = new ArrayList<>();
        if (this.getTransactionClients() != null) {
            for (TransactionClientPO transactionClientPO : this.getTransactionClients()) {
                transactionClientVOs.add(transactionClientPO.toTransactionClientVO());
            }
        }
        transactionMachineVO.setTransactionClients(transactionClientVOs);
        return transactionMachineVO;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public List<TransactionClientPO> getTransactionClients() {
        return transactionClients;
    }

    public void setTransactionClients(List<TransactionClientPO> transactionClients) {
        this.transactionClients = transactionClients;
    }
}
