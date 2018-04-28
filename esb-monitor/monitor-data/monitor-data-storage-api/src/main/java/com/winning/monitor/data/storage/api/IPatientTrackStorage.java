package com.winning.monitor.data.storage.api;

import com.winning.monitor.data.storage.api.entity.MessageTreeList;

import java.util.Set;

/**
 * @Author Lemod
 * @Version 2018/1/9
 */
public interface IPatientTrackStorage {

    MessageTreeList queryPatientMessageTree(String domain, String queryField, String limit,
                                            long startTime, long endTime,
                                            int startIndex, int pageSize);

    Set<String> queryDataFields(String serverId);
}
