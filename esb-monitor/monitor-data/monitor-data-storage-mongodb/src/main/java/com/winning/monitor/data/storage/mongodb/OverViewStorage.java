package com.winning.monitor.data.storage.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.winning.esb.utils.StringUtils;
import com.winning.monitor.data.api.base.ServerCountWithType;
import com.winning.monitor.data.api.base.ServiceDurationStatisticVO;
import com.winning.monitor.data.api.enums.DateType;
import com.winning.monitor.data.api.transaction.vo.TransactionClientVO;
import com.winning.monitor.data.api.transaction.vo.TransactionMachineVO;
import com.winning.monitor.data.api.transaction.vo.TransactionReportVO;
import com.winning.monitor.data.api.transaction.vo.TransactionTypeVO;
import com.winning.monitor.data.api.vo.Range2;
import com.winning.monitor.data.storage.api.IOverViewStorage;
import com.winning.monitor.data.storage.api.entity.MessageTreeList;
import com.winning.monitor.data.storage.mongodb.po.message.MessageTreePO;
import com.winning.monitor.utils.DateUtils;
import com.winning.monitor.utils.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.winning.monitor.data.api.enums.TransactionReportsEnum.*;

/**
 * @author nicholasyan
 * @date 16/9/14
 */
@Service
public class OverViewStorage implements IOverViewStorage {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<ServerCountWithType> dayCountGroupBySys(String dateDay, List<String> svcCodeList) {
        String startTime = dateDay + " 00:00:00";
        String endTime = dateDay + " 23:59:59";

        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(Aggregation.match(Criteria.where("startTime").gte(startTime).lte(endTime)));
        operations.add(Aggregation.unwind(MACHINES));
        operations.add(Aggregation.unwind(CLIENTS));
        operations.add(Aggregation.unwind(TYPES));
        operations.add(Aggregation.match(new Criteria(TYPES + ".name").in(svcCodeList)));
        Field serverId = Fields.field("domain", "domain");
        Field serverDomain = Fields.field("name", "domain");
        operations.add(Aggregation.group(Fields.from(serverId, serverDomain))
                .sum(TYPES + ".totalCount").as("totalCount")
                .sum(TYPES + ".failCount").as("failCount"));
        operations.add(Aggregation.sort(new Sort(Sort.Direction.DESC, "totalCount")));
        Aggregation aggForServers = Aggregation.newAggregation(operations);

        System.out.println(aggForServers);//测试

        AggregationResults<ServerCountWithType> serverResult = mongoTemplate
                .aggregate(aggForServers, COLLECTION_REALTIME, ServerCountWithType.class);

//        Field targetTime = Fields.field("targetStartTime", "startTime");
//        Aggregation aggForDailyTotal = Aggregation.newAggregation(match, machines, clients, types,
//                Aggregation.group(Fields.from(targetTime))
//                        .sum(TYPES + ".totalCount").as("totalCount")
//                        .sum(TYPES + ".failCount").as("failCount")
//                        .addToSet("domain").as("serverList")
//        );
//        AggregationResults<Map> totalResult = mongoTemplate
//                .aggregate(aggForDailyTotal, COLLECTION_DAILY, Map.class);

        return serverResult.getMappedResults();
    }

