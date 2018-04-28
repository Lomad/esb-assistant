package com.winning.monitor.data.transaction;

import com.winning.esb.service.IAppInfoService;
import com.winning.esb.service.ISvcInfoService;
import com.winning.esb.utils.StringUtils;
import com.winning.monitor.data.api.IPatientTrackService;
import com.winning.monitor.data.api.transaction.domain.TransactionMessageList;
import com.winning.monitor.data.storage.api.IPatientTrackStorage;
import com.winning.monitor.data.storage.api.entity.MessageTreeList;
import com.winning.monitor.data.transaction.utils.DataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by nicholasyan on 16/9/14.
 */
@Service
public class PatientTrackService implements IPatientTrackService {
    private static final Logger logger = LoggerFactory.getLogger(PatientTrackService.class);

    @Autowired
    private IPatientTrackStorage patientTrackStorage;
    @Autowired
    private IAppInfoService appInfoService;
    @Autowired
    private ISvcInfoService svcInfoService;

    @Override
    public TransactionMessageList queryPatientServices(String domain, String queryField, String limit,
                                                       String startTime, String endTime,
                                                       int startIndex, int pageSize) {
        if (!StringUtils.isEmpty(domain, queryField, limit, startTime, endTime)) {
            startTime = startTime + ":00.000";
            endTime = endTime + ":59.999";

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            try {
                long startTimestamp = dateFormat.parse(startTime).getTime();
                long endTimestamp = dateFormat.parse(endTime).getTime();
                queryField = org.springframework.util.StringUtils.trimAllWhitespace(queryField);

                MessageTreeList treeList = patientTrackStorage
                        .queryPatientMessageTree(domain, queryField, limit, startTimestamp,
                                endTimestamp, startIndex, pageSize);

                if (treeList != null) {
                    TransactionMessageList transactionMessageList = new TransactionMessageList();
                    transactionMessageList.setTotalSize(treeList.getTotalSize());
                    transactionMessageList.setTransactionMessages(DataUtils.convertAppName(treeList, appInfoService, svcInfoService));

                    return transactionMessageList;
                }
            } catch (ParseException e) {
                logger.error("患者服务清单追踪失败！", e);
            }
        }
        return null;
    }

    @Override
    public List<String> queryDataFields(String serverId) {
        Set<String> fields = patientTrackStorage.queryDataFields(serverId);
        List<String> fieldList = new ArrayList<>(fields);
        Collections.sort(fieldList);
        return fieldList;
    }
}
