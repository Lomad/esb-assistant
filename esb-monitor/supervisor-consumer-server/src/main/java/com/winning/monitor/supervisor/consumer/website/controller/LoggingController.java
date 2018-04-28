package com.winning.monitor.supervisor.consumer.website.controller;

import com.winning.monitor.agent.logging.message.MessageTree;
import com.winning.monitor.supervisor.consumer.website.entity.LoggingEntity;
import com.winning.monitor.supervisor.consumer.website.entity.converter.MessageTreeConverter;
import com.winning.monitor.supervisor.consumer.website.webservice.entry.Converter;
import com.winning.monitor.supervisor.core.message.handle.MessageHandlerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;

/**
 * @Author Lemod
 * @Version 2017/9/15
 */
@Controller
@RequestMapping(value = "/monitor")
public class LoggingController {

    private static final Logger logger = LoggerFactory.getLogger(LoggingController.class);

    @Autowired
    private ApplicationContext applicationContext;

    private MessageHandlerManager manager;

    @PostConstruct
    public void init() {
        if (applicationContext != null) {
            ApplicationContext parent = applicationContext.getParent();
            this.manager = parent.getBean(MessageHandlerManager.class);
        } else {
            logger.error("初始化springMVC Ioc失败！", new NullPointerException("mvc ApplicationContext为空"));
        }
    }

    @RequestMapping(value = "/RESTLogging", method = RequestMethod.POST, consumes = {"application/json", "application/xml"})
    @ResponseBody
    public String doLogging(@RequestBody LoggingEntity request) {
        StringBuffer errorBuffer = new StringBuffer();
        if (request == null) {
            return "传入json有误！";
        }
        try {
            //xuehao 2018-03-30：将mainId转为数组
            Converter.parseDatalistValue(request.getTransactionCopy());

            MessageTree tree = MessageTreeConverter.converterLoggingEntity(request, errorBuffer);

            if (tree != null) {
                String error = manager.handle(tree);

                if (StringUtils.hasText(error)) {
                    MessageTreeConverter.insertErrorEntity(request, "当前埋点服务失效异常！开始时间：" + tree.getMessage().getTimestamp());
                    return error;
                }
                return "AA";
            } else {
                MessageTreeConverter.insertErrorEntity(request, errorBuffer.toString());
                return errorBuffer.toString();
            }
        } catch (Exception e) {
            MessageTreeConverter.insertErrorEntity(request, e.getMessage());
            return "AE" + e.getMessage();
        }
    }
}
