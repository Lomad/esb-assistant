package com.winning.monitor.data.api;

import com.winning.monitor.data.api.transaction.domain.TransactionMessageList;

import java.util.List;
import java.util.Map;

/**
 * 错误概览信息
 * @author xuehao
 * @date 17/12/14
 */
public interface IErrorOverViewService {

    /**
     * 获取发生错误的提供方系统的主键(id)列表
     *
     * @param map key取值如下：
     *            timeType - 时间类型，参考“DateType”类型定义：21 - 今天， 23 - 本周， 24 - 本月
     */
    List<Integer> countErrorProviders(Map<String, Object> map);

    /**
     * 统计错误信息
     *
     * @param map key取值如下：
     *            queryWord - 查询关键字，
     *            timeType - 时间类型，参考“DateType”类型定义：21 - 今天， 23 - 本周， 24 - 本月，
     *            orgId - 机构ID，
     *            appId - 系统代码，
     *            appIdList - 系统代码列表，
     *            svcCode - 服务代码，
     *            svcCodeList - 服务代码列表
     * @return 返回Map列表，key取值如下：
     * svcCode - 服务代码
     * svcName - 服务名称
     * totalCount - 调用次数
     * failCount - 失败次数
     * failPercent - 失败率
     */
    List<Map<String, Object>> countErrorList(Map<String, Object> map);

    TransactionMessageList queryTodayErrorMessageList(String serverId, String keyWords, int startIndex, int pageSize);

}