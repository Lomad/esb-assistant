package com.winning.monitor.data.api.transaction.vo;

import java.util.*;

/**
 * Created by nicholasyan on 16/9/12.
 */
public class TransactionReportVO {

    private String id;
    private String domain;
    private String group;
    private Set<String> domainNames = new LinkedHashSet<String>();
    private Set<String> ips = new LinkedHashSet<String>();
    private String startTime;
    private String endTime;
    private List<TransactionMachineVO> machines;
    private String server;
    private int index;
    private TransactionReportType type;
    private Map<String, TransactionMachineVO> machineMap = new LinkedHashMap<String, TransactionMachineVO>();
    private String ip;

    public TransactionReportType getType() {
        return type;
    }

    public void setType(TransactionReportType type) {
        this.type = type;
    }

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

    public Set<String> getIps() {
        return ips;
    }

    public void setIps(Set<String> ips) {
        this.ips = ips;
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

    public List<TransactionMachineVO> getMachines() {
        return machines;
    }

    public void setMachines(List<TransactionMachineVO> machines) {
        this.machines = machines;
        this.machineMap.clear();
        if (machines == null) {
            return;
        }
        for (TransactionMachineVO machine : machines) {
            this.machineMap.put(machine.getIp(), machine);
        }
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Set<String> getDomainNames() {
        return domainNames;
    }

    public void setDomainNames(Set<String> domainNames) {
        this.domainNames = domainNames;
    }

    public TransactionMachineVO getTransactionMachine(String ip) {
        return this.machineMap.get(ip);
    }

    public void addTransactionMachine(TransactionMachineVO transactionMachine) {
        this.machines.add(transactionMachine);
        this.machineMap.put(transactionMachine.getIp(), transactionMachine);
    }

    public void initialReport() {
        this.domainNames.add(domain);
        this.setMachines(machines);
        for (TransactionMachineVO machine : machines) {
            machine.setTransactionClients(machine.getTransactionClients());
            for (TransactionClientVO client : machine.getTransactionClients()) {
                client.setTransactionTypes(client.getTransactionTypes());
                for (TransactionTypeVO type : client.getTransactionTypes()) {
                    type.setTransactionNames(type.getTransactionNames());
                }
            }
        }
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
