package com.winning.monitor.data.api.transaction;


import com.winning.monitor.data.api.transaction.vo.TransactionNameVO;
import com.winning.monitor.data.api.transaction.vo.TransactionTypeVO;
import com.winning.monitor.data.api.vo.AllDuration;
import com.winning.monitor.data.api.vo.Range;
import com.winning.monitor.data.api.vo.Range2;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class TransactionStatisticsComputer {

    public double m_duration = 3600;

    private double computeLineValue(Map<Integer, AllDuration> durations, double percent) {
        int totalCount = 0;
        Map<Integer, AllDuration> sorted = new TreeMap<Integer, AllDuration>(TransactionComparator.DESC);

        sorted.putAll(durations);

        for (AllDuration duration : durations.values()) {
            totalCount += duration.getCount();
        }

        int remaining = (int) (totalCount * (100 - percent) / 100);

        for (Entry<Integer, AllDuration> entry : sorted.entrySet()) {
            remaining -= entry.getValue().getCount();

            if (remaining <= 0) {
                return entry.getKey();
            }
        }

        return 0.0;
    }

    public TransactionStatisticsComputer setDuration(double duration) {
        m_duration = duration;
        return this;
    }

    double std(long count, double avg, double sum2, double max) {
        double value = sum2 / count - avg * avg;

        if (value <= 0 || count <= 1) {
            return 0;
        } else if (count == 2) {
            return max - avg;
        } else {
            return Math.sqrt(value);
        }
    }

    public void calcTransactionName(TransactionNameVO name) {
        long count = name.getTotalCount();

        if (count > 0) {
            long failCount = name.getFailCount();
            double avg = name.getSum() / count;
            double std = std(count, avg, name.getSum2(), name.getMax());
            double failPercent = 100.0 * failCount / count;

            name.setFailPercent(failPercent);
            name.setAvg(avg);
            name.setStd(std);

            double line95 = computeLineValue(name.getAllDurations(), 95);
            double line999 = computeLineValue(name.getAllDurations(), 99.9);
            name.setLine95Value(line95);
            name.setLine99Value(line999);
        }
        if (m_duration > 0) {
            name.setTps(name.getTotalCount() * 1.0 / m_duration);
        }
    }


    public void calcRange(Range range) {
        if (range.getCount() > 0) {
            range.setAvg(range.getSum() / range.getCount());
        }
    }

    public void calcRange2(Range2 range) {
        if (range.getCount() > 0) {
            range.setAvg(range.getSum() / range.getCount());
        }
    }

    public void calcTransactionType(TransactionTypeVO type) {

        long count = type.getTotalCount();

        if (count > 0) {
            long failCount = type.getFailCount();
            double avg = type.getSum() / count;
            double std = std(count, avg, type.getSum2(), type.getMax());
            double failPercent = 100.0 * failCount / count;

            type.setFailPercent(failPercent);
            type.setAvg(avg);
            type.setStd(std);

            double line95 = computeLineValue(type.getAllDurations(), 95);
            double line999 = computeLineValue(type.getAllDurations(), 99.9);
            type.setLine95Value(line95);
            type.setLine99Value(line999);

            if (m_duration > 0) {
                type.setTps(type.getTotalCount() * 1.0 / m_duration);
            }
        }
    }

    private static enum TransactionComparator implements Comparator<Integer> {
        DESC {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        }
    }
}