package com.winning.esb.controller.monitor.model;

/**
 * Created by xuehao on 2017/3/28.
 * 大屏接口类型的枚举
 */
public enum BigScreenEnum {

    CDR("CDR"),
    ODS("ODS"),
    ODR("ODR"),
    BU("BU"),//备份库
    WDK("WDK");//文档库

    private String code;
    private BigScreenEnum(String code) {
        this.code=code;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
}