package com.winning.esb.model.ext;

import com.winning.esb.model.ValueListModel;

import java.util.List;

/**
 * Created by xuehao on 2017/8/9.
 */
public class ValueListExtModel {
    /**
     * 使用换行符拼接候选值
     */
    private String strValueList;
    /**
     * 使用换行符拼接候选值
     */
    private String strValueListFailure;
    private List<String> valueList;
    private List<String> valueListFailure;

    public String getStrValueList() {
        return strValueList;
    }

    public void setStrValueList(String strValueList) {
        this.strValueList = strValueList;
    }

    public String getStrValueListFailure() {
        return strValueListFailure;
    }

    public void setStrValueListFailure(String strValueListFailure) {
        this.strValueListFailure = strValueListFailure;
    }

    public List<String> getValueList() {
        return valueList;
    }

    public void setValueList(List<String> valueList) {
        this.valueList = valueList;
    }

    public List<String> getValueListFailure() {
        return valueListFailure;
    }

    public void setValueListFailure(List<String> valueListFailure) {
        this.valueListFailure = valueListFailure;
    }
}
