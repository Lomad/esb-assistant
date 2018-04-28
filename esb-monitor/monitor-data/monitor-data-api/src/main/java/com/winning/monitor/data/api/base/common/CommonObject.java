package com.winning.monitor.data.api.base.common;

import java.util.Collection;

/**
 * xuehao 2017-07-25：新增；
 * 主要用于传输通用对象
 */
public class CommonObject {
    private Object data;
    private Collection datas;
    private int totalSize;

    public Collection getDatas() {
        return this.datas;
    }

    public void setDatas(Collection datas) {
        this.datas = datas;
    }

    public int getTotalSize() {
        return this.totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}