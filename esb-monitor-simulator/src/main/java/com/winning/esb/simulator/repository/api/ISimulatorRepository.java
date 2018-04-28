package com.winning.esb.simulator.repository.api;

import com.winning.monitor.data.storage.mongodb.po.message.MessageTreePO;

import java.util.List;
import java.util.Set;

/**
 * @Author Lemod
 * @Version 2018/4/4
 */
public interface ISimulatorRepository {

    Set<String> querySampleCollections();

    //查询总次数
    long querySampleCount(String collectionName, long start, long end);

    //查询具体明细
    List<MessageTreePO> queryHourMessageTree(String collectionName,
                                             long start, long end, int limit);
}
