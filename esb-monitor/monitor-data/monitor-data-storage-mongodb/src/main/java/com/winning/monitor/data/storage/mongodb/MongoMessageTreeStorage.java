package com.winning.monitor.data.storage.mongodb;

import com.winning.esb.stable.MonitorConst;
import com.winning.esb.utils.MapUtils;
import com.winning.monitor.agent.logging.message.MessageTree;
import com.winning.monitor.data.api.base.ServiceCountStatisticVO;
import com.winning.monitor.data.api.enums.DateType;
import com.winning.monitor.data.api.transaction.vo.TransactionReportType;
import com.winning.monitor.data.storage.Utils.ConvertUtils;
import com.winning.monitor.data.storage.api.MessageTreeStorage;
import com.winning.monitor.data.storage.api.entity.MessageTreeList;
import com.winning.monitor.data.storage.mongodb.po.message.MessageTreePO;
import com.winning.monitor.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Field;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.winning.monitor.data.storage.Utils.ConvertUtils.*;
import static com.winning.monitor.utils.DateUtils.*;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * @author nicholasyan
 * @date 16/9/30
 */
@Repository
public class MongoMessageTreeStorage implements MessageTreeStorage {

    private static final Logger logger = LoggerFactory.getLogger(MongoMessageTreeStorage.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void storeTransaction(MessageTree tree) {
        if (tree == null) {
            return;
        }

        String collectionName = GetCollectionName(tree.getDomain());
        MessageTreePO messageTreePO = new MessageTreePO(tree);
        this.mongoTemplate.insert(messageTreePO, collectionName);
    }

//    @Override
//    public MessageTreeList queryMessageTree(String group, String domain, long startTime,
//                                            long endTime, Map<String, Object> arguments,
//                                            int startIndex, int pageSize,
//                                            Map<String, Object> previous) {
//
//        String collectionName = getCollectionName(domain);
//
//        Query query = new Query();
//        query.addCriteria(new Criteria("group").is(group));
//
//        if (arguments != null && arguments.containsKey("clientAppName"))
//            query.addCriteria(new Criteria("messageTree.caller.name").is(arguments.get("clientAppName")));
//        if (arguments != null && arguments.containsKey("transactionTypeName"))
//            query.addCriteria(new Criteria("messageTree.message.type").is(arguments.get("transactionTypeName")));
//
//        query.addCriteria(new Criteria("messageTree.message.timestampInMillis")
//                .gte(startTime).lte(endTime));
//
//        if (arguments != null && arguments.containsKey("transactionName"))
//            query.addCriteria(new Criteria("messageTree.message.name").is(arguments.get("transactionName")));
//        if (arguments != null && arguments.containsKey("serverIpAddress"))
//            query.addCriteria(new Criteria("messageTree.ipAddress").is(arguments.get("serverIpAddress")));
//
//        if (arguments != null && arguments.containsKey("clientIpAddress"))
//            query.addCriteria(new Criteria("messageTree.caller.ip").is(arguments.get("clientIpAddress")));
//
//        if (arguments != null && arguments.containsKey("status")) {
//            String status = (String) arguments.get("status");
//            if ("执行成功".equals(status) || "0".equals(status)) {
//                query.addCriteria(new Criteria("messageTree.message.status").is("0"));
//            } else if ("执行失败".equals(status) || "-1".equals(status)) {
//                query.addCriteria(new Criteria("messageTree.message.status").ne("0"));
//            }
//        }
//
//        if (arguments != null && arguments.containsKey("keyWords")) {
//            String keyWords = (String) arguments.get("keyWords");
//            if (null != keyWords) {
//                Criteria cr = new Criteria();
//                query.addCriteria(cr.orOperator(Criteria.where("messageTree.caller.ip").regex(keyWords),
//                        Criteria.where("messageTree.caller.name").regex(keyWords),
//                        Criteria.where("messageTree.message.type").regex(keyWords),
//                        Criteria.where("messageTree.ipAddress").regex(keyWords)));
//            }
//        }
//
//        long total = this.mongoTemplate.count(query, collectionName);
//
//        query.fields().include("messageTree");
//
///*取消前台排序功能*/
////        if (orderBy != null) {
////            for (Map.Entry<String, String> orderByItem : orderBy.entrySet()) {
////                String field = orderByItem.getKey();
////                Sort.Direction orderDirection = "DESC".equals(orderByItem.getValue()) ?
////                        Sort.Direction.DESC : Sort.Direction.ASC;
////
////                if ("duration".equals(field)) {
////                    query.with(new Sort(orderDirection, "messageTree.message.durationInMicro"));
////                }
////                if ("time".equals(field)) {
////                    query.with(new Sort(orderDirection, "messageTree.message.timestampInMillis"));
////                }
////                if ("status".equals(field)) {
////                    query.with(new Sort(orderDirection, "messageTree.message.status"));
////                }
////            }
////        }
//
//        query.skip(startIndex).
//                limit(pageSize).
//                with(new Sort(Sort.Direction.DESC, "messageTree.message.timestampInMillis"));
//
//        List<MessageTreePO> messageTrees =
//                this.mongoTemplate.find(query, MessageTreePO.class, collectionName);
//
//        MessageTreeList messageTreeList = new MessageTreeList();
//        for (MessageTreePO messageTreePO : messageTrees) {
//            messageTreeList.addMessageTree(messageTreePO.getMessageTree());
//        }
//
//        messageTreeList.setTotalSize(total);
//
//        return messageTreeList;
//    }


    @Override
    public MessageTreeList queryMessageTree(String group, String domain,
                                            TransactionReportType dateType, String time,
                                            Map<String, Object> arguments,
                                            int startIndex, int pageSize,
                                            Map<String, Object> previous) {
        String machineAddress = null, clientAddress = null,
                clientDomain = null, transactionType = null,
                status = null, collectionName = GetCollectionName(domain);
        String inputIP = null;

        MessageTreeList messageTreeList = new MessageTreeList();
        List<MessageTreePO> treePOList;
        long totalCount;

        if (arguments.containsKey("serverIpAddress")) {
            machineAddress = (String) arguments.get("serverIpAddress");
        }
        if (arguments.containsKey("clientIpAddress")) {
            clientAddress = (String) arguments.get("clientIpAddress");
        }
        if (arguments.containsKey("clientAppName")) {
            clientDomain = (String) arguments.get("clientAppName");
        }
        if (arguments.containsKey("transactionTypeName")) {
            transactionType = (String) arguments.get("transactionTypeName");
        }
        if (arguments.containsKey("status")) {
            status = (String) arguments.get("status");
            if ("执行失败".equals(status) || "-1".equals(status)) {
                status = "-1";
            } else if ("执行成功".equals(status) || "0".equals(status)) {
                status = "0";
            } else {
                status = "";
            }
        }

        if (arguments.containsKey("inputIP")) {
            inputIP = (String) arguments.getOrDefault("inputIP", "");
        }
        String keyWords = (String) arguments.getOrDefault("keyWords", ""),
                limitStartTime = (String) arguments.getOrDefault("limitStartTime", ""),
                limitEndTime = (String) arguments.getOrDefault("limitEndTime", "");
        String durationTop = (String) arguments.getOrDefault("durationTop", "");

        if (StringUtils.hasText(keyWords)) {

            long startTimestamp = com.winning.esb.utils.DateUtils.getTimestampMin(), endTimeStamp = com.winning.esb.utils.DateUtils.getTimestampMax();
            switch (dateType) {
                case REALTIME:
                    if (StringUtils.hasText(limitStartTime) && StringUtils.hasText(limitEndTime)) {
                        startTimestamp = convertStartTime(limitStartTime);
                        endTimeStamp = convertEndTime(limitEndTime);
                    } else {
                        startTimestamp = getStartTimeByLong(DateUtils.DAY_TYPE.CURRENT, new Date());
                        endTimeStamp = getCurrentTime();
                    }
                    break;
                case HOUR_IN_TODAY:
                    if (StringUtils.hasText(limitStartTime) && StringUtils.hasText(limitEndTime)) {
                        startTimestamp = convertStartTime(limitStartTime);
                        endTimeStamp = convertEndTime(limitEndTime);
                    } else {
                        startTimestamp = GetStartTimeLongWithTimeGral(TransactionReportType.HOUR_IN_TODAY, time);
                        endTimeStamp = GetEndTimeLongWithTimeGral(TransactionReportType.HOUR_IN_TODAY, time);
                    }
                    break;
                case TODAY:
                    if (StringUtils.hasText(limitStartTime) && StringUtils.hasText(limitEndTime)) {
                        startTimestamp = convertStartTime(limitStartTime);
                        endTimeStamp = convertEndTime(limitEndTime);
                    }
                    break;
            }
            treePOList = queryMessageTreeByLimit(clientDomain, transactionType,
                    machineAddress, clientAddress, status, keyWords,
                    startTimestamp, endTimeStamp, collectionName, startIndex, pageSize, durationTop);
            totalCount = countMessageTreeByLimit(clientDomain, transactionType,
                    machineAddress, clientAddress, status, keyWords,
                    startTimestamp, endTimeStamp, collectionName, durationTop);
        } else {
            totalCount = queryTotalCount(domain, time, clientDomain, transactionType,
                    machineAddress, clientAddress, dateType, status, durationTop, inputIP);

            //界面显示按照调用时间从近到远
            long startTimeStamp = GetStartTimeLongWithTimeGral(dateType, time);
            long endTimeStamp = GetEndTimeLongWithTimeGral(dateType, time);
            Criteria basicMatch = generateBasicCrt(startTimeStamp, endTimeStamp, clientDomain,
                    transactionType, machineAddress, clientAddress, status, null, inputIP);
            if (StringUtils.hasText(durationTop)) {
                Sort sort = new Sort(Sort.Direction.DESC, "messageTree.message.durationInMicro");
                Integer limit = 10;
                Integer skip = startIndex;
                treePOList = aggregateForTreeList(basicMatch, sort, skip, limit, collectionName);
            } else {
                String sortField = "messageTree.message.timestampInMillis";
                //无结果
                if (totalCount < 1) {
                    treePOList = null;
                }
                //第一页
                else if (startIndex == 0) {
                    Sort sort = new Sort(Sort.Direction.DESC, sortField);
                    Integer limit = 10;
                    treePOList = aggregateForTreeList(basicMatch, sort, null, limit, collectionName);
                }
                //最后一页
                else if ((totalCount - startIndex) <= 10) {
                    Sort sort = new Sort(Sort.Direction.ASC, sortField);
                    Integer limit = Math.toIntExact((totalCount - startIndex));
                    treePOList = aggregateForTreeList(basicMatch, sort, null, limit, collectionName);
                }
                //中间页 限制替换跳页
                else {
                    int currentPage = startIndex / 10 + 1;
                    int previousPage = (int) previous.get("previousPage");
                    Sort sort = null;
                    Integer skip = null;

                    //页数变大、时间变小
                    if (currentPage > previousPage) {
                        //为了避免相同时间戳漏选，第一条为上页最小时间，因此需要额外跳过最小时间条数
                        int sameMinCount = (int) previous.get("minSameCount");
                        int skipCount = (currentPage - previousPage - 1) * 10 + (sameMinCount + 1);
                        //exactMatch = Criteria.where(sortField).lte(previous.get("minTime"));
                        basicMatch = generateBasicCrt(startTimeStamp, (Long) previous.get("minTime"), clientDomain,
                                transactionType, machineAddress, clientAddress, status, null, inputIP);

                        sort = new Sort(Sort.Direction.DESC, sortField);
                        skip = skipCount;
                    }
                    //页数变小、时间变大
                    else if (currentPage < previousPage) {
                        int sameMaxCount = (int) previous.get("maxSameCount");
                        int skipCount = (previousPage - currentPage - 1) * 10 + (sameMaxCount + 1);
                        //exactMatch = Criteria.where(sortField).gte(previous.get("maxTime"));
                        basicMatch = generateBasicCrt((Long) previous.get("maxTime"), endTimeStamp, clientDomain,
                                transactionType, machineAddress, clientAddress, status, null, inputIP);

                        sort = new Sort(Sort.Direction.ASC, sortField);
                        skip = skipCount;
                    }
                    Integer limit = 10;
                    treePOList = aggregateForTreeList(basicMatch, sort, skip, limit, collectionName);
                }
            }
        }

        messageTreeList.setTotalSize(totalCount);
        if (!CollectionUtils.isEmpty(treePOList)) {
            for (MessageTreePO messageTreePO : treePOList) {
                messageTreeList.addMessageTree(messageTreePO.getMessageTree());
            }
            if (StringUtils.isEmpty(durationTop)) {
                //逆序 o1.time>=o2.time返回-1
                treePOList.sort((o1, o2) -> {
                    long com1 = o1.getMessageTree().getMessage().getTimestamp();
                    long com2 = o2.getMessageTree().getMessage().getTimestamp();
                    return com1 >= com2 ? -1 : 1;
                });
            }
        }
        return messageTreeList;
    }

    @Override
    public MessageTreeList queryMessageTree(String group, String messageId, String domain) {
        Query query = new Query();
        query.addCriteria(new Criteria("domain").is(domain));
        query.addCriteria(new Criteria("group").is(group));
        query.addCriteria(new Criteria("messageId").is(messageId));

        List<MessageTreePO> messageTrees = this.mongoTemplate.find(query, MessageTreePO.class, GetCollectionName(domain));
        MessageTreeList messageTreeList = new MessageTreeList();
        for (MessageTreePO messageTreePO : messageTrees) {
            messageTreeList.addMessageTree(messageTreePO.getMessageTree());
        }

        return messageTreeList;
    }

    @Override
    public MessageTreeList queryMessageTree(String group, String domain, Map<String, Object> arguments, int startIndex, int pageSize) {
        String machineAddress = null, clientAddress = null,
                clientDomain = null, transactionType = null,
                status = null, collectionName = GetCollectionName(domain);

        //整理参数map
        arguments.remove("start");
        arguments.remove("pageSize");
        MapUtils.deleteEmptyNull(arguments);

        if (arguments.containsKey("serverIpAddress")) {
            machineAddress = (String) arguments.get("serverIpAddress");
        }
        if (arguments.containsKey("clientIpAddress")) {
            clientAddress = (String) arguments.get("clientIpAddress");
        }
        if (arguments.containsKey("clientAppName")) {
            clientDomain = (String) arguments.get("clientAppName");
        }
        if (arguments.containsKey("transactionTypeName")) {
            transactionType = (String) arguments.get("transactionTypeName");
        }
        if (arguments.containsKey("status")) {
            status = (String) arguments.get("status");
            if ("执行失败".equals(status) || "-1".equals(status)) {
                status = "-1";
            } else if ("执行成功".equals(status) || "0".equals(status)) {
                status = "0";
            } else {
                status = "";
            }
        }

        String keyWords = (String) arguments.getOrDefault("keyWords", "");
        String durationTop = (String) arguments.getOrDefault("durationTop", "");

        //设置筛选时间范围
        setRangetime(arguments);
        long startTimestamp = Long.parseLong(arguments.get("startTimestamp").toString()),
                endTimeStamp = Long.parseLong(arguments.get("endTimeStamp").toString());

        //查询记录
        List<MessageTreePO> treePOList = queryMessageTreeByLimit(clientDomain, transactionType,
                machineAddress, clientAddress, status, keyWords,
                startTimestamp, endTimeStamp, collectionName, startIndex, pageSize, durationTop);

        //查询总数【xuehao 2018-03-25：由于MongoDB的count性能较低，因此，可以从前端把报告中的总数传到后端，这样就无需再获取了，
        // 但是如果查询关键字不为空，则必须实时查询总数】
        long totalCount;
        String totalCountString = MapUtils.getValue(arguments, "totalCount");
        if(StringUtils.isEmpty(totalCountString)) {
            totalCount = countMessageTreeByLimit(clientDomain, transactionType,
                    machineAddress, clientAddress, status, keyWords,
                    startTimestamp, endTimeStamp, collectionName, durationTop);
        } else {
            totalCount = Long.parseLong(totalCountString);
        }

        MessageTreeList messageTreeList = new MessageTreeList();
        messageTreeList.setTotalSize(totalCount);
        if (!CollectionUtils.isEmpty(treePOList)) {
            for (MessageTreePO messageTreePO : treePOList) {
                messageTreeList.addMessageTree(messageTreePO.getMessageTree());
            }
            if (StringUtils.isEmpty(durationTop)) {
                //逆序 o1.time>=o2.time返回-1
                treePOList.sort((o1, o2) -> {
                    long com1 = o1.getMessageTree().getMessage().getTimestamp();
                    long com2 = o2.getMessageTree().getMessage().getTimestamp();
                    return com1 >= com2 ? -1 : 1;
                });
            }
        }
        return messageTreeList;
    }

    /**
     * 设置筛选时间范围
     */
    private void setRangetime(Map<String, Object> map) {
        String datetypeString = MapUtils.getValue(map, "datetype");
        int datetype = com.winning.esb.utils.StringUtils.isEmpty(datetypeString) ? DateType.TODAY.getKey() : Integer.parseInt(datetypeString);
        long startTimestamp = com.winning.esb.utils.DateUtils.getTimestampMin(), endTimeStamp = com.winning.esb.utils.DateUtils.getTimestampMax();
        if(datetype == DateType.LAST1H.getKey()) {
            startTimestamp = getStartTimeByLong(DateUtils.DAY_TYPE.CURRENT, new Date());
            endTimeStamp = getCurrentTime();
        } else if(datetype == DateType.HOUR.getKey()) {
            String time = MapUtils.getValue(map, "time");
            //指定小时的开始时间（例如：2018-03-25 00:00:00.000）
            startTimestamp = convertStartTime(time);
            //指定小时的结束时间（例如：2018-03-25 00:59:59.999）
            endTimeStamp = startTimestamp + 3600 * 1000 - 1;
        } else if(datetype == DateType.TODAY.getKey()) {
            String limitStartTime = MapUtils.getValue(map, "limitStartTime");
            String limitEndTime = MapUtils.getValue(map, "limitEndTime");
            startTimestamp = convertStartTime(limitStartTime);
            endTimeStamp = convertEndTime(limitEndTime);
        }
        map.put("startTimestamp", startTimestamp);
        map.put("endTimeStamp", endTimeStamp);
    }

    private long queryTotalCount(String domain, String time, String clientDomain,
                                 String transactionType, String machineAddress,
                                 String clientAddress, TransactionReportType dateType,
                                 String status, String durationTop, String inputIP) {

        String startTime = GetStartTimeWithTimeGral(dateType, time);

        //首要筛选条件：serverDomain、timeScape
        Criteria criToScreen = Criteria.where("domain").is(domain);
        if (dateType.equals(TransactionReportType.TODAY)) {
            String endTime = GetEndTimeWithTimeGral(dateType, time);
            criToScreen.and("startTime").gte(startTime).lte(endTime);
        } else {
            criToScreen.and("startTime").is(startTime);
        }

        //精确匹配条件：serverIP、clientDomain、transactionType、clientIP
//        Criteria criToMatch = Criteria.where("machines.transactionClients.domain").is(clientDomain)
//                .and("machines.transactionClients.transactionTypes.name").is(transactionType);
        Criteria criToMatch = new Criteria();
        if (!StringUtils.isEmpty(clientDomain)) {
            criToMatch.and("machines.transactionClients.domain").is(clientDomain);
        }
        if (!StringUtils.isEmpty(transactionType)) {
            criToMatch.and("machines.transactionClients.transactionTypes.name").is(transactionType);
        }
        if (StringUtils.hasText(machineAddress)) {
            criToMatch.and("machines.ip").is(machineAddress);
        }
        if (StringUtils.hasText(clientAddress)) {
            criToMatch.and("machines.transactionClients.ip").is(clientAddress);
        }
        if (StringUtils.hasText(inputIP) && StringUtils.isEmpty(clientAddress)) {
            criToMatch.and("machines.transactionClients.ip").is(inputIP);
        }

        Field serverName = Fields.field("serverName", "$domain");
        Field serverIp = Fields.field("serverIp", "$machines.ip");
        Field clientName = Fields.field("clientName", "$machines.transactionClients.domain");
        Field clientIp = Fields.field("clientIp", "$machines.transactionClients.ip");
        Field serviceName = Fields.field("serviceName", "$machines.transactionClients.transactionTypes._id");
        Fields fields = Fields.from(serverName, serverIp, clientName, clientIp, serviceName);

        Aggregation aggToCount = Aggregation.newAggregation(
                match(criToScreen),
                unwind("machines"),
                unwind("machines.transactionClients"),
                unwind("machines.transactionClients.transactionTypes"),
                match(criToMatch),
                group(fields).sum("$machines.transactionClients.transactionTypes.totalCount")
                        .as("totalCount")
                        .sum("$machines.transactionClients.transactionTypes.failCount")
                        .as("failCount")
        );

        AggregationResults<ServiceCountStatisticVO> statisticVOS =
                mongoTemplate.aggregate(aggToCount, GetReportCollectionName(dateType), ServiceCountStatisticVO.class);

        long totalCount = 0, failCount = 0, result;
        for (ServiceCountStatisticVO statisticVO : statisticVOS) {
            totalCount += statisticVO.getTotalCount();
            failCount += statisticVO.getFailCount();
        }
        switch (status) {
            //成功
            case "0":
                result = totalCount - failCount;
                break;
            //失败
            case "-1":
                result = failCount;
                break;
            //失败
            default:
                result = totalCount;
        }

        if (StringUtils.hasText(durationTop)) {
            return result > 50 ? 50 : result;
        }
        return result;
    }

    private Criteria generateBasicCrt(long startTimeStamp, long endTimeStamp, String clientDomain,
                                      String transactionType, String machineAddress,
                                      String clientAddress, String status, String keyWords, String inputIP) {
        Criteria criteria = Criteria.where("messageTree.message.timestampInMillis").gte(startTimeStamp).lte(endTimeStamp);

        //添加关键字
        ConvertUtils.addKeywordToQuery(criteria, keyWords);

        if (!StringUtils.isEmpty(transactionType)) {
            criteria.and("messageTree.message.type").is(transactionType);
        }
        if (!StringUtils.isEmpty(clientDomain)) {
            criteria.and("messageTree.caller.name").is(clientDomain);
        }

        if (StringUtils.hasText(machineAddress)) {
            criteria.and("ipAddress").is(machineAddress);
        }
        if (StringUtils.hasText(clientAddress)) {
            criteria.and("messageTree.caller.ip").is(clientAddress);
        }
        if (StringUtils.hasText(status)) {
            if ("0".equals(status)) {
                criteria.and("messageTree.message.status").is(status);
            } else {
                criteria.and("messageTree.message.status").ne("0");
            }
        }
        if (StringUtils.hasText(inputIP) && StringUtils.isEmpty(clientAddress)) {
            criteria.and("messageTree.caller.ip").is(inputIP);
        }
        return criteria;
    }

    private List<MessageTreePO> aggregateForTreeList(Criteria basicMatch, Sort sort,
                                                     Integer skip, Integer limit,
                                                     String collectionName) {

        Query query = new Query();
        if (skip != null) {
            query.addCriteria(basicMatch).with(sort).skip(skip).limit(limit);
        } else {
            query.addCriteria(basicMatch).with(sort).limit(limit);
        }

        List<MessageTreePO> results;
        try {
            results = mongoTemplate.find(query, MessageTreePO.class, collectionName);

            //convert unmodifiableList
            if (results != null) {
                return new ArrayList<>(results);
            }
        } catch (Exception e) {
            logger.error("查询明细超时，查询语句：" + query.toString());
        }
        return null;
    }

    private List<MessageTreePO> queryMessageTreeByLimit(String clientDomain, String transactionTye, String machineAddress,
                                                        String clientAddress, String status, String keyWords,
                                                        long limitStartTime, long limitEndTime, String collectionName,
                                                        int startIndex, int pageSize, String durationTop) {

        Criteria criteria = generateBasicCrt(limitStartTime, limitEndTime,
                clientDomain, transactionTye, machineAddress, clientAddress, status, keyWords, null);

        Query query = Query.query(criteria);
        if (StringUtils.hasText(durationTop)) {
            query.with(new Sort(Sort.Direction.DESC, "messageTree.message.durationInMicro"));
        } else {
            query.with(new Sort(Sort.Direction.DESC, "messageTree.message.timestampInMillis"));
        }
        if (startIndex > 0) {
            query.skip(startIndex);
        }
        query.limit(pageSize);

//        System.out.println(query.toString());   //测试

        List<MessageTreePO> results;
        try {
            results = mongoTemplate.find(query, MessageTreePO.class, collectionName);
            return results == null ? null : new ArrayList<>(results);
        } catch (Exception e) {
            logger.error("查询明细超时，查询语句：" + query.toString());
        }
        return null;
    }

    private long countMessageTreeByLimit(String clientDomain, String transactionTye, String machineAddress,
                                         String clientAddress, String status, String keyWords,
                                         long limitStartTime, long limitEndTime,
                                         String collectionName, String durationTop) {
        Criteria criteria = generateBasicCrt(limitStartTime, limitEndTime,
                clientDomain, transactionTye, machineAddress, clientAddress, status, keyWords, null);

        Query query = Query.query(criteria);

//        System.out.println(query);  //测试

        long count = mongoTemplate.count(query, collectionName);
        if (StringUtils.hasText(durationTop)) {
            return count > 50 ? 50 : count;
        }
        return count;
    }

    private Long convertStartTime(String limitTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String time = limitTime.length() == 16 ? (limitTime + ":00.000") : (limitTime + ".000");
        return toDateTime(time, dateFormat);
    }

    private Long convertEndTime(String limitTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String time = limitTime.length() == 16 ? (limitTime + ":59.999") : (limitTime + ".999");
        return toDateTime(time, dateFormat);
    }

}