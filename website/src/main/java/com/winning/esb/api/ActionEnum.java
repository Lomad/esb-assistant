package com.winning.esb.api;

/**
 * Created by xuehao on 2017/10/26.
 */
public enum ActionEnum {
    /**
     * 注册服务
     */
    RegisterService("RegisterService"),
    /**
     * 接收信息
     */
    ReceiveMsg("ReceiveMsg"),
    /**
     * 接收ESB发来的异常
     */
    ReceiveException("ReceiveException");

    private String code;
    ActionEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}