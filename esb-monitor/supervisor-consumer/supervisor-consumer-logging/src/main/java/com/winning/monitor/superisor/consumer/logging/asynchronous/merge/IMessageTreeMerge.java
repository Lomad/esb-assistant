package com.winning.monitor.superisor.consumer.logging.asynchronous.merge;

import com.winning.monitor.data.api.base.common.CommonObject;

import java.util.List;

/**
 * @Author Lemod
 * @Version 2017/10/11
 */
public interface IMessageTreeMerge {

    List<String> mergeMessageTree(List<CommonObject> idList);

}
