package com.winning.monitor.supervisor.consumer.website.entity.converter;

import com.google.gson.Gson;
import com.winning.monitor.agent.logging.message.MessageTree;
import com.winning.monitor.agent.logging.message.internal.DefaultMessageTree;
import com.winning.monitor.agent.logging.transaction.DefaultTransaction;
import com.winning.monitor.agent.logging.transaction.Transaction;
import com.winning.monitor.supervisor.consumer.website.entity.Data;
import com.winning.monitor.supervisor.consumer.website.entity.LoggingEntity;
import com.winning.monitor.supervisor.consumer.website.entity.RemoteCaller;
import com.winning.monitor.supervisor.consumer.website.entity.TransactionCopy;
import com.winning.monitor.utils.ApplicationContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.winning.monitor.utils.DateUtils.HOUR;

/**
 * @Author Lemod
 * @Version 2017/9/18
 */
public class MessageTreeConverter {
    //日志记录
    private static final Logger logger = LoggerFactory.getLogger(MessageTreeConverter.class);

    private static final String GROUP = "BI";

    private static Gson gson = new Gson();

    public static MessageTree converterLoggingEntity(LoggingEntity entity, StringBuffer error) throws ParseException {
        DefaultMessageTree tree = new DefaultMessageTree();

        /**
         * set {@link com.winning.monitor.agent.logging.entity.Domain}
         */
        String domain = entity.getSourceProvider();
        tree.setDomain(domain);
        tree.setGroup(GROUP);
        tree.setHostName(entity.getProviderHostName());
        tree.setIpAddress(entity.getProviderAddress());

        /**
         * set {@link com.winning.monitor.agent.logging.message.Caller}
         */
        RemoteCaller remoteCaller = entity.getRemoteCaller();
        tree.setCaller(remoteCaller.toCaller());

        tree.setMessageId(UUID.randomUUID().toString());

        TransactionCopy transactionCopy = entity.getTransactionCopy();

        Transaction parent = converterTransactionCopy(transactionCopy, error);
        if (parent != null) {
            tree.setMessage(parent);
            return tree;
        } else {
            return null;
        }
    }

    private static Transaction converterTransactionCopy(TransactionCopy copy, StringBuffer error) {
        String type = copy.getType() == null ? "" : copy.getType();
        String name = copy.getName() == null ? "" : copy.getName();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        Long startTime, endTime;
        try {
            startTime = dateFormat.parse(copy.getStartTime()).getTime();
            endTime = dateFormat.parse(copy.getEndTime()).getTime();
        } catch (ParseException e) {
            error.append("解析服务时间字段错误！");
            return null;
        }

        String status = copy.getStatus();

        DefaultTransaction transactionType = new DefaultTransaction(type, name, null);
        transactionType.setTimestamp(startTime);

        List<Data> dataList = copy.getDataList();
        if (dataList != null) {
            for (Data data : copy.getDataList()) {
                transactionType.addData(data.getKey(), data.getValue());
            }
        }

        long duration = endTime - startTime;
        if (duration < 0) {
            error.append("当前埋点服务耗时：" + duration + "ms为负！");
        } else if (duration > HOUR * 3) {
            error.append("当前埋点服务耗时：" + duration + "ms过大！");
        }
        transactionType.setDurationInMillis(duration);

        List<TransactionCopy> children = copy.getChildren();
        if (children != null && children.size() > 0) {
            for (TransactionCopy child : children) {
                Transaction trans = converterTransactionCopy(child, error);
                transactionType.addChild(trans);
            }
        }
        transactionType.setType(type);
        transactionType.setStatus(status);
        transactionType.setCompleted(true);

        return transactionType;
    }

    public static void insertErrorEntity(Object error, String errorType) {
        Map<String, Object> logging = new HashMap<>();

        MongoTemplate mongoTemplate = ApplicationContextUtils
                .getApplicationContext().getBean(MongoTemplate.class);
        if (mongoTemplate != null) {
            //logger.warn("耗时异常服务已入库！");
            logging.put("errorType", errorType);
            logging.put("异常消息体", error);
            try {
                mongoTemplate.insert(logging, "ErrorLoggingEntity");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
