package com.winning.monitor.data.storage.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.winning.esb.utils.CalculateUtils;
import com.winning.esb.utils.StringUtils;
import com.winning.monitor.data.storage.Utils.ConvertUtils;
import com.winning.monitor.data.storage.api.IErrorOverViewStorage;
import com.winning.monitor.data.storage.api.entity.MessageTreeList;
import com.winning.monitor.data.storage.mongodb.po.message.MessageTreePO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.winning.monitor.data.api.enums.TransactionReportsEnum.MESSAGE_TREE_TIMESTAMP;
import static com.winning.monitor.data.storage.Utils.ConvertUtils.GetCollectionName;

/**
 * Created by nicholasyan on 16/9/14.
 */
@Service
public class ErrorOverViewStorage implements IErrorOverViewStorage {
    private final String REALTIME_COLLECTION_NAME = "TransactionRealtimeReports";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<String> countErrorProviders(Map<String, Object> map) {
        Object startTime = map.get("startTime");
        Object endTime = map.get("endTime");
        List<AggregationOperation> operations = new ArrayList<>();
        Criteria criteria = new Criteria("startTime").gte(startTime).lte(endTime);
        operations.add(Aggregation.match(criteria));
        operations.add(Aggregation.unwind("machines"));
        operations.add(Aggregation.unwind("machines.transactionClients"));
        operations.add(Aggregation.unwind("machines.transactionClients.transactionTypes"));
        operations.add(Aggregation.match(new Criteria("machines.transactionClients.transactionTypes.failCount").gt(0)));
        operations.add(Aggregation.group("domain"));
        Aggregation agg = Aggregation.newAggregation(operations);
        AggregationResults<BasicDBObject> arts = mongoTemplate.aggregate(agg, REALTIME_COLLECTION_NAME, BasicDBObject.class);
        List<String> result;
        if (arts.getMappedResults().size() > 0) {
            result = new ArrayList<>();
            for (DBObject obj : arts.getMappedResults()) {
                result.add(obj.get("_id").toString());
            }
        } else {
            result = null;
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> countErrorList(Map<String, Object> map) {
        Object startTime = map.get("startTime");
        Object endTime = map.get("endTime");
        Object appId = map.get("appId");
        Object appIdList = map.get("appIdList");
        Object svcCode = map.get("svcCode");
        Object svcCodeList = map.get("svcCodeList");
        List<AggregationOperation> operations = new ArrayList<>();
        Criteria criteria = new Criteria("startTime").gte(startTime).lte(endTime);
        operations.add(Aggregation.match(criteria));
        operations.add(Aggregation.unwind("machines"));
        operations.add(Aggregation.unwind("machines.transactionClients"));
        operations.add(Aggregation.unwind("machines.transactionClients.transactionTypes"));
        operations.add(Aggregation.match(new Criteria("machines.transactionClients.transactionTypes.failCount").gt(0)));
        //系统代码
        if (!StringUtils.isEmpty(appId)) {
            operations.add(Aggregation.match(new Criteria("domain").is(appId)));
        }
        //系统代码列表
        if (!StringUtils.isEmpty(appIdList)) {
            //必须将Object强制转为List，否则会导致查询无数据
            operations.add(Aggregation.match(new Criteria("domain").in((List) appIdList)));
        }
        //服务代码
        if (!StringUtils.isEmpty(svcCode)) {
            operations.add(Aggregation.match(new Criteria("machines.transactionClients.transactionTypes._id").is(svcCode)));
        }
        //服务代码列表
        if (!StringUtils.isEmpty(svcCodeList)) {
            //必须将Object强制转为List，否则会导致查询无数据
            operations.add(Aggregation.match(new Criteria("machines.transactionClients.transactionTypes._id").in((List) svcCodeList)));
        }
        operations.add(Aggregation.group("machines.transactionClients.transactionTypes._id")
                .sum("machines.transactionClients.transactionTypes.totalCount").as("totalCount")
                .sum("machines.transactionClients.transactionTypes.failCount").as("failCount"));
        Aggregation agg = Aggregation.newAggregation(operations);
        AggregationResults<BasicDBObject> arts = mongoTemplate.aggregate(agg, REALTIME_COLLECTION_NAME, BasicDBObject.class);
        List<Map<String, Object>> result;
        if (arts.getMappedResults().size() > 0) {
            result = new ArrayList<>();
            Map<String, Object> tempMap;
            Object totalCount, failCount;
            String failPercent;
            for (DBObject obj : arts.getMappedResults()) {
                totalCount = obj.get("totalCount").toString();
                failCount = obj.get("failCount").toString();
                failPercent = String.format("%s%%",
                        CalculateUtils.round(CalculateUtils.div(failCount, totalCount, 2) * 100, 2));

                tempMap = new HashMap();
                tempMap.put("svcCode", obj.get("_id"));
                tempMap.put("totalCount", totalCount);
                tempMap.put("failCount", failCount);
                tempMap.put("failPercent", failPercent);
                result.add(tempMap);
            }
        } else {
            result = null;
        }
        return result;
    }

    @Override
    public MessageTreeList queryTodayErrorMessageList(String serverId, long lowerTimestamp, String keyWords,
                                                      int startIndex, int pageSize) {
        Criteria criteria = Criteria.where(MESSAGE_TREE_TIMESTAMP).gte(lowerTimestamp);
        criteria.and("messageTree.message.status").ne("0");

        //添加关键字
        ConvertUtils.addKeywordToQuery(criteria, keyWords);

        Query query = Query.query(criteria);
        query.skip(startIndex).limit(pageSize);

//        System.out.println(query.toString());   //测试

        Query query1 = Query.query(criteria);

//        System.out.println(query1.toString());   //测试

        long totalCount = mongoTemplate.count(query1, GetCollectionName(serverId));

        List<MessageTreePO> treePOList = mongoTemplate
                .find(query, MessageTreePO.class, GetCollectionName(serverId));
        MessageTreeList messageTreeList = new MessageTreeList();
        messageTreeList.setTotalSize(totalCount);
        if (!CollectionUtils.isEmpty(treePOList)) {
            for (MessageTreePO messageTreePO : treePOList) {
                messageTreeList.addMessageTree(messageTreePO.getMessageTree());
            }
            //逆序 o1.time>=o2.time返回-1
            treePOList.sort((o1, o2) -> {
                long com1 = o1.getMessageTree().getMessage().getTimestamp();
                long com2 = o2.getMessageTree().getMessage().getTimestamp();
                return com1 >= com2 ? -1 : 1;
            });

        }
        return messageTreeList;
    }
}