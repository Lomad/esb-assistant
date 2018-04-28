package com.winning.esb.simulator.repository;

import com.winning.esb.simulator.repository.api.ISimulatorRepository;
import com.winning.monitor.data.storage.mongodb.po.message.MessageTreePO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

import static com.winning.esb.simulator.utils.GlobalConstant.MESSAGE_TREE_TIMESTAMP;

/**
 * @Author Lemod
 * @Version 2018/4/4
 */
@Repository
public class SimulatorRepository implements ISimulatorRepository {

    private static final Logger logger = LoggerFactory.getLogger(SimulatorRepository.class);

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
    public Set<String> querySampleCollections() {
        return mongoTemplate.getCollectionNames();
    }

    @Override
    public long querySampleCount(String collectionName, long start, long end) {
        Criteria criteria = Criteria.where(MESSAGE_TREE_TIMESTAMP).gte(start).lt(end);

        return mongoTemplate.count(new Query(criteria), collectionName);
    }

    @Override
    public List<MessageTreePO> queryHourMessageTree(String collectionName,
                                                    long start, long end, int limit) {
        Criteria criteria = Criteria.where(MESSAGE_TREE_TIMESTAMP).gte(start).lt(end);

        return mongoTemplate.find(new Query(criteria).limit(limit),
                MessageTreePO.class, collectionName);
    }
}
