package com.winning.monitor.superisor.consumer.logging.transaction.entity;

import java.util.LinkedHashMap;
import java.util.Map;

public class Machine {
    private String ip;

    private Map<String, Client> clients = new LinkedHashMap<String, Client>();

    public Machine() {
    }

    public Machine(String ip) {
        this.ip = ip;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Machine) {
            Machine _o = (Machine) obj;

            if (!ip.equals(_o.getIp())) {
                return false;
            }

            return true;
        }

        return false;
    }

    public String getIp() {
        return ip;
    }

    public Machine setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public Client findClient(String domain, String ip, String type) {
        String id = domain + "-" + ip + "-" + type;
        return clients.get(id);
    }

    public Client findOrCreateClient(String domain, String ip, String type) {
        String id = domain + "-" + ip + "-" + type;
        Client client = clients.get(id);

        if (client == null) {
            synchronized (this.clients) {
                client = clients.get(id);
                if (client == null) {
                    client = new Client(domain, ip, type);
                    clients.put(id, client);
                }
            }
        }

        return client;
    }

    public Map<String, Client> getClients() {
        return this.clients;
    }

    @Override
    public int hashCode() {
        int hash = 0;

        hash = hash * 31 + (ip == null ? 0 : ip.hashCode());

        return hash;
    }

}
