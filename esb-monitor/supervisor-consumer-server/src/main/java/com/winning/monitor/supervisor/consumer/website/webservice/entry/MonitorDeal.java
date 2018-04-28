package com.winning.monitor.supervisor.consumer.website.webservice.entry;

import com.winning.monitor.agent.logging.message.MessageTree;
import com.winning.monitor.supervisor.consumer.website.entity.LoggingEntity;
import com.winning.monitor.supervisor.consumer.website.entity.converter.MessageTreeConverter;
import com.winning.monitor.supervisor.core.message.handle.MessageHandlerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static com.winning.monitor.supervisor.consumer.website.entity.converter.MessageTreeConverter.insertErrorEntity;
import static com.winning.monitor.supervisor.consumer.website.webservice.entry.Converter.CheckForLegal;
import static com.winning.monitor.supervisor.consumer.website.webservice.entry.Converter.serialize;

/**
 * @author Lemod
 * @version 2016/11/26
 */
@Service
public class MonitorDeal {

    private static final Logger logger = LoggerFactory.getLogger(MonitorDeal.class);

    @Autowired
    private MessageHandlerManager manager;

    public String monitorLogging(String xml) {
        StringBuffer result = new StringBuffer();
        try {
            LoggingEntity entity = serialize(xml);

            //检查传入的xml是否缺少必须字段
            result.append(CheckForLegal(entity));

            if (StringUtils.hasText(result)) {
                insertErrorEntity(entity, result.toString());
                return result.toString();
            }
            MessageTree tree = MessageTreeConverter.converterLoggingEntity(entity, result);
            result.append(manager.handle(tree));

            if (StringUtils.hasText(result)) {
                result.append("当前埋点服务失效异常！开始时间：" + tree.getMessage().getTimestamp());
                insertErrorEntity(entity, result.toString());
                return result.toString();
            }
            return "AA";
        } catch (Exception e) {
            result.append("webservice埋点失败，可能XML格式有误，请参考接口文档检查..." + e.getMessage());
            logger.error(result.toString(), e);

            //保存错误信息
            try {
                insertErrorEntity(xml, result.toString());
            } catch (Exception ex) {
            }

            return result.toString();
        }
    }
}
