package com.winning.monitor.data.storage.api;

import com.winning.monitor.agent.logging.message.MessageTree;
import com.winning.monitor.data.api.base.common.CommonObject;
import com.winning.monitor.data.api.transaction.vo.TransactionReportVO;

import java.util.List;

/**
 * @Author Lemod
 * @Version 2017/9/13
 */
public interface IAsynchronousLogMessageStorage {

    void storeTemporaryTree(MessageTree tree);

    List<CommonObject> distinctPatentMessageID();

    MessageTree queryParent(String messageID,String domain);

    List<MessageTree> queryChildren(String parentMessageID);

    int removeDiscardedMessageTree(String messageId);

    int updateMessageTreePO(MessageTree tree,String domain);

    boolean updateRealTimeReport(TransactionReportVO reportVO);
}
