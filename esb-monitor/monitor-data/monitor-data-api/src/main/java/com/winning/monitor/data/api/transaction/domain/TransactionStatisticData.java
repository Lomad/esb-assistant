package com.winning.monitor.data.api.transaction.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 单条Transaction的统计结果数据
 */
public class TransactionStatisticData {

    //服务提供方ID
    private String serverAppId;
    //服务对应的系统名称
    private String serverAppName;
    //服务名称
    private String transactionTypeName;
    //服务ID
    private String transactionTypeId;
    //服务步骤名称
    private String transactionName;
    //服务端IP名称
    private String serverIpAddress;
    //客户端APP ID
    private String clientAppId;
    //客户端APP名称
    private String clientAppName;

    private String clientIpAddress;
    private String clientType;

    private long totalCount;
    private long failCount;
    private double failPercent;
    private double min = 86400000d;
    private double max = -1d;
    private double sum;
    private double avg;
    private double std;

    private double tps;
    private double line95Value;
    private double line99Value;

    private List<TransactionStatisticData> transactionStatisticDataDetails =
            new ArrayList<>();

    private String transactionTypeFromReport;

    public String getServerAppId() {
        return serverAppId;
    }

    public void setServerAppId(String serverAppId) {
        this.serverAppId = serverAppId;
    }

    public String getClientAppId() {
        return clientAppId;
    }

    public void setClientAppId(String clientAppId) {
        this.clientAppId = clientAppId;
    }

    public String getServerAppName() {
        return serverAppName;
    }

    public void setServerAppName(String serverAppName) {
        this.serverAppName = serverAppName;
    }

    public String getTransactionTypeName() {
        return transactionTypeName;
    }

    public void setTransactionTypeName(String transactionTypeName) {
        this.transactionTypeName = transactionTypeName;
    }

    public String getTransactionTypeId() {
        return transactionTypeId;
    }

    public void setTransactionTypeId(String transactionTypeId) {
        this.transactionTypeId = transactionTypeId;
    }

    public String getTransactionName() {
        return transactionName;
    }

    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }

    public String getServerIpAddress() {
        return serverIpAddress;
    }

    public void setServerIpAddress(String serverIpAddress) {
        this.serverIpAddress = serverIpAddress;
    }

    public String getClientAppName() {
        return clientAppName;
    }

    public void setClientAppName(String clientAppName) {
        this.clientAppName = clientAppName;
    }

    public String getClientIpAddress() {
        return clientIpAddress;
    }

    public void setClientIpAddress(String clientIpAddress) {
        this.clientIpAddress = clientIpAddress;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getFailCount() {
        return failCount;
    }

    public void setFailCount(long failCount) {
        this.failCount = failCount;
    }

    public double getFailPercent() {
        return failPercent;
    }

    public void setFailPercent(double failPercent) {
        this.failPercent = failPercent;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public double getAvg() {
        return avg;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }

    public double getStd() {
        return std;
    }

    public void setStd(double std) {
        this.std = std;
    }

    public double getTps() {
        return tps;
    }

    public void setTps(double tps) {
        this.tps = tps;
    }

    public double getLine95Value() {
        return line95Value;
    }

    public void setLine95Value(double line95Value) {
        this.line95Value = line95Value;
    }

    public double getLine99Value() {
        return line99Value;
    }

    public void setLine99Value(double line99Value) {
        this.line99Value = line99Value;
    }

    public List<TransactionStatisticData> getTransactionStatisticDataDetails() {
        return transactionStatisticDataDetails;
    }

    public void setTransactionStatisticDataDetails(List<TransactionStatisticData> transactionStatisticDataDetails) {
        this.transactionStatisticDataDetails = transactionStatisticDataDetails;
    }

    public void addTransactionStatisticData(TransactionStatisticData transactionStatisticData) {
        this.transactionStatisticDataDetails.add(transactionStatisticData);
    }

    public String getTransactionTypeFromReport() {
        return transactionTypeFromReport;
    }

    public void setTransactionTypeFromReport(String transactionTypeFromReport) {
        this.transactionTypeFromReport = transactionTypeFromReport;
    }
}
