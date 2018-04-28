package com.winning.monitor.supervisor.core.timer.mongo;

import com.winning.monitor.agent.logging.message.MessageTree;

import java.util.Map;

/**
 * @Author Lemod
 * @Version 2017/5/8
 */
public interface CleanJob {

    /**
     * 查询数据库中设定的清除周期，并计算对应的清理日期
     */
    long getClearDate();

    /**
     * 数据库删除工作
     * @param period 截止日期
     * @param domain collection名字
     * @return 删除条数
     */
    int remove(long period, String domain);

    /**
     * 对各表建立符合查询需求的特定索引，且顺序确定
     */
    void ensureIndex();

    void removeErrorLoggingEntity();

    void storeTopDuration(Map<Long,MessageTree> treeMap);

}