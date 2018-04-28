package com.winning.monitor.data.api.transaction.domain;

import com.winning.monitor.message.Message;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by sao something on 2016/11/16.
 */
public class TransactionMessageListDetail {
    private Map<String, String> data;

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

}
