package com.winning.monitor.data.storage.mongodb;

import com.winning.monitor.agent.logging.transaction.DefaultTransaction;
import com.winning.monitor.data.api.base.*;
import com.winning.monitor.data.api.enums.QueryParameterKeys;
import com.winning.monitor.data.api.transaction.domain.TransactionStatisticData;
import com.winning.monitor.data.api.transaction.vo.*;
import com.winning.monitor.data.storage.Utils.ConvertUtils;
import com.winning.monitor.data.storage.api.ITransactionDataStorage;
import com.winning.monitor.data.storage.api.exception.StorageException;
import com.winning.monitor.data.storage.mongodb.po.message.MessageTreePO;
import com.winning.monitor.data.storage.mongodb.po.transaction.TransactionReportPO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.winning.monitor.data.api.enums.TransactionReportsEnum.MESSAGE_TREE_TIMESTAMP;
import static com.winning.monitor.data.storage.Utils.ConvertUtils.GetReportCollectionName;
import static com.winning.monitor.utils.DateUtils.HOUR;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;

/**
 * Created by nicholasyan on 16/9/14.
 */
@Service
public class TransactionDataStorage implements ITransactionDataStorage {

    //xuehao 2017-03-28：系统与接口分类映射表
    private final String AppSvcMap_COLLECTION_NAME = "AppSvcMap";
    private static final Logger logger = LoggerFactory.getLogger(TransactionDataStorage.class);
    private static final String REALTIME_COLLECTION_NAME = "TransactionRealtimeReports";
    private static List<String> ptApp;

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

//        ptApp = queryPTApp();
    }

    @Override
    public LinkedHashSet<String> queryAllSystems(String group, String kind) {

        List<String> domains = new ArrayList<>();

        List realTimeDomains = this.mongoTemplate.getCollection(REALTIME_COLLECTION_NAME).distinct(kind);
        if (!realTimeDomains.isEmpty()) {
            domains.addAll(realTimeDomains);
        }

        Collections.sort(domains);
        return new LinkedHashSet<>(domains);
    }


    @Override
    public LinkedHashSet<String> findAllTransactionClients(String group) {
        Query query = new Query();
        query.addCriteria(new Criteria("group").is(group));
        List<String> clients = this.mongoTemplate.getCollection(REALTIME_COLLECTION_NAME).distinct("machines.transactionClients.domain");
        clients.remove("");
        Collections.sort(clients);
        return new LinkedHashSet<>(clients);
    }

    @Override
    public LinkedHashSet<String> findAllServerIpAddress(String group, String domain) {
        Criteria criteria = Criteria.where("domain").is(domain)
                .and("group").is(group);
        Query query = new Query(criteria);
        List<String> ips = this.mongoTemplate.getCollection(REALTIME_COLLECTION_NAME)
                .distinct("machines.ip", query.getQueryObject());
        Collections.sort(ips);
        return new LinkedHashSet<>(ips);
    }

    @Override
    public List<TransactionReportVO> queryRealtimeTransactionReports(String group, String domain, String startTime) {
        Query query = new Query();
        query.addCriteria(new Criteria("domain").is(domain));
        query.addCriteria(new Criteria("startTime").is(startTime));
        query.addCriteria(new Criteria("type").is(TransactionReportType.REALTIME.getName()));
        query.addCriteria(new Criteria("group").is(group));
        List<TransactionReportVO> list =
                this.mongoTemplate.find(query, TransactionReportVO.class, REALTIME_COLLECTION_NAME);

        if (list != null) {
            for (TransactionReportVO report : list) {
                report.initialReport();
            }
        }

        return list;
    }

    @Override
    public List<TransactionReportVO> queryRealtimeClientTransactionReportsByServer(String group, String clientAppName, String serverAppName, String startTime) {
        Query query = new Query();
        query.addCriteria(new Criteria("machines.transactionClients.domain").is(clientAppName));
        query.addCriteria(new Criteria("startTime").is(startTime));
        query.addCriteria(new Criteria("domain").is(serverAppName));
        query.addCriteria(new Criteria("group").is(group));
        List<TransactionReportVO> list =
                this.mongoTemplate.find(query, TransactionReportVO.class, REALTIME_COLLECTION_NAME);

        if (list != null) {
            for (TransactionReportVO report : list) {
                report.initialReport();
            }
        }

        return list;
    }

    @Override
    public List<TransactionReportVO> queryRealtimeTransactionReportsBySOC(String group, Map<String, Object> map) {
        Query query = new Query();
        query.addCriteria(new Criteria("type").is(TransactionReportType.REALTIME.getName()));
        query.addCriteria(new Criteria("group").is(group));
        if (map != null) {
            if (map.containsKey("domain")) {
                query.addCriteria(new Criteria("domain").is(map.get("domain")));
            }
            if (map.containsKey("startTime")) {
                Criteria criteria;
                if (map.containsKey("endTime")) {
                    criteria = new Criteria("startTime").
                            gte(map.get("startTime")).
                            lte(map.get("endTime"));
                } else {
                    criteria = new Criteria("startTime").gte(map.get("startTime"));
                }
                query.addCriteria(criteria);
            }
            if (map.containsKey("transactionType")) {
                query.addCriteria(new Criteria("machines.transactionClients.transactionTypes.name").is(map.get("transactionType")));
            }
            if (map.containsKey("serverIp")) {
                query.addCriteria(new Criteria("machines.ip").is(map.get("serverIp")));
            }
            if (map.containsKey("clientAppName") && !StringUtils.isEmpty(map.get("clientAppName"))) {
                query.addCriteria(new Criteria("machines.transactionClients.domain").is(map.get("clientAppName")));
            }
        }
        List<TransactionReportVO> list =
                this.mongoTemplate.find(query, TransactionReportVO.class, REALTIME_COLLECTION_NAME);

        if (list != null) {
            for (TransactionReportVO report : list) {
                report.initialReport();
            }
        }

        return list;
    }

    @Override
    public List<TransactionReportVO> queryRealtimeClientTransactionReports(String group, Map<String, Object> map) {
        Query query = new Query();
        query.addCriteria(new Criteria("type").is(TransactionReportType.REALTIME.getName()));
        query.addCriteria(new Criteria("group").is(group));
        if (map != null && map.containsKey("domain")) {
            query.addCriteria(new Criteria("domain").is(map.get("domain")));
        }
        if (map != null && map.containsKey("startTime")) {
            query.addCriteria(new Criteria("startTime").is(map.get("startTime")));
        }
        if (map != null && map.containsKey("transactionType")) {
            query.addCriteria(new Criteria("machines.transactionClients.transactionTypes.name").is(map.get("transactionType")));
        }
        if (map != null && map.containsKey("serverIp")) {
            query.addCriteria(new Criteria("machines.ip").is(map.get("serverIp")));
        }
        if (map != null && map.containsKey("clientAppName")) {
            query.addCriteria(new Criteria("machines.transactionClients.domain").is(map.get("clientAppName")));
        }


        List<TransactionReportVO> list =
                this.mongoTemplate.find(query, TransactionReportVO.class, REALTIME_COLLECTION_NAME);

        if (list != null) {
            for (TransactionReportVO report : list) {
                report.initialReport();
            }
        }

        return list;
    }


    @Override
    public List<TransactionReportVO> queryRealtimeTransactionReports(String group, String domain, String startTime, String endTime, Map<String, Object> map) {
        Query query = new Query();
        query.addCriteria(new Criteria("domain").is(domain));
        query.addCriteria(new Criteria("startTime").gte(startTime).lte(endTime));
        query.addCriteria(new Criteria("type").is(TransactionReportType.REALTIME.getName()));
        query.addCriteria(new Criteria("group").is(group));


        if (map != null && map.containsKey("transactionType")) {
            query.addCriteria(new Criteria("machines.transactionClients.transactionTypes.name").is(map.get("transactionType")));
        }
        if (map != null && map.containsKey("serverIp")) {
            query.addCriteria(new Criteria("machines.ip").is(map.get("serverIp")));
        }
        if (map != null && map.containsKey("clientAppName")) {
            query.addCriteria(new Criteria("machines.transactionClients.domain").is(map.get("clientAppName")));
        }

        List<TransactionReportVO> list =
                this.mongoTemplate.find(query, TransactionReportVO.class, REALTIME_COLLECTION_NAME);

        if (list != null) {
            for (TransactionReportVO report : list) {
                report.initialReport();
            }
        }

        return list;
    }

    @Override
    public List<TransactionReportVO> queryRealtimeClientTransactionReports(String group, String domain, String startTime, String endTime, Map<String, Object> map) {
        Query query = new Query();
        query.addCriteria(new Criteria("machines.transactionClients.domain").is(domain));
        query.addCriteria(new Criteria("startTime").gte(startTime).lte(endTime));
        query.addCriteria(new Criteria("type").is(TransactionReportType.REALTIME.getName()));
        query.addCriteria(new Criteria("group").is(group));
        if (map.containsKey("serverAppName")) {
            query.addCriteria(new Criteria("domain").is(map.get("serverAppName")));
        }


        if (map != null && map.containsKey("transactionType")) {
            query.addCriteria(new Criteria("machines.transactionClients.transactionTypes.name").is(map.get("transactionType")));
        }

        List<TransactionReportVO> list =
                this.mongoTemplate.find(query, TransactionReportVO.class, REALTIME_COLLECTION_NAME);

        if (list != null) {
            for (TransactionReportVO report : list) {
                report.initialReport();
            }
        }

        return list;
    }


    @Override
    public List<TransactionReportVO> queryRealtimeTransactionReports(String group, String domain, String startTime, String endTime) {
//        Criteria criteria = Criteria.where("domain").is(domain)
//                .and("type").is(TransactionReportType.REALTIME.getName())
//                .and("startTime").gte(startTime).lte(endTime)
//                .and("group").is(group);
//        Query query = new Query(criteria);
//
//
//        List<TransactionReportPO> list =
//                this.mongoTemplate.find(query, TransactionReportPO.class, REALTIME_COLLECTION_NAME);
//
//        List<TransactionReportVO> transactionReports = this.convertTransactionReports(list);
//
//        return transactionReports;
        List<String> domains = new ArrayList<>();
        domains.add(domain);
        return queryRealtimeTransactionReports(group, domains, startTime, endTime);
    }

    @Override
    public List<TransactionReportVO> queryRealtimeTransactionReports(String group, List<String> domains, String startTime, String endTime) {
        Query query = new Query();
        if (domains != null && domains.size() == 1) {
            query.addCriteria(new Criteria("domain").is(domains.get(0)));
        } else {
            query.addCriteria(new Criteria("domain").in(domains));
        }
        query.addCriteria(new Criteria("startTime").gte(startTime).lte(endTime));
        query.addCriteria(new Criteria("type").is(TransactionReportType.REALTIME.getName()));
        query.addCriteria(new Criteria("group").is(group));
        List<TransactionReportPO> list = this.mongoTemplate.find(query, TransactionReportPO.class, REALTIME_COLLECTION_NAME);
        List<TransactionReportVO> transactionReports = this.convertTransactionReports(list);
        return transactionReports;
    }

    @Override
    public List<TransactionReportVO> queryRealtimeClientTransactionReports(String group, String domain, String startTime, String endTime) {
        Criteria criteria = Criteria.where("machines.transactionClients.domain").is(domain)
                .and("type").is(TransactionReportType.REALTIME.getName())
                .and("startTime").gte(startTime).lte(endTime)
                .and("group").is(group);
        Query query = new Query(criteria);


        List<TransactionReportPO> list =
                this.mongoTemplate.find(query, TransactionReportPO.class, REALTIME_COLLECTION_NAME);

        List<TransactionReportVO> transactionReports = this.convertTransactionReports(list);

        return transactionReports;
    }

    @Override
    public List<TransactionReportVO> querySpecifiedHourTransactionReports(String group, Map map) {

        Criteria criteria = null;
        if (map != null) {
            String domain = (String) map.get(QueryParameterKeys.DOMAIN.getKey());
            String startTime = (String) map.get(QueryParameterKeys.STARTTIME.getKey());
            String endTime = (String) map.get(QueryParameterKeys.ENDTIME.getKey());

            criteria = Criteria.where("domain").is(domain)
                    .and("startTime").gte(startTime).lte(endTime)
                    .and("group").is(group);

            if (!StringUtils.isEmpty(map.get("clientAppName"))){
                String clientAppName = (String) map.get("clientAppName");
                criteria.and("machines.transactionClients.domain").is(clientAppName);
            }
        }
        Query query = new Query(criteria);

        List<TransactionReportPO> list =
                this.mongoTemplate.find(query, TransactionReportPO.class, "TransactionHourlyReports");

        List<TransactionReportVO> transactionReports = this.convertTransactionReports(list);

        return transactionReports;
    }

    @Override
    public List<TransactionReportVO> queryHistoryTransactionReports(String group, String domain, String clientAppName, String startTime, String endTime, TransactionReportType type) {
        Criteria criteria = Criteria.where("domain").is(domain)
                .and("type").is(type.getName())
                .and("startTime").gte(startTime).lte(endTime)
                .and("group").is(group);

        if (StringUtils.hasText(clientAppName)) {
            criteria.and("machines.transactionClients.domain").is(clientAppName);
        }
        Query query = new Query(criteria);

        String collectionName = GetReportCollectionName(type);

        List<TransactionReportPO> list =
                this.mongoTemplate.find(query, TransactionReportPO.class, collectionName);

        List<TransactionReportVO> transactionReports = this.convertTransactionReports(list);

        return transactionReports;
    }

    @Override
    public List<TransactionReportVO> queryHistoryClientTransactionReports(String group,
                                                                          String domain,
                                                                          String serverAppName,
                                                                          String startTime,
                                                                          String endTime,
                                                                          TransactionReportType type) {
        Criteria criteria = Criteria.where("machines.transactionClients.domain").is(domain)
                .and("type").is(type.getName())
                .and("startTime").gte(startTime).lte(endTime)
                .and("group").is(group);

        if (StringUtils.hasText(serverAppName)) {
            criteria.and("domain").is(serverAppName);
        }
        Query query = new Query(criteria);

        String collectionName = GetReportCollectionName(type);

        List<TransactionReportPO> list =
                this.mongoTemplate.find(query, TransactionReportPO.class, collectionName);

        List<TransactionReportVO> transactionReports = this.convertTransactionReports(list);

        return transactionReports;
    }


    @Override
    public List<TransactionReportVO> queryHistoryTransactionReports(String group, String domain, String startTime, String endTime, TransactionReportType type, Map<String, Object> map) {
        Query query = new Query();
        query.addCriteria(new Criteria("domain").is(domain));
        query.addCriteria(new Criteria("startTime").gte(startTime).lte(endTime));
        query.addCriteria(new Criteria("type").is(type.getName()));
        query.addCriteria(new Criteria("group").is(group));
        String collectionName = GetReportCollectionName(type);

        if (map != null && map.containsKey("transactionType")) {
            query.addCriteria(new Criteria("machines.transactionClients.transactionTypes.name").is(map.get("transactionType")));
        }
        if (map != null && map.containsKey("serverIp")) {
            query.addCriteria(new Criteria("machines.ip").is(map.get("serverIp")));
        }
        if (map != null && map.containsKey("clientAppName")) {
            query.addCriteria(new Criteria("machines.transactionClients.domain").is(map.get("clientAppName")));
        }

        List<TransactionReportVO> list =
                this.mongoTemplate.find(query, TransactionReportVO.class, collectionName);

        if (list != null) {
            for (TransactionReportVO report : list) {
                report.initialReport();
            }
        }

        return list;
    }

    @Override
    public List<TransactionReportVO> queryHistoryClientTransactionReports(String group,
                                                                          String domain,
                                                                          String startTime,
                                                                          String endTime,
                                                                          TransactionReportType type,
                                                                          Map<String, Object> map) {
        Query query = new Query();
        query.addCriteria(new Criteria("machines.transactionClients.domain").is(domain));
        query.addCriteria(new Criteria("startTime").gte(startTime).lte(endTime));
        query.addCriteria(new Criteria("type").is(type.getName()));
        query.addCriteria(new Criteria("group").is(group));
        String collectionName = GetReportCollectionName(type);

        if (map != null && map.containsKey("transactionType")) {
            query.addCriteria(new Criteria("machines.transactionClients.transactionTypes.name").is(map.get("transactionType")));
        }
        if (map.containsKey("serverAppName")) {
            query.addCriteria(new Criteria("domain").is(map.get("serverAppName")));
        }

        List<TransactionReportVO> list =
                this.mongoTemplate.find(query, TransactionReportVO.class, collectionName);

        if (list != null) {
            for (TransactionReportVO report : list) {
                report.initialReport();
            }
        }

        return list;
    }


    @Override
    public List<TransactionReportVO> queryTransactionReportsByType(String group, String domain, String startTime, String typeName, TransactionReportType type) {
        Query query = new Query();
        query.fields().include("machines.transactionTypes.transactionNames");
        query.addCriteria(new Criteria("domain").is(domain));
        query.addCriteria(new Criteria("group").is(group));
        query.addCriteria(new Criteria("startTime").is(startTime));
        query.addCriteria(new Criteria("machines.transactionTypes.name").is(typeName));
        query.addCriteria(new Criteria("type").is(type));
        query.addCriteria(new Criteria("group").is(group));


        List<TransactionReportVO> list =
                this.mongoTemplate.find(query, TransactionReportVO.class, REALTIME_COLLECTION_NAME);


        if (list != null) {
            for (TransactionReportVO report : list) {
                report.initialReport();
            }
        }

        return list;
    }


    @Override
    public void storeRealtimeTransactionReport(TransactionReportVO transactionReport) throws StorageException {
        try {
            TransactionReportPO transactionReportPO = new TransactionReportPO(transactionReport);

            Query query = new Query();
            query.addCriteria(new Criteria("_id").is(transactionReport.getId()));


            boolean existsReport = this.mongoTemplate.exists(query, TransactionReportPO.class, REALTIME_COLLECTION_NAME);

            //如果不存在,则直接插入数据
            if (existsReport == false) {
                this.mongoTemplate.insert(transactionReportPO, REALTIME_COLLECTION_NAME);
                return;
            }

            //更新数据
            Update update = new Update();

            update.set("machines", transactionReportPO.getMachines());
            this.mongoTemplate.upsert(query, update, REALTIME_COLLECTION_NAME);
        } catch (Exception e) {
            logger.error("保存至mongodb时执行storeTransactionReport发生错误", e);
        }
    }

    @Override
    public void storeHistoryTransactionReport(TransactionReportVO transactionReport) {
        try {
            TransactionReportPO transactionReportPO = new TransactionReportPO(transactionReport);

            Query query = new Query();
            query.addCriteria(new Criteria("group").is(transactionReport.getGroup()));
            query.addCriteria(new Criteria("domain").is(transactionReport.getDomain()));
            query.addCriteria(new Criteria("startTime").is(transactionReport.getStartTime()));
            query.addCriteria(new Criteria("endTime").is(transactionReport.getEndTime()));
            query.addCriteria(new Criteria("type").is(transactionReport.getType().getName()));


            String collectionName = GetReportCollectionName(transactionReport.getType());

            boolean existsReport = this.mongoTemplate.exists(query, TransactionReportPO.class, collectionName);

            //如果不存在,则直接插入数据
            if (existsReport == false) {
                transactionReportPO.setId(UUID.randomUUID().toString());
                this.mongoTemplate.insert(transactionReportPO, collectionName);
                return;
            }

            //更新数据
            Update update = new Update();
            update.set("machines", transactionReportPO.getMachines());
            this.mongoTemplate.upsert(query, update, collectionName);
        } catch (Exception e) {
            logger.error("保存至mongodb时执行storeTransactionReport发生错误", e);
        }
    }


    private List<TransactionReportVO> convertTransactionReports(List<TransactionReportPO> reports) {
        List<TransactionReportVO> transactionReports = new ArrayList<>();
        if (reports == null || reports.size() == 0) {
            return transactionReports;
        }
        for (TransactionReportPO transactionReportPO : reports) {
            TransactionReportVO transactionReportVO = transactionReportPO.toTransactionReportVO();
            transactionReports.add(transactionReportVO);
        }
        return transactionReports;
    }

    private List<TransactionReportVO> convertToTransactionReports(List<LinkedHashMap> reportMaps) {
        List<TransactionReportVO> transactionReports = new ArrayList<>();
        if (reportMaps == null || reportMaps.size() == 0) {
            return transactionReports;
        }

        for (LinkedHashMap<String, Object> reportMap : reportMaps) {
            transactionReports.add(this.convertTransactionReport(reportMap));
        }

        return transactionReports;
    }

    private TransactionReportVO convertTransactionReport(LinkedHashMap<String, Object> reportMap) {
        TransactionReportVO transactionReportVO = new TransactionReportVO();
        transactionReportVO.setId(ConvertUtils.getStringValue(reportMap.get("id")));
        transactionReportVO.setDomain(ConvertUtils.getStringValue(reportMap.get("domain")));
        transactionReportVO.setGroup(ConvertUtils.getStringValue(reportMap.get("group")));
        transactionReportVO.setIndex(ConvertUtils.getIntValue(reportMap.get("idx")));
        transactionReportVO.setIps(ConvertUtils.getStringSetValue(reportMap.get("ips")));
        transactionReportVO.setType(TransactionReportType.valueOf(ConvertUtils.getStringValue(reportMap.get("type"))));
        transactionReportVO.setServer(ConvertUtils.getStringValue(reportMap.get("server")));
        transactionReportVO.setStartTime(ConvertUtils.getStringValue(reportMap.get("startTime")));
        transactionReportVO.setEndTime(ConvertUtils.getStringValue(reportMap.get("endTime")));

        return transactionReportVO;
    }

    //xuehao 2017-03-16：支持上海中医院设置历史查询的总数基数设置，数据库中需要增加Collections，名称为“BaseCountTemp”，字段说明如下：
    // reportType：统计类型（0-日统计，1-周统计，2-月统计）
    // svcName：接口名称（必须与统一监控前台界面中的接口名称一致）
    // totalCount：基数值（必须是整数，默认为0）
    // 格式如下：
    // {
    //    "reportType" : "2",
    //        "svcName" : "新增个人身份注册",
    //        "totalCount" : "0"
    // }
    @Override
    public void reviseStatisticsCount(List<TransactionStatisticData> tranList, String reportType) {
        try {
            if (tranList != null && tranList.size() > 0) {
                //获取数据库中的校正基数
                Query query = new Query();
                query.addCriteria(new Criteria("reportType").is(reportType));
                List<BaseCountTempVO> bctList = this.mongoTemplate.find(query, BaseCountTempVO.class, "BaseCountTemp");
                Map<String, Integer> bctMap = new HashMap<>();
                if (bctList != null && bctList.size() > 0) {
                    BaseCountTempVO bct;
                    for (int i = 0, len = bctList.size(); i < len; i++) {
                        bct = bctList.get(i);
                        bctMap.put(bct.getSvcName(), bct.getTotalCount());
                    }
                }

                //设置基数
                TransactionStatisticData tran;
                String tranName;
                for (int i = 0, len = tranList.size(); i < len; i++) {
                    tran = tranList.get(i);
                    tranName = tran.getTransactionTypeName();
                    if (bctMap.containsKey(tranName) && bctMap.get(tranName) != null) {
                        tran.setTotalCount(tran.getTotalCount() + bctMap.get(tranName));
                    }
                }
            }
        } catch (Exception ex) {

        }
    }

    @Override
    public List<RunningStatusUnPTVO> countUnPTRunningStatus(String startTime, String endTime, Map<String, Object> map) {
//        List<String> ptApp = queryPTApp();

        String domain = null, soc = null;
        if (map != null) {
            domain = (String) map.get("domain");
            soc = (String) map.get("soc");
        }

        Criteria criteria;
        if (StringUtils.hasText(endTime)) {
            criteria = new Criteria("startTime").gte(startTime).lte(endTime);
        } else {
            criteria = new Criteria("startTime").gte(startTime);
        }
        if (!StringUtils.isEmpty(soc)) {
            if ("SERVER".equals(soc)) {
                criteria.andOperator(new Criteria("domain").is(domain));
            } else {
                criteria.andOperator(new Criteria("machines.transactionClients.domain").is(domain));
            }
        }
        MatchOperation match = Aggregation.match(criteria);

        Field server = Fields.field("server", "$domain");
        Field client = Fields.field("client", "$machines.transactionClients.domain");
        Field router = Fields.field("router", "$machines.transactionClients.transactionTypes.router");
        Fields fields = Fields.from(server, client, router);

        Aggregation agg = Aggregation.newAggregation(
                match,
                unwind("machines"),
                unwind("machines.transactionClients"),
                unwind("machines.transactionClients.transactionTypes"),
                group(fields).addToSet("machines.transactionClients.transactionTypes.name")
                        .as("services").sum("machines.transactionClients.transactionTypes.totalCount").as("count")
                        .sum("machines.transactionClients.transactionTypes.failCount").as("failCount")
        );
        String collectionName;
        if (map.containsKey("reportType")) {
            collectionName = GetReportCollectionName((TransactionReportType) map.get("reportType"));
        } else {
            collectionName = REALTIME_COLLECTION_NAME;
        }

        AggregationResults<RunningStatusUnPTVO> results = mongoTemplate.aggregate(agg, collectionName, RunningStatusUnPTVO.class);
        List<RunningStatusUnPTVO> list = results.getMappedResults();

        return list;
    }


    @Override
    public List<SumVO> countAllServiceSizeByTop(String startTime, String endTime, Map<String, Object> map) {
        int top = 10;
        if (map != null && map.containsKey("top")) {
            top = Integer.parseInt(map.get("top").toString());
        }

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(new Criteria("startTime").gte(startTime).lte(endTime)),
                Aggregation.unwind("machines"),
                Aggregation.unwind("machines.transactionClients"),
                Aggregation.unwind("machines.transactionClients.transactionTypes"),
                Aggregation.group("machines.transactionClients.transactionTypes.name").sum("machines.transactionClients.transactionTypes.totalCount")
                        .as("totalSum").sum("machines.transactionClients.transactionTypes.failCount")
                        .as("failSum"),
                Aggregation.sort(Sort.Direction.DESC, "failSum"),
                Aggregation.limit(top)
        );
        AggregationResults<SumVO> results = mongoTemplate.aggregate(agg, REALTIME_COLLECTION_NAME, SumVO.class);
        List<SumVO> list = results.getMappedResults();
        return list;
    }

    @Override
    public Map<Integer, ServiceShowVO> getServiceFlowShow(String serverAppName, String messageId) {
        Map<Integer, ServiceShowVO> showLine = new LinkedHashMap<>();

        Query query = Query.query(new Criteria("messageId").is(messageId));
        MessageTreePO treePO = this.mongoTemplate.findOne(query, MessageTreePO.class, "Messages-" + serverAppName);

        this.addServiceShow(showLine, treePO);
        return showLine;
    }


    private void addServiceShow(Map<Integer, ServiceShowVO> showLine, MessageTreePO agent) {
        int index = showLine.size() + 1;

        DefaultTransaction transaction = (DefaultTransaction) agent.getMessageTree().getMessage();
        String s_timestamp = ConvertUtils.getTargetDateString(transaction.getTimestamp());
        String e_timestamp = ConvertUtils.getTargetDateString(transaction.getTimestamp() + transaction.getDurationInMillis());

        Map data = transaction.getData();
        if (data.containsKey("router")) {
            String router = (String) data.get("router");
            ServiceShowVO one_showVO = new ServiceShowVO(router, s_timestamp, e_timestamp, index);
            one_showVO.setMessageTree(agent.getMessageTree());
            showLine.put(index, one_showVO);

            MessageTreePO treePO2 = null;
            if (data.containsKey("MessageID")) {
                String uid = (String) data.get("MessageID");
                long startTimestamp = transaction.getTimestamp() - HOUR,
                        endTimestamp = startTimestamp + 2 * HOUR;

                Criteria criteria = Criteria.where(MESSAGE_TREE_TIMESTAMP)
                        .gte(startTimestamp).lte(endTimestamp);

                criteria.and("messageTree.message.data.MessageID").is(uid)
                        .and("messageTree.caller.name").is(router);

                Query query1 = Query.query(criteria);
//                System.out.println(query1.toString());
                treePO2 = mongoTemplate.findOne(query1, MessageTreePO.class, "Messages-" + agent.getDomain());
            }

            if (treePO2 == null) {
                String time = (String) data.get("sourceTime");
                String[] timeArray = time.split("&");
                ServiceShowVO two_showVO = new ServiceShowVO(agent.getDomain(),
                        ConvertUtils.getTargetDateString(timeArray[0]),
                        ConvertUtils.getTargetDateString(timeArray[1]),
                        index + 1);
                showLine.put(index + 1, two_showVO);
            } else {
                addServiceShow(showLine, treePO2);
            }
        } else {
            ServiceShowVO one_showVO = new ServiceShowVO(agent.getDomain(), s_timestamp, e_timestamp, index);
            one_showVO.setMessageTree(agent.getMessageTree());
            showLine.put(index, one_showVO);
        }
    }

    @Override
    public List<ServiceStatisticVO> getServiceStatistic(String serviceId, String startTime) {
        MatchOperation timeLimit = new MatchOperation(new Criteria("startTime").gte(startTime));
        MatchOperation exactLimit =
                new MatchOperation(new Criteria("machines.transactionClients.transactionTypes._id")
                        .is(serviceId));

        Field service = Fields.field("serviceId", "machines.transactionClients.transactionTypes._id"),
                providerId = Fields.field("providerId", "domain"),
                consumerId = Fields.field("consumerId", "machines.transactionClients.domain");

        Aggregation aggregation = Aggregation.newAggregation(
                timeLimit,
                unwind("machines"),
                unwind("machines.transactionClients"),
                unwind("machines.transactionClients.transactionTypes"),
                exactLimit,
                group(Fields.from(service, providerId, consumerId))
                        .sum("machines.transactionClients.transactionTypes.totalCount").as("totalCount")
                        .sum("machines.transactionClients.transactionTypes.failCount").as("failCount")
        );

        AggregationResults<ServiceStatisticVO> results = mongoTemplate.aggregate(aggregation, REALTIME_COLLECTION_NAME, ServiceStatisticVO.class);
        return results.getMappedResults();
    }

    //    @Override
