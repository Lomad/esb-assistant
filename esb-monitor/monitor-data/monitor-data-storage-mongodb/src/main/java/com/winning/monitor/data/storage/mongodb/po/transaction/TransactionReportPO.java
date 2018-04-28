package com.winning.monitor.data.storage.mongodb.po.transaction;

import com.winning.monitor.data.api.transaction.vo.TransactionMachineVO;
import com.winning.monitor.data.api.transaction.vo.TransactionReportType;
import com.winning.monitor.data.api.transaction.vo.TransactionReportVO;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by nicholasyan on 16/9/29.
 */
@Document
public class TransactionReportPO {

    @Id
    private String id;
    private String domain;
    private String group;
    private int idx;
    private Set<String> ips;
    private String type;
    private String server;
    private String startTime;
    private String endTime;
    private String ip;

    private List<TransactionMachinePO> machines;

    public TransactionReportPO() {

    }

    public TransactionReportPO(TransactionReportVO transactionReportVO) {
        this.id = transactionReportVO.getId();
        this.domain = transactionReportVO.getDomain();
        this.group = transactionReportVO.getGroup();
        this.idx = transactionReportVO.getIndex();
        this.ips = transactionReportVO.getIps();
        this.type = transactionReportVO.getType().getName();
        this.server = transactionReportVO.getServer();
        this.startTime = transactionReportVO.getStartTime();
        this.endTime = transactionReportVO.getEndTime();
        this.ip = transactionReportVO.getIp();

        List<TransactionMachinePO> machines = new ArrayList<>();
        if (transactionReportVO.getMachines() != null) {
            for (TransactionMachineVO machineVO : transactionReportVO.getMachines()) {
                machines.add(new TransactionMachinePO(machineVO));
            }
        }
        this.machines = machines;
    }

    public TransactionReportVO toTransactionReportVO() {
        TransactionReportVO transactionReportVO = new TransactionReportVO();
        transactionReportVO.setId(this.id);
        transactionReportVO.setDomain(this.domain);
        transactionReportVO.setIndex(this.idx);
        transactionReportVO.setIps(this.ips);
        transactionReportVO.setType(TransactionReportType.valueOf(this.type));
        transactionReportVO.setServer(this.server);
        transactionReportVO.setStartTime(this.startTime);
        transactionReportVO.setEndTime(this.endTime);
        transactionReportVO.setIp(this.ip);
        transactionReportVO.setGroup(this.group);

        List<TransactionMachineVO> machines = new ArrayList<>();
        if (this.getMachines() != null) {
            for (TransactionMachinePO machinePO : this.getMachines()) {
                machines.add(machinePO.toTransactionMachineVO());
            }
        }
        transactionReportVO.setMachines(machines);
        return transactionReportVO;
    }

    @Field
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public Set<String> getIps() {
        return ips;
    }

    public void setIps(Set<String> ips) {
        this.ips = ips;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<TransactionMachinePO> getMachines() {
        return machines;
    }

    public void setMachines(List<TransactionMachinePO> machines) {
        this.machines = machines;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
