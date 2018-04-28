package com.winning.monitor.superisor.consumer.logging.asynchronous.merge;

import com.winning.monitor.agent.logging.message.LogMessage;
import com.winning.monitor.agent.logging.message.MessageTree;
import com.winning.monitor.agent.logging.transaction.DefaultTransaction;
import com.winning.monitor.agent.logging.transaction.Transaction;
import com.winning.monitor.data.api.base.common.CommonObject;
import com.winning.monitor.data.storage.api.IAsynchronousLogMessageStorage;
import com.winning.monitor.superisor.consumer.logging.asynchronous.merge.analyzer.ITransactionAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author Lemod
 * @Version 2017/10/11
 */
//@Component
public class MessageTreeMergeImpl implements IMessageTreeMerge {

    private static final Logger logger = LoggerFactory.getLogger(MessageTreeMergeImpl.class);

    @Autowired
    protected IAsynchronousLogMessageStorage logMessageStorage;
    @Autowired
    private ITransactionAnalyzer analyzer;

    @Override
    public List<String> mergeMessageTree(List<CommonObject> idList) {
        List<String> mergedIdList = new ArrayList<>();

        for (CommonObject object : idList) {
            String domain = (String) object.getData();

            List<String> ids = (List<String>) object.getDatas();
            for (String id : ids){
                //寻找父节点所在的Tree
                MessageTree parentTree = logMessageStorage.queryParent(id,domain);

                if (parentTree != null) {
                    List<MessageTree> children = logMessageStorage.queryChildren(id);
                    if (children != null && children.size() > 0) {
                        DefaultTransaction parentTransaction =
                                (DefaultTransaction) getParentTransaction(id, (Transaction) parentTree.getMessage());

                        logger.info("定位父级Transaction成功！" + parentTransaction.getType());

                        String type = parentTransaction.getType();
                        long duration = parentTransaction.getDurationInMicros();

                        for (MessageTree child : children){
                            DefaultTransaction childTrans = (DefaultTransaction) child.getMessage();
                            childTrans.setName(childTrans.getType());
                            childTrans.setType(type);
                            parentTransaction.addChild(child.getMessage());

                            insertMessageID(parentTransaction,childTrans);

                            //累加耗时
                            duration += childTrans.getDurationInMicros();
                        }
                        parentTransaction.setDurationInMicros(duration);
                    }
                    //更新数据库明细树
                    int updNumber = logMessageStorage.updateMessageTreePO(parentTree, domain);
                    logger.info("成功更新明细树：" + updNumber);

                    offerWithoutMissing(parentTree);

                    //Temporary-Messages中待删除数据
                    mergedIdList.add(id);
                }
            }
        }
        return mergedIdList;
    }

    private void offerWithoutMissing(MessageTree tree){
        boolean success = analyzer.offer(tree);
        if (!success) {
            logger.info("analyzer队列已满，等待插入...");
            offerWithoutMissing(tree);
        }
    }

    private Transaction getParentTransaction(String parentMessageID,Transaction root){
        //check root Transaction
        if (checkTransactionData(root,parentMessageID)) {
            return root;
        }

        //recursion check children
        List<LogMessage> children = root.getChildren();
        if (children != null && children.size() > 0) {
            for (LogMessage child : children) {
                Transaction res = getParentTransaction(parentMessageID, (Transaction) child);
                if (res != null) {
                    return res;
                }
            }
        }
        return null;
    }

    private boolean checkTransactionData(Transaction trans, String targetId) {
        Map datas = (Map) trans.getData();
        String headerId = (String) datas.get("MessageID");

        return headerId.equals(targetId);
    }

    private void insertMessageID(DefaultTransaction parent,DefaultTransaction child){
        Map datas = child.getData();
        String messageID = (String) datas.get("MessageID");
        parent.getHeaderIdList().add(messageID);
    }
}
