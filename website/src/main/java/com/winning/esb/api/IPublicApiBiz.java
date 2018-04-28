package com.winning.esb.api;

import com.winning.webapp.framework.core.api.response.message.AjaxResponseMessage;

import java.util.LinkedHashMap;

/**
 * Created by xuehao on 2017/11/17.
 * 公共的业务接口
 */
public interface IPublicApiBiz {
    AjaxResponseMessage handle(Object obj) throws Exception;
}