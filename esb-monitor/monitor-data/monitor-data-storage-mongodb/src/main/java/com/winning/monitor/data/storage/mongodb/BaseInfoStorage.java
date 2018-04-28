package com.winning.monitor.data.storage.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.winning.monitor.data.api.enums.TransactionReportsEnum;
import com.winning.monitor.data.storage.api.IBaseInfoStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BaseInfoStorage implements IBaseInfoStorage {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<String> loopProviderFromRealtimeReport(String group, String startTime, String endTime) {
        Query query = new Query();
        Criteria criteria = new Criteria("startTime").gte(startTime).lte(endTime);
        if(!StringUtils.isEmpty(group)) {
            criteria.and("group").is(group);
        }
        query.addCriteria(criteria);
        List<String> domains = this.mongoTemplate.getCollection(TransactionReportsEnum.COLLECTION_REALTIME)
                .distinct("domain", query.getQueryObject());
        return domains;
    }

    @Override
    public List<String> loopConsumerFromRealtimeReport(String group, String startTime, String endTime) {
        Query query = new Query();
        Criteria criteria = new Criteria("startTime").gte(startTime).lte(endTime);
        if(!StringUtils.isEmpty(group)) {
            criteria.and("group").is(group);
        }
        query.addCriteria(criteria);
        List<String> domains = this.mongoTemplate.getCollection(TransactionReportsEnum.COLLECTION_REALTIME)
                .distinct("machines.transactionClients.domain", query.getQueryObject());
        return domains;
    }

    @Override
    public Map<String, List<String>> loopSvcFromRealtimeReport(String group, Object appId, String startTime, String endTime) {
        Criteria criteria = new Criteria("startTime").gte(startTime).lte(endTime);
        //系统代码
        if (!StringUtils.isEmpty(appId)) {
            if(appId instanceof String) {
                criteria.and("domain").is(appId);
            } else if(appId instanceof List) {
                //必须将Object强制转为List，否则会导致查询无数据
                criteria.and("domain").in((List) appId);
            }
        }
        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(Aggregation.match(criteria));
        operations.add(Aggregation.unwind("machines"));
        operations.add(Aggregation.unwind("machines.transactionClients"));
        operations.add(Aggregation.unwind("machines.transactionClients.transactionTypes"));
        Fields fields = Fields.from(Fields.field("appId", "domain"),
                Fields.field("svcCode", "machines.transactionClients.transactionTypes._id"));
        operations.add(Aggregation.group(fields));
        Aggregation agg = Aggregation.newAggregation(operations);
        AggregationResults<BasicDBObject> arts = mongoTemplate.aggregate(agg, TransactionReportsEnum.COLLECTION_REALTIME,
                BasicDBObject.class);
        Map<String, List<String>> result;
        if (arts.getMappedResults().size() > 0) {
            result = new HashMap<>();
            Object app, svc;
            for (DBObject obj : arts.getMappedResults()) {
                app = obj.get("appId");
                svc = obj.get("svcCode");
                if(!StringUtils.isEmpty(app) && !StringUtils.isEmpty(svc)) {
                    if(!result.containsKey(app)) {
                        result.put(app.toString(), new ArrayList<>());
                    }
                    if(!result.get(app).contains(svc.toString())) {
                        result.get(app).add(svc.toString());
                    }
                }
            }
        } else {
            result = null;
        }
        return result;
    }

}