package com.winning.esb.model.common;

import java.util.List;

/**
 * 通用的结果对象
 * @author xuehao
 */
public class ResultObject<T> {
    private boolean success;
    private String errorMsg;
    private T obj;
    private List<T> objs;

    public ResultObject() {
    }

    public ResultObject(boolean success, String errorMsg, T obj) {
        this.success = success;
        this.errorMsg = errorMsg;
        this.obj = obj;
    }

    public ResultObject(boolean success, String errorMsg, List<T> objs) {
        this.success = success;
        this.errorMsg = errorMsg;
        this.objs = objs;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public List<T> getObjs() {
        return objs;
    }

    public void setObjs(List<T> objs) {
        this.objs = objs;
    }
}