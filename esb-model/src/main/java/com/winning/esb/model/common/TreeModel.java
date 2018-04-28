package com.winning.esb.model.common;

import java.util.List;

/**
 * 树模型
 * @author xuehao 2017-07-28
 */
public class TreeModel {
    private List<TreeModel> children;
    private Object myData;

    public List<TreeModel> getChildren() {
        return children;
    }

    public void setChildren(List<TreeModel> children) {
        this.children = children;
    }

    public Object getMyData() {
        return myData;
    }

    public void setMyData(Object myData) {
        this.myData = myData;
    }
}