//    public ServiceUrlVOList getUrls(Map map) {
//        ServiceUrlVOList voList = new ServiceUrlVOList();
//        List<ServiceUrlVO> urlVOList = new ArrayList<>();
//
//        if (map == null) {
//            List<ServiceUrlPO> serviceUrlPOS = mongoTemplate.findAll(ServiceUrlPO.class);
//            for (ServiceUrlPO urlPO : serviceUrlPOS) {
////                urlVOList.add(urlPO.toServiceUrlVO());
//            }
//
//            voList.setCount(urlVOList.size());
//            voList.setUrlVOList(urlVOList);
//        } else {
//            if (map.containsKey(QueryParameterKeys.TARGET.getKey())) {
//                String target = (String) map.get(QueryParameterKeys.TARGET.getKey());
//                ServiceUrlPO urlPO = mongoTemplate.findById(target, ServiceUrlPO.class);
////                urlVOList.add(urlPO.toServiceUrlVO());
//                voList.setCount(1);
//                voList.setUrlVOList(urlVOList);
//            } else {
//                int startIndex = (int) map.get("start");
//                int pageSize = (int) map.get(QueryParameterKeys.PAGESIZE.getKey());
//
//                Query query = new Query().skip(startIndex).limit(pageSize);
//                List<ServiceUrlPO> serviceUrlPOS = mongoTemplate.find(query, ServiceUrlPO.class);
//
//                long count = mongoTemplate.getCollection("SvcUrl").getCount();
//
//                for (ServiceUrlPO urlPO : serviceUrlPOS) {
////                    urlVOList.add(urlPO.toServiceUrlVO());
//                }
//                voList.setCount(count);
//                voList.setUrlVOList(urlVOList);
//            }
//        }
//        return voList;
//    }
//
//    @Override
//    public void saveInputUrl(String urlId, String svcType, String url) {
//        ServiceUrlPO urlPO = new ServiceUrlPO();
////        urlPO.setUrlId(urlId);
////        urlPO.setSvcType(svcType);
////        urlPO.setUrl(url);
////        urlPO.setStatus("");
//
//        this.mongoTemplate.save(urlPO);
//    }
//
//    @Override
//    public List<String> deleteUrl(String urlId) {
//        List<String> user = new ArrayList<>();
//        Query isExist = new Query();
//        isExist.addCriteria(new Criteria("urlPO._id").is(urlId));
//        List<ServiceInfoPO> infoPOList = this.mongoTemplate.find(isExist,ServiceInfoPO.class);
//
//        if (infoPOList != null && infoPOList.size() > 0){
//            for (ServiceInfoPO serviceInfoPO : infoPOList){
//                user.add(serviceInfoPO.getId());
//            }
//        }else {
//            Query query = new Query();
//            query.addCriteria(new Criteria("_id").is(urlId));
//
//            this.mongoTemplate.remove(query,ServiceUrlPO.class);
//        }
//
//        return user;
//    }
//
//    @Override
//    public List<String> queryPTApp() {
//
//        List<String> ptApp;
//
//        DBObject match = new BasicDBObject("appType", 1);
//        ptApp = mongoTemplate.getCollection("AppInfo").distinct("appName", match);
//
//        return ptApp;
//    }
}
