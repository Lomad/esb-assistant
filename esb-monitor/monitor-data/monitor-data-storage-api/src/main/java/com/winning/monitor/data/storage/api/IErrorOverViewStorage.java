package com.winning.monitor.data.storage.api;

import com.winning.monitor.data.storage.api.entity.MessageTreeList;

import java.util.List;
import java.util.Map;

/**
 * Created by nicholasyan on 16/9/30.
 */
public interface IErrorOverViewStorage {
    /**
     * 获取发生错误的提供方系统代码(appId)列表
     *
     * @param map key取值如下：
     *            startTime - 开始时间（格式：2017-12-14 15:33:04），
     *            endTime - 结束时间（格式：2017-12-14 15:33:04），
     */
    List<String> countErrorProviders(Map<String, Object> map);

    /**
     * 统计错误信息
     *
     * @param map key取值如下：
     *            startTime - 开始时间（格式：2017-12-14 15:33:04），
     *            endTime - 结束时间（格式：2017-12-14 15:33:04），
     *            appId - 系统代码，
     *            appIdList - 系统代码列表，
     *            svcCode - 服务代码，
     *            svcCodeList - 服务代码列表
     * @return 返回Map列表，key取值如下：
     * svcCode - 服务代码，
     * totalCount - 调用次数，
     * failCount - 失败次数，
     * failPercent - 失败率
     */
    List<Map<String, Object>> countErrorList(Map<String, Object> map);


    MessageTreeList queryTodayErrorMessageList(String serverId, long lowerTimestamp, String keyWords,
                                               int startIndex, int pageSize);
}