package com.winning.monitor.data.storage.mongodb;

import com.mongodb.WriteResult;
import com.winning.monitor.agent.logging.message.MessageTree;
import com.winning.monitor.data.api.base.common.CommonObject;
import com.winning.monitor.data.api.transaction.vo.TransactionMachineVO;
import com.winning.monitor.data.api.transaction.vo.TransactionReportVO;
import com.winning.monitor.data.storage.api.IAsynchronousLogMessageStorage;
import com.winning.monitor.data.storage.mongodb.po.message.MessageTreePO;
import com.winning.monitor.data.storage.mongodb.po.transaction.TransactionMachinePO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Lemod
 * @Version 2017/9/13
 */
@Service
public class AsynchronousLogMessageStorage implements IAsynchronousLogMessageStorage {

    private static final Logger logger = LoggerFactory.getLogger(AsynchronousLogMessageStorage.class);
    private static final String REPORT_COLLECTION = "TransactionRealtimeReports";
    private static final String TEMPORARY_COLLECTION = "Temporary-Messages";

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void checkConnect() {
        logger.info("正在检测与mongodb之间的连接,address={}", mongoTemplate.getDb().getMongo().getAddress());
        try {
            this.mongoTemplate.getDb().getStats();
        } catch (Exception e) {
            logger.error("连接mongodb时发生错误{}", e.getMessage(), e);
            throw e;
        }
        logger.info("与mongodb之间的连接成功!");
    }

    @Override
    public void storeTemporaryTree(MessageTree tree) {
        if (tree == null) {
            return;
        }

        MessageTreePO messageTreePO = new MessageTreePO(tree);
        this.mongoTemplate.insert(messageTreePO, TEMPORARY_COLLECTION);
    }

    @Override
    public List<CommonObject> distinctPatentMessageID() {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.sort(Sort.Direction.DESC, "messageTree.message.timestampInMillis"),
                Aggregation.limit(300),
                Aggregation.group("domain")
                        .addToSet("messageTree.message.data.ParentMessageID")
                        .as("idList")
        );

        AggregationResults<CommonObject> results = mongoTemplate.aggregate(agg, TEMPORARY_COLLECTION, CommonObject.class);

        return results.getMappedResults();
    }

    @Override
    public MessageTree queryParent(String messageID, String domain) {
        Query query = Query.query(new Criteria("messageTree.message.headerIdList").is(messageID));

        MessageTreePO parentTree = mongoTemplate.findOne(query, MessageTreePO.class, "Messages-" + domain);

        return parentTree.getMessageTree();
    }

    @Override
    public List<MessageTree> queryChildren(String parentMessageID) {
        List<MessageTree> treeList = new ArrayList<>();

        Query query = Query.query(new Criteria("messageTree.message.data.ParentMessageID").is(parentMessageID));
        List<MessageTreePO> poList = mongoTemplate.find(query, MessageTreePO.class, TEMPORARY_COLLECTION);

        for (MessageTreePO po : poList) {
            if (po != null) {
                treeList.add(po.getMessageTree());
            }
        }
        return treeList;
    }

    @Override
    public int removeDiscardedMessageTree(String messageId) {
        Query query = Query.query(
                new Criteria("messageTree.message.data.ParentMessageID")
                        .is(messageId));
        WriteResult result = mongoTemplate.remove(query, TEMPORARY_COLLECTION);

        return result.getN();
    }

    @Override
    public int updateMessageTreePO(MessageTree tree, String domain) {
        Query query = Query.query(new Criteria("messageId").is(tree.getMessageId()));

        Update update = Update.update("messageTree.message", tree.getMessage());

        WriteResult result = mongoTemplate.updateFirst(query, update, MessageTreePO.class, "Messages-" + domain);
        return result.getN();
    }

    @Override
    public boolean updateRealTimeReport(TransactionReportVO reportVO) {
        String domain = reportVO.getDomain(),
                group = reportVO.getGroup(),
                startTime = reportVO.getStartTime();

        List<TransactionMachinePO> poList = new ArrayList<>();
        for (TransactionMachineVO machineVO : reportVO.getMachines()) {
            poList.add(new TransactionMachinePO(machineVO));
        }

        Query query = Query.query(new Criteria("domain").is(domain).
                and(group).is(group).and("startTime").is(startTime));

        Update update = new Update();
        update.set("machines", poList);

        WriteResult result = mongoTemplate.updateFirst(query, update, REPORT_COLLECTION);

        return result.isUpdateOfExisting();
    }
}
