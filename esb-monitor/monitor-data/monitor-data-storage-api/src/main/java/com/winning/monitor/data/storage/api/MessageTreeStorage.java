package com.winning.monitor.data.storage.api;

import com.winning.monitor.agent.logging.message.MessageTree;
import com.winning.monitor.data.api.transaction.vo.TransactionReportType;
import com.winning.monitor.data.storage.api.entity.MessageTreeList;

import java.util.Map;

/**
 * @author nicholasyan
 * @date 16/9/30
 */
public interface MessageTreeStorage {

    void storeTransaction(MessageTree tree);

    MessageTreeList queryMessageTree(String group, String domain,
                                     TransactionReportType dateType, String time,
                                     Map<String, Object> arguments,
                                     int startIndex, int pageSize,
                                     Map<String, Object> previous);

    MessageTreeList queryMessageTree(String group, String messageId, String domain);

    /**
     * 通用的获取调用消息的明细记录，支持当前小时、指定小时、当天、历史等
     * xuehao 2018-03-25：新增
     */
    MessageTreeList queryMessageTree(String group, String domain, Map<String, Object> arguments, int startIndex, int pageSize);

}