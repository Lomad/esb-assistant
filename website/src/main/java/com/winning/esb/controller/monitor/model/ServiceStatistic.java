package com.winning.esb.controller.monitor.model;

import com.winning.monitor.data.api.transaction.domain.TransactionStatisticData;
import com.winning.monitor.data.api.base.ServiceStatisticVO;

import java.util.Map;

/**
 * @Author Lemod
 * @Version 2017/9/22
 */
public class ServiceStatistic {

    private String id;

    private String name;

    private Long count;

    private Long countFail;

    public ServiceStatistic() {
    }

    public ServiceStatistic(ServiceStatisticVO vo){
        this.id = vo.getServiceId();
    }

    public ServiceStatistic(TransactionStatisticData data){
        this.id = data.getTransactionTypeName();
    }

    public ServiceStatistic(Map<String, Object> map) {
        String id = (String) map.get("serviceCode");
        String name = (String) map.get("serviceName");
        Long total = (Long) map.get("totalCount");
        Long failure = (Long) map.get("failCount");
        this.id = id;
        this.name = name;
        this.count = total;
        this.countFail = failure;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getCountFail() {
        return countFail;
    }

    public void setCountFail(Long countFail) {
        this.countFail = countFail;
    }
}
