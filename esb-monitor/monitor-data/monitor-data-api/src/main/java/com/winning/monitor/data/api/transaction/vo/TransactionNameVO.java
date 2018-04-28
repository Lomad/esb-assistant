package com.winning.monitor.data.api.transaction.vo;


import com.winning.monitor.data.api.vo.AllDuration;
import com.winning.monitor.data.api.vo.Duration;
import com.winning.monitor.data.api.vo.Range;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by nicholasyan on 16/9/12.
 */
public class TransactionNameVO {

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


    private Map<Integer, Range> ranges = new LinkedHashMap<>();
    private Map<Integer, Duration> durations = new LinkedHashMap<>();
    private Map<Integer, AllDuration> allDurations = new LinkedHashMap<>();


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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getFailPercent() {
        return failPercent;
    }

    public void setFailPercent(double failPercent) {
        this.failPercent = failPercent;
    }
}
