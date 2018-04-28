package com.winning.monitor.superisor.consumer.logging.transaction.entity;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class TransactionReport {

    private String id;

    private String m_domain;

    private String group;

    private java.util.Date m_startTime;

    private java.util.Date m_endTime;

//    private Set<String> m_domainNames = new LinkedHashSet<String>();

    private Map<String, Machine> m_machines = new LinkedHashMap<String, Machine>();

    private Set<String> m_ips = new LinkedHashSet<String>();

    public TransactionReport() {
    }

    public TransactionReport(String group, String domain) {
        m_domain = domain;
        this.group = group;
    }


    public TransactionReport addIp(String ip) {
        m_ips.add(ip);
        return this;
    }

    public TransactionReport addMachine(Machine machine) {
        m_machines.put(machine.getIp(), machine);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TransactionReport) {
            TransactionReport _o = (TransactionReport) obj;

            if (!m_domain.equals(_o.getDomain())) {
                return false;
            }

            return true;
        }

        return false;
    }

    public Machine findMachine(String ip) {
        return m_machines.get(ip);
    }

    public Machine findOrCreateMachine(String ip) {
        Machine machine = m_machines.get(ip);

        if (machine == null) {
            synchronized (m_machines) {
                machine = m_machines.get(ip);

                if (machine == null) {
                    machine = new Machine(ip);
                    m_machines.put(ip, machine);
                }
            }
        }

        return machine;
    }

    public String getDomain() {
        return m_domain;
    }

    public TransactionReport setDomain(String domain) {
        m_domain = domain;
        return this;
    }

//    public Set<String> getDomainNames() {
//        return m_domainNames;
//    }

    public java.util.Date getEndTime() {
        return m_endTime;
    }

    public TransactionReport setEndTime(java.util.Date endTime) {
        m_endTime = endTime;
        return this;
    }

    public Set<String> getIps() {
        return m_ips;
    }

    public Map<String, Machine> getMachines() {
        return m_machines;
    }

    public java.util.Date getStartTime() {
        return m_startTime;
    }

    public TransactionReport setStartTime(java.util.Date startTime) {
        m_startTime = startTime;
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;

        hash = hash * 31 + (m_domain == null ? 0 : m_domain.hashCode());

        return hash;
    }

    public Machine removeMachine(String ip) {
        return m_machines.remove(ip);
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
