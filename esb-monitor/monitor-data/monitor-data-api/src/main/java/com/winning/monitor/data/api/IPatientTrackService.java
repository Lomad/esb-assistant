package com.winning.monitor.data.api;

import com.winning.monitor.data.api.transaction.domain.TransactionMessageList;

import java.util.List;

/**
 * Created by nicholasyan on 16/9/14.
 */
public interface IPatientTrackService {

    TransactionMessageList queryPatientServices(String domain, String queryField, String limit,
                                                String startTime, String endTime,
                                                int startIndex, int pageSize);

    List<String> queryDataFields(String serverId);
}