    @Override
    public Map<String, Map<String, Object>> queryTrendChartData(String startTime, String endTime, String appId, int type) {
        String reportName;
        if (type == DateType.LAST7D.getKey() || type == DateType.LAST30D.getKey()) {
            reportName = COLLECTION_DAILY;
        } else {
            reportName = COLLECTION_REALTIME;
        }

        LinkedHashMap<String, Map<String, Object>> result = new LinkedHashMap<String, Map<String, Object>>();
        Map<String, Object> item;
        String time;
        long totalCount, failCount;
        if (type == DateType.LAST1H.getKey()) {
            Query query = new Query();
            query.addCriteria(new Criteria("domain").is(appId));
            query.addCriteria(new Criteria("startTime").gte(startTime).lte(endTime));
            List<TransactionReportVO> list = this.mongoTemplate.find(query, TransactionReportVO.class, reportName);
            List<TransactionMachineVO> machineVOs;
            List<TransactionClientVO> clientVOs;
            List<TransactionTypeVO> typeVOs;
            Map<Integer, Range2> range2s;
            String timeStart;
            if (list != null) {
                for (TransactionReportVO report : list) {
                    machineVOs = report.getMachines();
                    timeStart = report.getStartTime();
                    for (TransactionMachineVO machs : machineVOs) {
                        clientVOs = machs.getTransactionClients();
                        for (TransactionClientVO clients : clientVOs) {
                            typeVOs = clients.getTransactionTypes();
                            for (TransactionTypeVO types : typeVOs) {
                                range2s = types.getRange2s();
                                for (Map.Entry<Integer, Range2> range2Entry : range2s.entrySet()) {
                                    //替换分钟
                                    time = timeStart.replaceFirst(":00:", String.format(":%02d:", range2Entry.getKey()));
                                    item = result.get(time);
                                    if (item == null) {
                                        item = new HashMap<>();
                                        totalCount = 0L;
                                        failCount = 0L;
                                    } else {
                                        totalCount = Long.parseLong(item.get("totalCount").toString());
                                        failCount = Long.parseLong(item.get("failCount").toString());
                                    }
                                    //计算总数与失败数
                                    totalCount += range2Entry.getValue().getCount();
                                    failCount += range2Entry.getValue().getFails();
                                    item.put("totalCount", totalCount);
                                    item.put("failCount", failCount);
                                    result.put(time, item);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            List<AggregationOperation> operations = new ArrayList<>();
            operations.add(Aggregation.match(new Criteria("domain").is(appId)));
            operations.add(Aggregation.match(new Criteria("startTime").gte(startTime).lte(endTime)));
            operations.add(Aggregation.unwind("machines"));
            operations.add(Aggregation.unwind("machines.transactionClients"));
            operations.add(Aggregation.unwind("machines.transactionClients.transactionTypes"));
            operations.add(Aggregation.group("startTime")
                    .sum("machines.transactionClients.transactionTypes.totalCount").as("totalCount")
                    .sum("machines.transactionClients.transactionTypes.failCount").as("failCount"));
            operations.add(Aggregation.sort(new Sort(Sort.Direction.ASC, "_id")));
            Aggregation agg = Aggregation.newAggregation(operations);
            AggregationResults<BasicDBObject> arts = mongoTemplate.aggregate(agg, reportName, BasicDBObject.class);
            if (arts.getMappedResults().size() > 0) {
                for (DBObject obj : arts.getMappedResults()) {
                    time = String.valueOf(obj.get("_id"));
                    totalCount = Long.parseLong(String.valueOf(obj.get("totalCount")));
                    failCount = Long.parseLong(String.valueOf(obj.get("failCount")));
                    item = new HashMap<>();
                    item.put("totalCount", totalCount);
                    item.put("failCount", failCount);
                    result.put(time, item);
                }
            }
        }
        return result;
    }

    @Override
    public Map<String, Map<String, Object>> indexProject_queryTrendChartData(String startTime, int type,
                                                                             List<String> svcCodeList) {
        String reportName = COLLECTION_REALTIME;
        if (type != DateType.TODAY.getKey()) {
            reportName = COLLECTION_DAILY;
        }

        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(Aggregation.match(new Criteria("startTime").gte(startTime)));
        operations.add(Aggregation.unwind("machines"));
        operations.add(Aggregation.unwind("machines.transactionClients"));
        operations.add(Aggregation.unwind("machines.transactionClients.transactionTypes"));
        //下面这个match必须写在这个位置，因为只有在unwind拆分数组之后，才能根据name字段过滤
        operations.add(Aggregation.match(new Criteria("machines.transactionClients.transactionTypes._id").in(svcCodeList)));
        operations.add(Aggregation.group("startTime")
                .sum("machines.transactionClients.transactionTypes.totalCount").as("totalCount")
                .sum("machines.transactionClients.transactionTypes.failCount").as("failCount"));
        operations.add(Aggregation.sort(new Sort(Sort.Direction.ASC, "_id")));
        Aggregation agg = Aggregation.newAggregation(operations);
        AggregationResults<BasicDBObject> arts = mongoTemplate.aggregate(agg, reportName, BasicDBObject.class);
        LinkedHashMap result = new LinkedHashMap<String, Map<String, Object>>();
        Map<String, Object> item;
        String time;
        Long totalCount, failCount;
        if (arts.getMappedResults().size() > 0) {
            for (DBObject obj : arts.getMappedResults()) {
                time = String.valueOf(obj.get("_id"));
                totalCount = Long.parseLong(String.valueOf(obj.get("totalCount")));
                failCount = Long.parseLong(String.valueOf(obj.get("failCount")));
                item = new HashMap<>();
                item.put("totalCount", totalCount);
                item.put("failCount", failCount);
                result.put(time, item);
            }
        }
        if (type != DateType.TODAY.getKey()) {
            String todayStartTime = DateUtils.getStartTime(DateUtils.DAY_TYPE.TODAY);
            Aggregation aggregation = Aggregation.newAggregation(
                    Aggregation.match(Criteria.where("startTime").gte(todayStartTime)),
                    Aggregation.unwind(MACHINES), Aggregation.unwind(CLIENTS),
                    Aggregation.unwind(TYPES),
                    Aggregation.group("group")
                            .sum(TYPES + ".totalCount").as("totalCount")
                            .sum(TYPES + ".failCount").as("failCount")
            );
            AggregationResults<Map> results =
                    mongoTemplate.aggregate(aggregation, COLLECTION_REALTIME, Map.class);
            Map todayCount = results.getMappedResults().get(0);

            Map<String, Object> errorTodayCount =
                    (Map<String, Object>) result.get(todayStartTime);
            errorTodayCount.replace("totalCount", todayCount.get("totalCount"));
            errorTodayCount.replace("failCount", todayCount.get("failCount"));
        }
        return result;
    }

    @Override
    public LinkedList<Map<String, Object>> queryClientTypeChartData(String startTime, String endTime) {
        List<AggregationOperation> operations = new ArrayList<AggregationOperation>();
        operations.add(Aggregation.match(new Criteria("startTime").gte(startTime).lte(endTime)));
        operations.add(Aggregation.unwind("machines"));
        operations.add(Aggregation.unwind("machines.transactionClients"));
        operations.add(Aggregation.unwind("machines.transactionClients.transactionTypes"));
        /*operations.add(Aggregation.group("machines.transactionClients.type").sum("machines.transactionClients.transactionTypes.totalCount")
                .as("totalCount").sum("machines.transactionClients.transactionTypes.failCount")
                .as("failCount"));*/
        operations.add(Aggregation.group("machines.transactionClients.domain").sum("machines.transactionClients.transactionTypes.totalCount")
                .as("totalCount"));
        operations.add(Aggregation.sort(Sort.Direction.ASC, "_id"));
        Aggregation agg = Aggregation.newAggregation(operations);
        AggregationResults<BasicDBObject> arts = mongoTemplate.aggregate(agg, COLLECTION_REALTIME, BasicDBObject.class);

        LinkedList result = new LinkedList<Map<String, Object>>();
        Map<String, Object> item;
        Long totalCount;
        if (arts.getMappedResults().size() > 0) {
            for (DBObject obj : arts.getMappedResults()) {
                item = new HashMap<>();
                String typeCode = String.valueOf(obj.get("_id"));
                item.put("type", typeCode);
                totalCount = Long.parseLong(String.valueOf(obj.get("totalCount")));
                item.put("totalCount", totalCount);

                result.add(item);
            }
        }
        return result;
    }

//    @Override
//    public Map<String, Map<String, Object>> queryDataByAppInfo(String appId, String startTime, String endTime) {
//        Map<String, Map<String, Object>> result = new HashMap<>();
//        Map<String, Object> server = queryDataByServer(appId, startTime, endTime);
//        Map<String, Object> consumer = queryDataByConsumer(appId, startTime, endTime);
//        result.put("server", server);
//        result.put("consumer", consumer);
//        return result;
//    }

    @Override
    public Map<String, Map<String, Long>> queryDataByServer(List<String> svcCodeList, String startTime, String endTime) {
        //服务方统计
        List<AggregationOperation> operations = new ArrayList<>();
        Criteria criteria = new Criteria("startTime").gte(startTime).lte(endTime);
        operations.add(Aggregation.match(criteria));
        operations.add(Aggregation.unwind("machines"));
        operations.add(Aggregation.unwind("machines.transactionClients"));
        operations.add(Aggregation.unwind("machines.transactionClients.transactionTypes"));
        //下面这个match必须写在这个位置，因为只有在unwind拆分数组之后，才能根据name字段过滤
        operations.add(Aggregation.match(new Criteria("machines.transactionClients.transactionTypes._id").in(svcCodeList)));
        operations.add(Aggregation.group("domain")
                .sum("machines.transactionClients.transactionTypes.totalCount").as(TOTAL_COUNT)
                .sum("machines.transactionClients.transactionTypes.failCount").as(FAIL_COUNT));
        Aggregation agg = Aggregation.newAggregation(operations);
        AggregationResults<BasicDBObject> arts = mongoTemplate.aggregate(agg, COLLECTION_REALTIME, BasicDBObject.class);
        Map<String, Map<String, Long>> result = convertResult(arts);
        return result;
    }

    @Override
    public Map<String, Map<String, Long>> queryDataByConsumer(List<String> svcCodeList, String startTime, String endTime) {
        //服务方统计
        List<AggregationOperation> operations = new ArrayList<>();
        Criteria criteria = new Criteria("startTime").gte(startTime).lte(endTime);
        operations.add(Aggregation.match(criteria));
        operations.add(Aggregation.unwind("machines"));
        operations.add(Aggregation.unwind("machines.transactionClients"));
        operations.add(Aggregation.unwind("machines.transactionClients.transactionTypes"));
        //下面这个match必须写在这个位置，因为只有在unwind拆分数组之后，才能根据name字段过滤
        operations.add(Aggregation.match(new Criteria("machines.transactionClients.transactionTypes._id").in(svcCodeList)));
        operations.add(Aggregation.group("machines.transactionClients.domain")
                .sum("machines.transactionClients.transactionTypes.totalCount").as(TOTAL_COUNT)
                .sum("machines.transactionClients.transactionTypes.failCount").as(FAIL_COUNT));
        Aggregation agg = Aggregation.newAggregation(operations);
        AggregationResults<BasicDBObject> arts = mongoTemplate.aggregate(agg, COLLECTION_REALTIME, BasicDBObject.class);
        Map<String, Map<String, Long>> result = convertResult(arts);
        return result;
    }

    /**
     * 将聚合查询的结果转为Map
     */
    private Map<String, Map<String, Long>> convertResult(AggregationResults<BasicDBObject> arts) {
        Map<String, Map<String, Long>> result;
        List<BasicDBObject> dbObjects = arts.getMappedResults();
        if (!ListUtils.isEmpty(dbObjects)) {
            result = new HashMap<>();
            String appId;
            Long totalCount, failCount;
            Map<String, Long> resultCount;
            for (BasicDBObject obj : dbObjects) {
                appId = String.valueOf(obj.get(KEY_ID));
                if (!result.containsKey(appId)) {
                    resultCount = new HashMap<>();
                    resultCount.put(TOTAL_COUNT, 0L);
                    resultCount.put(FAIL_COUNT, 0L);
                    result.put(appId, resultCount);
                }
                totalCount = result.get(appId).get(TOTAL_COUNT) + Long.parseLong(obj.get(TOTAL_COUNT).toString());
                failCount = result.get(appId).get(FAIL_COUNT) + Long.parseLong(obj.get(FAIL_COUNT).toString());
                result.get(appId).put(TOTAL_COUNT, totalCount);
                result.get(appId).put(FAIL_COUNT, failCount);
            }
        } else {
            result = null;
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> queryDataDetailsByServer(Object appId, String startTime, String endTime) {
        List<AggregationOperation> operations = new ArrayList<>();
        Criteria criteria = new Criteria("startTime").gte(startTime).lte(endTime);
        if (appId instanceof String) {
            criteria.and("domain").is(appId);
        } else {
            //必须将Object强制转为List，否则会导致查询无数据
            criteria.and("domain").in((List) appId);
        }
        operations.add(Aggregation.match(criteria));
        operations.add(Aggregation.unwind("machines"));
        operations.add(Aggregation.unwind("machines.transactionClients"));
        operations.add(Aggregation.unwind("machines.transactionClients.transactionTypes"));

        Fields fields = Fields.fields("domain")
                .and("serviceName", "machines.transactionClients.transactionTypes._id");
        operations.add(Aggregation.group(fields)
                .sum("machines.transactionClients.transactionTypes.totalCount").as("totalCount")
                .sum("machines.transactionClients.transactionTypes.failCount").as("failCount"));
        Aggregation agg = Aggregation.newAggregation(operations);
        AggregationResults<BasicDBObject> arts = mongoTemplate.aggregate(agg, COLLECTION_REALTIME, BasicDBObject.class);
        List result = new LinkedList();
        Long totalCount, failCount;
        String serviceName;

        if (arts.getMappedResults().size() > 0) {
            for (DBObject obj : arts.getMappedResults()) {
                serviceName = String.valueOf(obj.get("serviceName"));
                totalCount = Long.parseLong(String.valueOf(obj.get("totalCount")));
                failCount = Long.parseLong(String.valueOf(obj.get("failCount")));
                Map tempMap = new HashMap();
                tempMap.put("totalCount", totalCount);
                tempMap.put("failCount", failCount);
                tempMap.put("serviceName", serviceName);
                result.add(tempMap);
            }
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> queryDataDetailsByConsumer(Object appId, String startTime, String endTime) {
        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(Aggregation.match(new Criteria("startTime").gte(startTime).lte(endTime)));
        operations.add(Aggregation.unwind("machines"));
        operations.add(Aggregation.unwind("machines.transactionClients"));
        operations.add(Aggregation.unwind("machines.transactionClients.transactionTypes"));
        if (appId instanceof String) {
            operations.add(Aggregation.match(new Criteria("machines.transactionClients.domain").is(appId)));
        } else {
            //必须将Object强制转为List，否则会导致查询无数据
            operations.add(Aggregation.match(new Criteria("machines.transactionClients.domain").in((List) appId)));
        }
        Fields fields = Fields.fields("domain")
                .and("serviceName", "machines.transactionClients.transactionTypes._id");
        operations.add(Aggregation.group(fields).sum("machines.transactionClients.transactionTypes.totalCount")
                .as("totalCount").sum("machines.transactionClients.transactionTypes.failCount")
                .as("failCount"));
        Aggregation agg = Aggregation.newAggregation(operations);
        AggregationResults<BasicDBObject> arts = mongoTemplate.aggregate(agg, COLLECTION_REALTIME, BasicDBObject.class);
        List result = new LinkedList();
        Long totalCount, failCount;
        String domain, serviceName;
        if (arts.getMappedResults().size() > 0) {
            Map<String, Object> tempMap;
            for (DBObject obj : arts.getMappedResults()) {
                domain = String.valueOf(obj.get("domain"));
                serviceName = String.valueOf(obj.get("serviceName"));
                if (!StringUtils.isEmpty(domain) && !StringUtils.isEmpty(serviceName)) {
                    totalCount = Long.parseLong(String.valueOf(obj.get("totalCount")));
                    failCount = Long.parseLong(String.valueOf(obj.get("failCount")));
                    tempMap = new HashMap();
                    tempMap.put("domain", domain);
                    tempMap.put("totalCount", totalCount);
                    tempMap.put("failCount", failCount);
                    tempMap.put("serviceName", serviceName);
                    result.add(tempMap);
                }
            }
        }
        return result;
    }

    /**
     * 统计监控中服务的历史调用总数
     */
    @Override
    public long totalHistory() {
        Long now = DateUtils.getCurrentTime();
        String endTime = DateUtils.toDateString(DateUtils.getStartTime(now, DateType.DAY));
        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(Aggregation.match(new Criteria("endTime").lt(endTime)));
        operations.add(Aggregation.unwind("machines"));
        operations.add(Aggregation.unwind("machines.transactionClients"));
        operations.add(Aggregation.unwind("machines.transactionClients.transactionTypes"));
        operations.add(Aggregation.group().sum("machines.transactionClients.transactionTypes.totalCount")
                .as("totalCount"));
        Aggregation agg = Aggregation.newAggregation(operations);
        AggregationResults<BasicDBObject> arts = mongoTemplate.aggregate(agg, COLLECTION_DAILY, BasicDBObject.class);
        long totalCount = 0L;
        if (arts.getMappedResults().size() > 0) {
            DBObject obj = arts.getMappedResults().get(0);
            totalCount = Long.parseLong(String.valueOf(obj.get("totalCount")));
        }
        return totalCount;
    }

    @Override
    public List<ServerCountWithType> queryErrorConsumers(String startTime) {
        Criteria baseCri = Criteria.where("startTime").gte(startTime);

        Criteria filterCri = Criteria.where(TYPES + ".failCount").ne(0);

        Field systemId = Fields.field("domain", CLIENTS + ".domain");
        Field systemName = Fields.field("name", CLIENTS + ".domain");
        Fields groupFields = Fields.from(systemId, systemName);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(baseCri),
                Aggregation.unwind(MACHINES), Aggregation.unwind(CLIENTS),
                Aggregation.unwind(TYPES), Aggregation.match(filterCri),
                Aggregation.group(groupFields)
                        .sum(TYPES + ".totalCount").as("totalCount")
                        .sum(TYPES + ".failCount").as("failCount"),
                Aggregation.sort(Sort.Direction.DESC, "failCount"),
                Aggregation.limit(5)
        );
        return mongoTemplate.aggregate(aggregation,
                COLLECTION_REALTIME, ServerCountWithType.class).getMappedResults();
    }

    @Override
    public List<ServiceDurationStatisticVO> queryServiceDuration(String startTime) {
        Criteria baseCri = Criteria.where("startTime").gte(startTime);

        Field serverId = Fields.field("serverId", "domain");
        Field serviceId = Fields.field("serviceId", TYPES + "._id");
        Fields groupFields = Fields.from(serverId, serviceId);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(baseCri),
                Aggregation.unwind(MACHINES), Aggregation.unwind(CLIENTS),
                Aggregation.unwind(TYPES),
                Aggregation.group(groupFields)
                        .avg(TYPES + ".avg").as("duration"),
                Aggregation.sort(Sort.Direction.DESC, "duration"),
                Aggregation.limit(5)
        );
        return mongoTemplate.aggregate(aggregation, COLLECTION_REALTIME,
                ServiceDurationStatisticVO.class).getMappedResults();
    }

    @Override
    public MessageTreeList queryDetailsDuration() {
        MessageTreeList messageTreeList = new MessageTreeList();

        List<MessageTreePO> treePOS = mongoTemplate
                .findAll(MessageTreePO.class, TOP_DURATION);
        for (MessageTreePO treePO : treePOS) {
            messageTreeList.addMessageTree(treePO.getMessageTree());
        }
        messageTreeList.setTotalSize(treePOS.size());
        return messageTreeList;
    }

}