package com.winning.monitor.superisor.consumer.logging.transaction.entity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by nicholasyan on 16/10/19.
 */
public class Client {

    private final String id;
    private final String domain;
    private final String ip;
    private final String type;

    private Map<String, TransactionType> types = new LinkedHashMap<String, TransactionType>();

    public Client(String domain, String ip, String type) {
        this.domain = domain;
        this.ip = ip;
        this.type = type;
        this.id = this.domain + "-" + this.ip + "-" + type;
    }

    public Client addType(TransactionType type) {
        types.put(type.getId(), type);
        return this;
    }

    public TransactionType removeType(String id) {
        return types.remove(id);
    }


    public TransactionType findType(String id) {
        return types.get(id);
    }


    public String getId() {
        return this.id;
    }

    public String getDomain() {
        return domain;
    }

    public String getIp() {
        return ip;
    }

    public String getType() {
        return type;
    }

    public Map<String, TransactionType> getTypes() {
        return types;
    }


    public TransactionType findOrCreateType(String id) {
        TransactionType type = types.get(id);

        if (type == null) {
            synchronized (types) {
                type = types.get(id);

                if (type == null) {
                    type = new TransactionType(id);
                    types.put(id, type);
                }
            }
        }

        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Client) {
            Client _o = (Client) obj;

            if (!this.id.equals(_o.getId())) {
                return false;
            }

            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 0;

        hash = hash * 31 + (id == null ? 0 : id.hashCode());

        return hash;
    }


}
