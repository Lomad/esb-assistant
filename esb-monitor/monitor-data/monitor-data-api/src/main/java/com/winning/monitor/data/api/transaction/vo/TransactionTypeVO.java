package com.winning.monitor.data.api.transaction.vo;


import com.winning.monitor.data.api.vo.AllDuration;
import com.winning.monitor.data.api.vo.Range2;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nicholasyan on 16/9/12.
 */
public class TransactionTypeVO {

    private String id;

    private String name;

    private String router;

    private long totalCount;

    private long failCount;

    private double failPercent;

    private double min = 86400000d;

    private double max = -1d;

    private double sum;

    private double sum2;

    private double avg;

    private double std;

    private double tps;
    private double line95Value;
    private double line99Value;
    private List<TransactionNameVO> transactionNames;
    private Map<Integer, Range2> range2s = new LinkedHashMap<>();
    private Map<Integer, AllDuration> allDurations = new LinkedHashMap<>();

    private Map<String, TransactionNameVO> nameMap = new LinkedHashMap<String, TransactionNameVO>();


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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRouter() {
        return router;
    }

    public void setRouter(String router) {
        this.router = router;
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

    public double getSum2() {
        return sum2;
    }

    public void setSum2(double sum2) {
        this.sum2 = sum2;
    }

    public List<TransactionNameVO> getTransactionNames() {
        return transactionNames;
    }

    public void setTransactionNames(List<TransactionNameVO> transactionNames) {
        this.transactionNames = transactionNames;
        this.nameMap.clear();
        if (transactionNames == null) {
            return;
        }
        for (TransactionNameVO transactionName : transactionNames) {
            nameMap.put(transactionName.getId(), transactionName);
        }
    }

    public TransactionNameVO getTransactionName(String id) {
        return nameMap.get(id);
    }

    public void addTransactionName(TransactionNameVO transactionName) {
        this.transactionNames.add(transactionName);
        this.nameMap.put(transactionName.getId(), transactionName);
    }

    public Map<Integer, Range2> getRange2s() {
        return range2s;
    }

    public void setRange2s(Map<Integer, Range2> range2s) {
        this.range2s = range2s;
    }

    public Map<Integer, AllDuration> getAllDurations() {
        return allDurations;
    }

    public void setAllDurations(Map<Integer, AllDuration> allDurations) {
        this.allDurations = allDurations;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
