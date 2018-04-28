package com.winning.monitor.data.storage.mongodb.po.transaction;

import com.winning.monitor.data.api.transaction.vo.TransactionNameVO;
import com.winning.monitor.data.api.vo.AllDuration;
import com.winning.monitor.data.api.vo.Duration;
import com.winning.monitor.data.api.vo.Range;

import java.util.Map;

/**
 * Created by nicholasyan on 16/9/18.
 */
public class TransactionNamePO {

    private String id;

    private String name;

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


    private Map<Integer, Range> ranges;
    private Map<Integer, Duration> durations;
    private Map<Integer, AllDuration> allDurations;

    public TransactionNamePO() {

    }

    public TransactionNamePO(TransactionNameVO transactionNameVO) {
        this.id = transactionNameVO.getId();
        this.name = transactionNameVO.getName();
        this.totalCount = transactionNameVO.getTotalCount();
        this.failCount = transactionNameVO.getFailCount();
        this.failPercent = transactionNameVO.getFailPercent();
        this.min = transactionNameVO.getMin();
        this.max = transactionNameVO.getMax();
        this.sum = transactionNameVO.getSum();
        this.sum2 = transactionNameVO.getSum2();
        this.avg = transactionNameVO.getAvg();
        this.std = transactionNameVO.getStd();
        this.tps = transactionNameVO.getTps();
        this.line95Value = transactionNameVO.getLine95Value();
        this.line99Value = transactionNameVO.getLine99Value();
        this.ranges = transactionNameVO.getRanges();
        this.durations = transactionNameVO.getDurations();
        this.allDurations = transactionNameVO.getAllDurations();
    }

    public TransactionNameVO toTransactionNameVO() {
        TransactionNameVO transactionNameVO = new TransactionNameVO();
        
        transactionNameVO.setId(id);
        transactionNameVO.setName(name);
        transactionNameVO.setTotalCount(totalCount);
        transactionNameVO.setFailCount(failCount);
        transactionNameVO.setFailPercent(failPercent);
        transactionNameVO.setMin(min);
        transactionNameVO.setMax(max);
        transactionNameVO.setSum(sum);
        transactionNameVO.setSum2(sum2);
        transactionNameVO.setAvg(avg);
        transactionNameVO.setStd(std);
        transactionNameVO.setTps(tps);
        transactionNameVO.setLine95Value(line95Value);
        transactionNameVO.setLine99Value(line99Value);
        transactionNameVO.setRanges(ranges);
        transactionNameVO.setDurations(durations);
        transactionNameVO.setAllDurations(allDurations);

        return transactionNameVO;
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

    public Map<Integer, Range> getRanges() {
        return ranges;
    }

    public void setRanges(Map<Integer, Range> ranges) {
        this.ranges = ranges;
    }

    public Map<Integer, Duration> getDurations() {
        return durations;
    }

    public void setDurations(Map<Integer, Duration> durations) {
        this.durations = durations;
    }

    public Map<Integer, AllDuration> getAllDurations() {
        return allDurations;
    }

    public void setAllDurations(Map<Integer, AllDuration> allDurations) {
        this.allDurations = allDurations;
    }

}
