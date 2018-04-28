package com.winning.monitor.data.api.base;

/**
 * @author Lemod
 * @Version 2017/4/24
 */
public class ServerCountWithType {

    private String name;
    private String domain;
    private Long totalCount;
    private Long failCount;

    public String getType() {
        return name;
    }

    public void setType(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getFailCount() {
        return failCount;
    }

    public void setFailCount(Long failCount) {
        this.failCount = failCount;
    }
}
