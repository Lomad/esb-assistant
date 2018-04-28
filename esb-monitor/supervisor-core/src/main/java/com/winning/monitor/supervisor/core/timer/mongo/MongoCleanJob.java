package com.winning.monitor.supervisor.core.timer.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.winning.esb.model.ConfigsModel;
import com.winning.esb.model.enums.ConfigsCodeConst;
import com.winning.esb.service.IConfigsService;
import com.winning.esb.utils.MapUtils;
import com.winning.esb.utils.StringUtils;
import com.winning.monitor.agent.logging.message.MessageTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.winning.monitor.data.api.enums.TransactionReportsEnum.MESSAGE_TREE_TIMESTAMP;
import static com.winning.monitor.data.api.enums.TransactionReportsEnum.TOP_DURATION;

/**
 * @Author Lemod
 * @Version 2017/5/8
 */
public class MongoCleanJob implements CleanJob {

    private static final Logger logger = LoggerFactory.getLogger(MongoCleanJob.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IConfigsService configsService;

    private static final long DAY = 24 * 60 * 60 * 1000L;

    @PostConstruct
    public void checkConnect() {
        logger.info("正在检测与mongodb之间的连接,address={}", mongoTemplate.getDb().getMongo().getAddress());
        try {
            this.mongoTemplate.getDb().getStats();
        } catch (Exception e) {
            logger.error("连接mongodb时发生错误{}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public int remove(long period, String domain) {
        Query query = new Query();
        query.addCriteria(new Criteria("messageTree.message.timestampInMillis").lte(period));
        WriteResult result = null;
        try {
            result = mongoTemplate.remove(query, domain);
        } catch (Exception e) {
            logger.error("删除" + domain + "中数据失败", e);
        }
        return result.getN();
    }

    @Override
    public long getClearDate() {
        //获取保留时长
        ConfigsModel configsModel = configsService.getByCode(ConfigsCodeConst.MonitorClearPeriod);
        String period = (configsModel == null || !StringUtils.isInt(configsModel.getValue())) ? "180" : configsModel.getValue();

        //计算清理日期
        long timeLine = Long.parseLong(period) * DAY;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis() - timeLine;
    }

    @Override
    public void ensureIndex() {
        Set<String> collectionNames = mongoTemplate.getCollectionNames();
        DBObject indexOption = new BasicDBObject("background", true);
        indexOption.put("spare", true);
        for (String name : collectionNames) {
            if (name.startsWith("Messages-")) {
                DBCollection collection = mongoTemplate.getCollection(name);
                if (queryIndexCount(collection) <= 1) {
                    logger.warn("当前建索引表为:" + name);
                    DBObject typeIndex = new BasicDBObject("messageTree.message.type", 1);
                    DBObject timeIndex = new BasicDBObject("messageTree.message.timestampInMillis", -1);
                    collection.createIndex(typeIndex, indexOption);
                    collection.createIndex(timeIndex, indexOption);

                    //xuehao 2018-03-23：由于mainId保存的是关键ID信息，许多情况下都是增长的，因此按照降序建立索引，可以保持较新的ID在前面
                    DBObject mainIdIndex = new BasicDBObject("messageTree.message.data.mainId", -1);
                    collection.createIndex(mainIdIndex, indexOption);

                    //xuehao 2018-03-23：新增以下两个，需要确认“messageTree.message.data.MessageID”与“messageId”是否一样，如果是，则保留一个即可
                    DBObject callerName = new BasicDBObject("messageTree.caller.name", -1);
                    collection.createIndex(callerName, indexOption);
                    DBObject MessageID = new BasicDBObject("messageTree.message.data.MessageID", -1);
                    collection.createIndex(MessageID, indexOption);

                    collection.createIndex("messageId");
                }
            } else if (name.startsWith("Transaction")) {
                DBCollection collection = mongoTemplate.getCollection(name);
                if (queryIndexCount(collection) <= 1) {
                    logger.warn("当前建索引表为:" + name);
                    DBObject domainIndex = new BasicDBObject("domain", 1);
                    DBObject clientDomainIndex = new BasicDBObject("machines.transactionClients.domain", 1);
                    DBObject timeIndex = new BasicDBObject("startTime", 1);
                    DBObject typeIndex = new BasicDBObject("machines.transactionClients.transactionTypes.name", 1);
                    collection.createIndex(domainIndex, indexOption);
                    collection.createIndex(clientDomainIndex, indexOption);
                    collection.createIndex(timeIndex, indexOption);
                    collection.createIndex(typeIndex, indexOption);
                }
            }
        }
    }

    @Override
    public void removeErrorLoggingEntity() {
        boolean isExists = mongoTemplate.collectionExists("ErrorLoggingEntity");
        if (isExists) {
            mongoTemplate.dropCollection("ErrorLoggingEntity");
        }
    }

    @Override
    public void storeTopDuration(Map<Long, MessageTree> treeMap) {
        if (MapUtils.isEmpty(treeMap)) {
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long todayTimeStamp = calendar.getTimeInMillis();
        Query remove = Query.query(
                new Criteria(MESSAGE_TREE_TIMESTAMP).lt(todayTimeStamp));
        mongoTemplate.remove(remove, TOP_DURATION);

        List<Map.Entry<Long, MessageTree>> entryList
                = new ArrayList<>(treeMap.entrySet());
        entryList.sort(Comparator.comparingLong(Map.Entry::getKey));

        if (treeMap.containsKey(Long.MAX_VALUE)) {
            String diedTreeId = treeMap.get(Long.MAX_VALUE).getMessageId();
            remove = Query.query(new Criteria("messageId").is(diedTreeId));
            mongoTemplate.remove(remove, TOP_DURATION);
            //及时同步内存中所存明细
            treeMap.entrySet().removeIf(entry -> entry.getKey().equals(Long.MAX_VALUE));
        }

        for (MessageTree tree : treeMap.values()) {
            String messageId = tree.getMessageId();
            Query existQuery = Query.query(Criteria.where("messageId").is(messageId));

            Update insertUpdate = Update.update("messageId", messageId);
            insertUpdate.set("domain", tree.getDomain());
            insertUpdate.set("group", tree.getGroup());
            insertUpdate.set("ipAddress", tree.getIpAddress());
            insertUpdate.set("messageTree", tree);
            mongoTemplate.upsert(existQuery, insertUpdate, TOP_DURATION);
        }
    }

    private int queryIndexCount(DBCollection collection) {
        return collection.getIndexInfo().size();
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

}
