package com.winning.monitor.restfulservice.mapping;

import com.alibaba.fastjson.JSON;
import com.winning.monitor.webservice.logging.AgentLogging;
import com.winning.monitor.webservice.request_entity.LoggingEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author Lemod
 * @Version 2017/4/5
 * 埋点restful接口
 */
@Controller
@RequestMapping("/monitor-service")
public class RestfulService {

    private static Logger logger = LoggerFactory.getLogger(RestfulService.class);

    @RequestMapping(value = "/logging", method = RequestMethod.POST, produces = "application/json")
    public
    @ResponseBody
    String logging(@RequestBody LoggingEntity loggingEntity) {
        AgentLogging agentLogging = new AgentLogging();
        StringBuffer res = new StringBuffer();

        try {
            StringBuffer error = new StringBuffer();
            if (loggingEntity == null) {
                error.append("输入的JSON为空或格式有误！");
            } else {
                res = agentLogging.mainLogging(loggingEntity, error);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            res.append(e.getMessage());
        }
        return JSON.toJSONString(res);
    }
}
