package com.winning.monitor.data.storage.mongodb;

import com.google.gson.JsonObject;
import com.mongodb.DBObject;
import com.winning.monitor.data.api.base.ServiceCountStatisticVO;
import com.winning.monitor.data.api.transaction.vo.TransactionReportVO;
import com.winning.monitor.data.storage.api.ITransactionDataStorage;
import com.winning.monitor.data.storage.api.MessageTreeStorage;
import com.winning.monitor.data.storage.mongodb.po.message.MessageTreePO;
import com.winning.monitor.data.storage.mongodb.po.transaction.*;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Field;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.winning.monitor.utils.DateUtils.SECOND;
import static com.winning.monitor.utils.DateUtils.getMonthFirstDay;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Created by nicholasyan on 16/9/18.
 */
@ContextConfiguration(locations = {"classpath*:META-INF/spring/*-context.xml"})
public class TransactionStorageUT extends
        AbstractJUnit4SpringContextTests {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ITransactionDataStorage transactionDataStorage;

    @Autowired
    private MessageTreeStorage messageTreeStorage;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String COLLECTION_NAME = "TransactionMonthlyReports";

    @Test
    public void testConcurrentHashMap() {
        Map<Long, String> map = new ConcurrentHashMap<>();
        map.put(18L, "123");
        map.put(2L, "456");
        map.put(12L, "6688");
        map.put(-1L, "negative");
        map.put(8L, "third");
        List<Map.Entry<Long, String>> entryList = new ArrayList<>(map.entrySet());
        entryList.sort(Comparator.comparing(Map.Entry::getKey));
        System.out.println(entryList.get(0).getKey());
        //entryList.forEach(item -> System.out.println(item.getKey()+item.getValue()));
    }

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Test
    public void testEnsureIndex() {
//        DBObject indexOption = new BasicDBObject("background", true);
//        indexOption.put("spare", true);
//
//        DBCollection collection = mongoTemplate.getCollection("Messages-HIS0214");
//
//        DBObject typeIndex = new BasicDBObject("messageTree.message.type", 1);
//        collection.createIndex(typeIndex, indexOption);


        for (int i = 0; i < 10; i++) {
            int random = new Random().nextInt(11) + 10;
            System.out.println(random);
        }
//        Calendar calendar = Calendar.getInstance();
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        int minus = calendar.get(Calendar.MINUTE);
//        minus = minus - (minus % 10);
//        LocalDate date = LocalDate.parse("2018-04-13", FORMATTER);
//        System.out.println(queryTimestampByLocalDate(date, hour, minus));
        //minus = (minus%10)
        //System.out.println();
    }

    public static Long queryTimestampByLocalDate(LocalDate localDate, int hour, int minus) {
        if (hour == 24) {
            LocalTime localTime = LocalTime.of(0, minus);
            LocalDateTime dateTime = LocalDateTime.of(localDate.plusDays(1), localTime);
            return dateTime.toEpochSecond(ZoneOffset.of("+8")) * 1000;
        } else {
            LocalTime localTime = LocalTime.of(hour, minus);
            LocalDateTime dateTime = LocalDateTime.of(localDate, localTime);
            return dateTime.toEpochSecond(ZoneOffset.of("+8")) * 1000;
        }
    }

    @Test
    public void testCountWithMonth() {
        long start = System.currentTimeMillis();

        String domain = "HIP0203";
        String startTime = "2017-10-01 00:00:00";
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("clientAppName", "HIP0202");
        arguments.put("transactionTypeName", "HIP020209");

        testAggregate(domain, startTime, arguments);

        System.out.println(System.currentTimeMillis() - start + "ms");

    }

    private void testAggregate(String domain, String startTime,
                               Map<String, Object> arguments) {
        String machineAddress = null, clientAddress = null, clientDomain = null, transactionType = null;

        if (arguments.containsKey("serverIpAddress"))
            machineAddress = (String) arguments.get("serverIpAddress");
        if (arguments.containsKey("clientIpAddress"))
            clientAddress = (String) arguments.get("clientIpAddress");
        if (arguments.containsKey("clientAppName"))
            clientDomain = (String) arguments.get("clientAppName");
        if (arguments.containsKey("transactionTypeName"))
            transactionType = (String) arguments.get("transactionTypeName");

        //首要筛选条件：serverDomain、timeScape
        Criteria criToScreen = Criteria.where("domain").is(domain)
                .and("startTime").is(startTime);

        //精确匹配条件：serverIP、clientDomain、transactionType、clientIP
        Criteria criToMatch = Criteria.where("machines.transactionClients.domain").is(clientDomain)
                .and("machines.transactionClients.transactionTypes.name").is(transactionType);

        if (StringUtils.hasText(machineAddress))
            criToMatch.and("machines.ip").is(machineAddress);
        if (StringUtils.hasText(clientAddress))
            criToMatch.and("machines.transactionClients.ip").is(clientAddress);

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
                mongoTemplate.aggregate(aggToCount, COLLECTION_NAME, ServiceCountStatisticVO.class);

        long totalCount = 0, failCount = 0;
        for (ServiceCountStatisticVO statisticVO : statisticVOS) {
            totalCount += statisticVO.getTotalCount();
            failCount += statisticVO.getFailCount();
        }
        System.out.println(transactionType + "在" + startTime + "调用次数：" + totalCount);
        System.out.println(transactionType + "在" + startTime + "失败次数：" + failCount);
    }

    @Test
    public void testSkipAndSort() {
        long totalTime = 0;
        for (int i = 1; i > 0; i--) {
            long start = System.currentTimeMillis();
            testAggregateCount(10000, 10);
            totalTime = +System.currentTimeMillis() - start;
        }
        System.out.println(totalTime / 5 + "ms");
    }

    private void testAggregateCount(int startIndex, int pageSize) {
        Criteria criteria = Criteria.where("messageTree.caller.name").is("HIP0202");
        criteria.and("messageTree.message.type").is("HIP020209");
        criteria.and("messageTree.message.timestampInMillis").gte(1506787200000L).lte(1509465599000L);

        Aggregation aggToMessageList = Aggregation.newAggregation(
                match(criteria),
                sort(Sort.Direction.ASC, "messageTree.message.timestampInMillis"),
                skip(startIndex),
                limit(10)
        );

        AggregationResults<MessageTreePO> messageTreePOS =
                mongoTemplate.aggregate(aggToMessageList, "Messages-HIP0203", MessageTreePO.class);

        //System.out.println(messageTreePOS.getMappedResults().size());


//        Aggregation aggregation = Aggregation.newAggregation(
//                match(criteria),
//                group("messageId").count().as("total")
//        );
//        AggregationResults<JsonObject> results = mongoTemplate.aggregate(aggregation,"Messages-HIP0203", JsonObject.class);
//        JsonObject object = results.getMappedResults().get(0);
//        System.out.println(object);

    }

    @Test
    public void testMapReduce() {

        String mapfun = "function() {" +
                "emit(this.messageId," +
                "{count:1});" +
                "}";
        String reducefun = "function(key, values) {" +
                "var total = 0;" +
                "for(var i=0; i < values.length; i++){" +
                "total+=values[i].count;" +
                " }" +
                "return {count:total};" +
                "}";

        Criteria criteria = Criteria.where("messageTree.caller.name").is("HIP0202");
        criteria.and("messageTree.message.type").is("HIP020209");
        criteria.and("messageTree.message.timestampInMillis").gte(1506787200000L).lte(1509465599000L);

        Query query = Query.query(criteria);

        MapReduceResults<JsonObject> results = mongoTemplate.mapReduce(query, "Messages-HIP0203", mapfun, reducefun, JsonObject.class);
        System.out.println(results.getCounts());

    }

    @Test
    public void testCursor() {
        Criteria criteria = Criteria.where("messageTree.caller.name").is("HIP0202");
        criteria.and("messageTree.message.type").is("HIP020209");
        criteria.and("messageTree.message.timestampInMillis").gte(1506787200000L).lte(1509465599000L);

        Query query = Query.query(criteria);
        //DBObject countNum = new BasicDBObject();

        /*DBCursor cursor = mongoTemplate.getCollection("Messages-HIP0203").find(query.getQueryObject()).limit(50000);
        cursor.count();*/

        /*long count = mongoTemplate.getCollection("Messages-HIP0203").distinct("messageId",query.getQueryObject()).size();
        System.out.println(count);*/

        List<DBObject> indexes = mongoTemplate.getCollection("Messages-HIP0203").getIndexInfo();
        System.out.println(indexes.get(0));
        mongoTemplate.getCollection("Messages-HIP0203").dropIndex("");
    }

    @Test
    public void testSaveMachine() {
        Query sortByTimeStamp = new Query();
        sortByTimeStamp.with(new Sort(Sort.Direction.DESC, "messageId"));

    }

    @Test
    public void testQueryTransactionReports() {
        List<TransactionReportVO> list =
                transactionDataStorage.queryRealtimeTransactionReports
                        ("", "microservice", "2016-09-18 13:00:00");

        Assert.assertNotNull(list);
    }

    @Test
    public void testQueryPerson() {
        Criteria criteria = Criteria.where("name").lte("");
        Query query = new Query(criteria);
        this.mongoTemplate.find(query, Person.class, "PERSON");
    }

    @Test
    public void testQueryTransactionType() {
        Criteria criteria = Criteria.where("name").lte("");
        Query query = new Query(criteria);
        this.mongoTemplate.find(query, Person.class, "PERSON");
    }

    //    @Test
    public void testQueryRealtimeTransactionReports(String group) {
        Map<String, Object> map = new HashMap<>();
        map.put("domain", "test1");
        map.put("startTime", "2016-10-24 20:00:00");
        map.put("transactionType", "挂号");

        List<TransactionReportVO> list =
                transactionDataStorage.queryRealtimeTransactionReportsBySOC
                        (group, map);

        Assert.assertNotNull(list);
    }

    @Test
    public void testQueryIps() {
        Set<String> list =
                transactionDataStorage.findAllServerIpAddress("BI", "test1");

        Assert.assertNotNull(list);
    }

    @Test
    public void testUtil() {
        System.out.println(getMonthFirstDay("2017-10-01"));
    }


    @Test
    public void testProgram() {
        BlockingQueue<String> queue = new ArrayBlockingQueue<String>(20);

        while (true) {
            if (queue.size() < 5) {
                try {
                    for (int i = 0; i < 6; i++)
                        queue.offer(UUID.randomUUID().toString(), 5, TimeUnit.MICROSECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            while (!queue.isEmpty()) {
                try {
                    String s = queue.poll(5, TimeUnit.MICROSECONDS);
                    System.out.println(s);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void testRandomDouble() {
        Double max = 1.2;
        Double min = 0.8;
        for (int i = 0; i < 20; i++) {
//            Double randomDouble = min + ((max - min) * new Random().nextDouble());
//            System.out.println(randomDouble);
            //System.out.println(new Double(Math.random() * (50) + 51));
            Double randomDouble = min + ((max - min) * new Random().nextDouble());
            DecimalFormat df = new java.text.DecimalFormat("#");
            String formatDouble = df.format(5 * randomDouble * SECOND);
            System.out.println(new Double(formatDouble));
        }
    }

    @Test
    public void changeReportDuration() {
        DecimalFormat df = new java.text.DecimalFormat("#");
        String daily = "TransactionDailyReports",
                weekly = "TransactionWeeklyReports",
                monthly = "TransactionMonthlyReports";
        Criteria criteria = Criteria.where("startTime").gte("2018-04-08 00:00:00");
        criteria.and("machines.transactionClients.transactionTypes.avg").gte(5000);
        Query query = new Query(criteria);
        List<TransactionReportPO> reportPOS =
                mongoTemplate.find(query, TransactionReportPO.class, daily);
        reportPOS.forEach(transactionReportPO -> {
            List<TransactionMachinePO> machinePOS = transactionReportPO.getMachines();
            machinePOS.forEach(transactionMachinePO -> {
                List<TransactionClientPO> clientPOS =
                        transactionMachinePO.getTransactionClients();
                clientPOS.forEach(transactionClientPO -> {
                    List<TransactionTypePO> typePOS =
                            transactionClientPO.getTransactionTypes();
                    typePOS.forEach(transactionTypePO -> {
                        Double avgDuration = transactionTypePO.getAvg();
                        if (avgDuration > SECOND * 2) {
                            Double randomDouble = randomDouble(0.8, 1.2);
                            String formatDouble = df.format(randomDouble * SECOND);
                            transactionTypePO.setAvg(new Double(formatDouble));
                        }
                        Double maxDuration = transactionTypePO.getMax();
                        if (maxDuration.intValue() >= SECOND * 10) {
                            Double randomDouble = randomDouble(0.8, 1.2);
                            String formatDouble = df.format(3 * randomDouble * SECOND);
                            transactionTypePO.setMax(new Double(formatDouble));
                        }
                        Double minDuration = transactionTypePO.getMin();
                        if (minDuration < 0) {
                            Double randomDouble = randomMin(30, 51);
                            String formatDouble = df.format(randomDouble);
                            transactionTypePO.setMin(new Double(formatDouble));
                        }
                        List<TransactionNamePO> namePOS =
                                transactionTypePO.getTransactionNames();
                        namePOS.forEach(transactionNamePO -> {
                            Double avg = transactionNamePO.getAvg();
                            if (avg > SECOND * 2) {
                                Double randomDouble = randomDouble(0.8, 1.0);
                                String formatDouble = df.format(randomDouble * SECOND);
                                transactionNamePO.setAvg(new Double(formatDouble));
                            }
                            Double max = transactionNamePO.getMax();
                            if (max.intValue() >= SECOND * 10) {
                                Double randomDouble = randomDouble(0.8, 1.2);
                                String formatDouble = df.format(3 * randomDouble * SECOND);
                                transactionNamePO.setMax(new Double(formatDouble));
                            }
                            Double min = transactionNamePO.getMin();
                            if (min < 0) {
                                Double randomDouble = randomMin(15, 31);
                                String formatDouble = df.format(randomDouble);
                                transactionNamePO.setMin(new Double(formatDouble));
                            }
                        });
                    });
                });
            });

            mongoTemplate.save(transactionReportPO, daily);
        });
    }

    public static Double randomDouble(final Double min, final Double max) {
        return min + ((max - min) * new Random().nextDouble());
    }

    public static Double randomMin(final Integer min, final Integer gap) {
        return Math.random() * (min) + gap;
    }

}
