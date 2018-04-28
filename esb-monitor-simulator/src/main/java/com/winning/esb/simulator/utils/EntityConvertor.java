package com.winning.esb.simulator.utils;

import com.winning.esb.simulator.utils.entity.Data;
import com.winning.esb.simulator.utils.entity.LoggingEntity;
import com.winning.esb.simulator.utils.entity.RemoteCaller;
import com.winning.esb.simulator.utils.entity.TransactionCopy;
import com.winning.esb.stable.MonitorConst;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.StringUtils;
import com.winning.monitor.agent.logging.message.Caller;
import com.winning.monitor.agent.logging.message.LogMessage;
import com.winning.monitor.agent.logging.message.internal.DefaultMessageTree;
import com.winning.monitor.agent.logging.transaction.DefaultTransaction;
import com.winning.monitor.data.storage.mongodb.po.message.MessageTreePO;

import java.util.*;

import static com.winning.esb.simulator.utils.GlobalUtils.formatLongTime;

/**
 * @Author Lemod
 * @Version 2018/4/4
 */
public class EntityConvertor {

    public static LoggingEntity convertMessageTreePO(MessageTreePO treePO) {
        LoggingEntity entity = new LoggingEntity();

        DefaultMessageTree messageTree = (DefaultMessageTree) treePO.getMessageTree();

        entity.setProviderAddress(messageTree.getIpAddress());
        entity.setProviderHostName(messageTree.getHostName());
        entity.setSourceProvider(messageTree.getDomain());

        Caller caller = messageTree.getCaller();
        RemoteCaller remoteCaller = new RemoteCaller();
        remoteCaller.setIp(caller.getIp());
        remoteCaller.setName(caller.getName());
        remoteCaller.setType(caller.getType());
        entity.setRemoteCaller(remoteCaller);

        DefaultTransaction transaction = (DefaultTransaction) messageTree.getMessage();
        TransactionCopy transactionCopy = convertTransaction(transaction);
        entity.setTransactionCopy(transactionCopy);

        return entity;
    }

    public static TransactionCopy convertTransaction(DefaultTransaction transaction) {
        TransactionCopy transactionCopy = new TransactionCopy();
        transactionCopy.setType(transaction.getType());
        transactionCopy.setName(transaction.getName());

        long currentTime = System.currentTimeMillis();
        long duration = transaction.getDurationInMillis();
        String startTime = formatLongTime(currentTime);
        String endTime = formatLongTime(currentTime + duration);
        transactionCopy.setStartTime(startTime);
        transactionCopy.setEndTime(endTime);

        Map<String, Object> dataMap = transaction.getData();
        List<Data> dataList;
        if (dataMap != null && dataMap.size() > 0) {
            dataList = new ArrayList<>();
            dataMap.forEach((key, value) -> {
                //转换data里面的sourceTime
                if (key.equals("sourceTime")) {
                    int sourceDuration = new Random().nextInt(new Long(duration).intValue() / 3 + 1);
                    String sourceStart = formatLongTime(currentTime + sourceDuration);
                    String sourceEnd = formatLongTime(currentTime + duration - sourceDuration);
                    value = sourceStart + "&" + sourceEnd;
                }
                Data data = new Data();
                data.setKey(key);
                data.setValue(value);
                dataList.add(data);
            });
            transactionCopy.setDataList(dataList);
        }

        List<LogMessage> children = transaction.getChildren();
        List<TransactionCopy> transactionCopies;
        if (children != null && children.size() > 0) {
            transactionCopies = new ArrayList<>();
            children.forEach((child) -> {
                TransactionCopy transactionCopy1 =
                        convertTransaction((DefaultTransaction) child);
                transactionCopies.add(transactionCopy1);
            });
            transactionCopy.setChildren(transactionCopies);
        }
        transactionCopy.setStatus(transaction.getStatus());

        return transactionCopy;
    }

    /**
     * 修改埋点的关键信息
     */
    public static void updateKeyInfo(TransactionCopy transactionCopy) {
        try {
            //设置时间
            long currentTime = System.currentTimeMillis();
            long duration = 80 + new Random().nextInt(120);
            String startTime = formatLongTime(currentTime);
            String endTime = formatLongTime(currentTime + duration);
            transactionCopy.setStartTime(startTime);
            transactionCopy.setEndTime(endTime);

            //设置Data节点
            List<Data> dataList = transactionCopy.getDataList();
            if (!ListUtils.isEmpty(dataList)) {
                Map<String, Object> dataMap = new HashMap<>();
                dataList.forEach(data -> {
                    dataMap.put(data.getKey(), data.getValue());
                });

                //转换data里面的sourceTime
                if (dataMap.containsKey(MonitorConst.KeySourceTime)) {
                    int sourceDuration = new Random().nextInt(new Long(duration).intValue() / 4 + 1);
                    String sourceStart = formatLongTime(currentTime + sourceDuration);
                    String sourceEnd = formatLongTime(currentTime + duration - sourceDuration);
                    dataMap.put(MonitorConst.KeySourceTime, sourceStart + "&" + sourceEnd);
                }

                //获取输入消息
                String reqMsg = (String) dataMap.get(MonitorConst.KeyRequestEsb);
                //替换关键ID和消息ID
                if (!StringUtils.isEmpty(reqMsg)) {
                    //生成消息ID
                    String msgID = UUID.randomUUID().toString();
                    //替换消息中的消息ID
                    reqMsg.replace(String.valueOf(dataMap.get(MonitorConst.KeyMessageID)), msgID);
                    //替换Map中的消息ID
                    dataMap.put(MonitorConst.KeyMessageID, msgID);

//                //设置关键ID
//                Object val = dataMap.get(MonitorConst.KeyMainId);
//                if(!StringUtils.isEmpty(val)) {
//
//                }
                }

                List<Data> dataListNew = new ArrayList<>();
                dataMap.forEach((key, value) -> {
                    Data data = new Data();
                    data.setKey(key);
                    data.setValue(value);
                    dataListNew.add(data);
                });
                transactionCopy.setDataList(dataListNew);
            }
        } catch (Exception ex) {

        }
    }
}
