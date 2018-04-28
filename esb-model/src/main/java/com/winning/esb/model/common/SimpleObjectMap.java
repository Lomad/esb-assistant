package com.winning.esb.model.common;

import java.util.List;
import java.util.Map;

/**
 * xuehao 2017-07-27：新增；
 * 主要用于传输简单通用Map对象
 */
public class SimpleObjectMap {
    private Map<String, List<SimpleObject>> map;

    public Map<String, List<SimpleObject>> getMap() {
        return map;
    }

    public void setMap(Map<String, List<SimpleObject>> map) {
        this.map = map;
    }
}