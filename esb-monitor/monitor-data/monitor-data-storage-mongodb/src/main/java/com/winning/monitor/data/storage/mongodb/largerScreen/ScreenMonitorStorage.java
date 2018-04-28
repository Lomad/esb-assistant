package com.winning.monitor.data.storage.mongodb.largerScreen;

import com.winning.monitor.data.api.base.RunningStatusUnPTVO;
import com.winning.monitor.data.api.largerScreen.entity.ServiceCount;
import com.winning.monitor.data.storage.api.largerScreen.IScreenMonitorStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.Field;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.winning.monitor.data.api.enums.TransactionReportsEnum.*;

/**
 * @Author Lemod
 * @Version 2018/2/1
 */
@Repository
public class ScreenMonitorStorage implements IScreenMonitorStorage {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<ServiceCount> queryServiceCountList(String startTime, String status) {
        String sumField = TYPES;
        if (status.equals("Total")) {
            sumField += ".totalCount";
        } else {
            sumField += ".failCount";
        }
        Field serverName = Fields.field("serverName", "$domain");
        Field serviceName = Fields.field("serviceName",
                "$machines.transactionClients.transactionTypes._id");
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(new Criteria("startTime").gte(startTime)),
                Aggregation.unwind(MACHINES), Aggregation.unwind(CLIENTS),
                Aggregation.unwind(TYPES),
                Aggregation.group(Fields.from(serverName, serviceName)).sum(sumField).as("count"),
                Aggregation.sort(Sort.Direction.DESC, "count"), Aggregation.limit(5)
        );
        return mongoTemplate.aggregate(aggregation,
                COLLECTION_REALTIME, ServiceCount.class).getMappedResults();
    }

    @Override
    public List<RunningStatusUnPTVO> querySystemList(String startTime) {
        Criteria criteria = new Criteria("startTime").gte(startTime);

        Field server = Fields.field("server", "$domain");
        Field client = Fields.field("client", CLIENTS + ".domain");
        Fields groupFields = Fields.from(server, client);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.unwind(MACHINES), Aggregation.unwind(CLIENTS),
                Aggregation.unwind(TYPES), Aggregation.group(groupFields)
                        .sum(TYPES + ".failCount").as("failCount")
        );

        return mongoTemplate.aggregate(aggregation,
                COLLECTION_REALTIME, RunningStatusUnPTVO.class).getMappedResults();
    }

    @Override
    public List<ServiceCount> queryServiceList(String startTime,
                                               Map<String, Object> params) {
        String provider = (String) params.get("provider"),
                consumer = (String) params.get("consumer"),
                status = (String) params.get("status");

        Criteria baseCri = Criteria.where("startTime").gte(startTime);
        baseCri.and("domain").is(provider);

        String sumField;
        if (status.equalsIgnoreCase("Total")) {
            sumField = TYPES + ".totalCount";
        } else {
            sumField = TYPES + ".failCount";
        }
        Field serverName = Fields.field("serverName", "$domain");
        Field serviceName = Fields.field("serviceName",
                "$machines.transactionClients.transactionTypes._id");

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(baseCri),
                Aggregation.unwind(MACHINES), Aggregation.unwind(CLIENTS),
                Aggregation.match(Criteria.where(CLIENTS + ".domain").is(consumer)),
                Aggregation.unwind(TYPES),
                Aggregation.group(Fields.from(serverName, serviceName))
                        .sum(sumField).as("count")
        );

        return mongoTemplate.aggregate(aggregation,
                COLLECTION_REALTIME, ServiceCount.class).getMappedResults();
    }
}
