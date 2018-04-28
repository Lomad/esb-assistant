package com.winning.monitor.data.storage.mongodb.po.transaction;

import com.winning.monitor.data.api.transaction.vo.TransactionNameVO;
import com.winning.monitor.data.api.transaction.vo.TransactionTypeVO;
import com.winning.monitor.data.api.vo.AllDuration;
import com.winning.monitor.data.api.vo.Range2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by nicholasyan on 16/9/18.
 */
public class TransactionTypePO {

    private String id;

    private String name;

    private String router;//新增字段：消息中间路由

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
    private List<TransactionNamePO> transactionNames = new ArrayList<>();
    private Map<Integer, Range2> range2s;
    private Map<Integer, AllDuration> allDurations;

    public TransactionTypePO() {

    }

    public TransactionTypePO(TransactionTypeVO transactionTypeVO) {
        this.id = transactionTypeVO.getId();
        this.name = transactionTypeVO.getName();
        this.router = transactionTypeVO.getRouter();
        this.totalCount = transactionTypeVO.getTotalCount();
        this.failCount = transactionTypeVO.getFailCount();
        this.failPercent = transactionTypeVO.getFailPercent();
        this.min = transactionTypeVO.getMin();
        this.max = transactionTypeVO.getMax();
        this.sum = transactionTypeVO.getSum();
        this.sum2 = transactionTypeVO.getSum2();
        this.avg = transactionTypeVO.getAvg();
        this.std = transactionTypeVO.getStd();
        this.tps = transactionTypeVO.getTps();
        this.line95Value = transactionTypeVO.getLine95Value();
        this.line99Value = transactionTypeVO.getLine99Value();
        this.range2s = transactionTypeVO.getRange2s();
        this.allDurations = transactionTypeVO.getAllDurations();
        if (transactionTypeVO.getTransactionNames() != null) {
            for (TransactionNameVO transactionNameVO : transactionTypeVO.getTransactionNames()) {
                this.transactionNames.add(new TransactionNamePO(transactionNameVO));
            }
        }
    }

    public TransactionTypeVO toTransactionTypeVO() {
        TransactionTypeVO transactionTypeVO = new TransactionTypeVO();
        transactionTypeVO.setId(id);
        transactionTypeVO.setName(name);
        transactionTypeVO.setRouter(router);
        transactionTypeVO.setTotalCount(totalCount);
        transactionTypeVO.setFailCount(failCount);
        transactionTypeVO.setFailPercent(failPercent);

        transactionTypeVO.setMin(min);
        transactionTypeVO.setMax(max);
        transactionTypeVO.setSum(sum);
        transactionTypeVO.setSum2(sum2);
        transactionTypeVO.setAvg(avg);
        transactionTypeVO.setStd(std);
        transactionTypeVO.setTps(tps);
        transactionTypeVO.setLine95Value(line95Value);
        transactionTypeVO.setLine99Value(line99Value);
        transactionTypeVO.setRange2s(range2s);
        transactionTypeVO.setAllDurations(allDurations);

        List<TransactionNameVO> transactionNames = new ArrayList<>();

        if (this.getTransactionNames() != null) {
            for (TransactionNamePO transactionNamePO : this.getTransactionNames()) {
                transactionNames.add(transactionNamePO.toTransactionNameVO());
            }
        }

        transactionTypeVO.setTransactionNames(transactionNames);

        return transactionTypeVO;
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

    public List<TransactionNamePO> getTransactionNames() {
        return transactionNames;
    }

    public void setTransactionNames(List<TransactionNamePO> transactionNames) {
        this.transactionNames = transactionNames;
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
}
