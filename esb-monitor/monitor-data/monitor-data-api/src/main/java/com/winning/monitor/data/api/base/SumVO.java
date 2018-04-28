package com.winning.monitor.data.api.base;

/**
 * Created by sao something on 2016/11/29.
 */
public class SumVO {
    public long getServiceSize() {
        return serviceSize;
    }

    public void setServiceSize(long serviceSize) {
        this.serviceSize = serviceSize;
    }

    private String _id;
    private long totalSum;
    private long failSum;
    private long serviceSize;

    public long getTotalSum() {
        return totalSum;
    }

    public void setTotalSum(long totalSum) {
        this.totalSum = totalSum;
    }

    public long getFailSum() {
        return failSum;
    }

    public void setFailSum(long failSum) {
        this.failSum = failSum;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
