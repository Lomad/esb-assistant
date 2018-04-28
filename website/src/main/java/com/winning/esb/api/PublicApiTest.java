package com.winning.esb.api;

import com.winning.esb.utils.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 公共服务测试接口，主要用于测试管理平台是否启动成功，或用于需要调用Restful接口的场景
 * 消费方调用时【content-type最好设置为“text/plain;Charset=UTF-8”】
 *
 * @author xuehao
 * @date 2018/01/30
 */
@Controller
@RequestMapping(value = {"/api-test", "/api-test/"})
public class PublicApiTest {
    /**
     * hello函数
     */
    @RequestMapping(value = "hello", method = RequestMethod.POST)
    @ResponseBody
    public String hello(@RequestBody String msg) {
        String result;
        try {
            result = "您调用了hello接口，输入信息：" + msg;
        } catch (Exception ex) {
            result = "发生异常错误！" + ex.getMessage();
        }

        return result;
    }

    /**
     * 获取当前时间
     *
     * @param type 0 - 获取当前精确到毫秒的日期时间，1 - 获取当前精确到秒的日期时间，2 - 获取当前日期，3 - 获取当前时间
     */
    @RequestMapping(value = "getDateTime", method = RequestMethod.POST)
    @ResponseBody
    public String getDateTime(@RequestBody String type) {
        String result;
        try {
            if ("1".equalsIgnoreCase(type)) {
                result = "当前日期时间（秒级）：" + DateUtils.getCurrentDatetimeString();
            } else if ("2".equalsIgnoreCase(type)) {
                result = "当前日期：" + DateUtils.getCurrentDateString();
            } else if ("3".equalsIgnoreCase(type)) {
                result = "当前时间：" + DateUtils.getCurrentTimeString();
            } else {
                result = "当前日期时间（毫秒级）：" + DateUtils.getCurrentDatetimeMiliSecondString();
            }
        } catch (Exception ex) {
            result = "发生异常错误！" + ex.getMessage();
        }

        return result;
    }


}