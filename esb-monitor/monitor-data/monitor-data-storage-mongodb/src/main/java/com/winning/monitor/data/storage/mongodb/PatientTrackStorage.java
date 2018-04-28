package com.winning.monitor.data.storage.mongodb;

import com.winning.monitor.data.storage.api.IPatientTrackStorage;
import com.winning.monitor.data.storage.api.entity.MessageTreeList;
import com.winning.monitor.data.storage.mongodb.po.message.MessageTreePO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.winning.monitor.data.storage.Utils.ConvertUtils.GetCollectionName;

/**
 * @Author Lemod
 * @Version 2018/1/9
 */
@Repository
public class PatientTrackStorage implements IPatientTrackStorage {
    private static final String SPECIFIED_FIELD = "messageTree.message.data.";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public MessageTreeList queryPatientMessageTree(String domain, String queryField, String limit,
                                                   long startTime, long endTime,
                                                   int startIndex, int pageSize) {
        String collectionName = GetCollectionName(domain);

        Criteria criteria = Criteria.where("messageTree.message.timestampInMillis")
                .gte(startTime).lte(endTime);
        criteria.and(SPECIFIED_FIELD + queryField).regex(limit, "i");
        Query query = Query.query(criteria).skip(startIndex).limit(pageSize);
        query.noCursorTimeout();

        List<MessageTreePO> treePOList = mongoTemplate.
                find(query, MessageTreePO.class, collectionName);

        if (!CollectionUtils.isEmpty(treePOList)) {
            MessageTreeList messageTreeList = new MessageTreeList();
            messageTreeList.setTotalSize(treePOList.size());

            treePOList.sort((o1, o2) -> {
                long com1 = o1.getMessageTree().getMessage().getTimestamp();
                long com2 = o2.getMessageTree().getMessage().getTimestamp();
                return com1 >= com2 ? -1 : 1;
            });
            for (MessageTreePO treePO : treePOList) {
                messageTreeList.addMessageTree(treePO.getMessageTree());
            }
            return messageTreeList;
        }
        return null;
    }

    @Override
    public Set<String> queryDataFields(String serverId) {
        String collectionName = GetCollectionName(serverId);

        Query query = new Query();
        query.with(new Sort(Sort.Direction.DESC, "_id"));
        query.limit(1);
        MessageTreePO treePO = mongoTemplate.findOne(query,
                MessageTreePO.class, collectionName);

        Object data = treePO.getMessageTree().getMessage().getData();
        if (data != null) {
            return ((Map<String, Object>) data).keySet();
        } else {
            return null;
        }
    }
}
