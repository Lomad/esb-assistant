package com.winning.monitor.data.api.transaction.domain;

import java.util.LinkedHashMap;

/**
 * Created by nicholasyan on 16/10/20.
 */
public class TransactionCallTimesReport {

    //持续时间内的调用次数结果集,Key代表分钟数或日期数,Value代表调用的次数
    private LinkedHashMap<String, Long> durations;

    public LinkedHashMap<String, Long> getDurations() {
        return durations;
    }

    public void setDurations(LinkedHashMap<String, Long> durations) {
        this.durations = durations;
    }
}
