package com.winning.monitor.data.api.base.common;

/**
 * xuehao 2017-07-25：新增；
 * 主要用于传输简单通用对象
 */
public class SimpleObject {
    private String item1;
    private String item2;

    public SimpleObject() {

    }
    public SimpleObject(String item1, String item2) {
        this.item1 = item1;
        this.item2 = item2;
    }

    public String getItem1() {
        return item1;
    }
    public void setItem1(String item1) {
        this.item1 = item1;
    }
    public String getItem2() {
        return item2;
    }
    public void setItem2(String item2) {
        this.item2 = item2;
    }
}