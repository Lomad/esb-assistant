package com.winning.esb.model;

public class SvcUrlModel implements Cloneable {
    Integer id;
    Integer svcType;
    String url;
    Integer status;
    String name;
    String desp;
    Integer esbAgent;

    @Override
    public SvcUrlModel clone() {
        try {
            return (SvcUrlModel) super.clone();
        } catch (Exception ex) {
            return null;
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSvcType() {
        return svcType;
    }

    public void setSvcType(Integer svcType) {
        this.svcType = svcType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    public Integer getEsbAgent() {
        return esbAgent;
    }

    public void setEsbAgent(Integer esbAgent) {
        this.esbAgent = esbAgent;
    }
}