package com.winning.esb.dao;

import com.winning.esb.model.SvcInfoModel;
import com.winning.esb.model.common.CommonObject;

import java.util.List;
import java.util.Map;

/**
 * Created by xuehao on 2017/8/9.
 */
public interface ISvcInfoDao {
    String TB_NAME = "ESB_SvcInfo";

    Integer insert(SvcInfoModel obj);

    void update(SvcInfoModel obj);

    /**
     * 更新上传的原始消息结构
     * @param svcStructureDirection 消息方向
     */
    void updateRawContent(Integer id, Integer svcStructureDirection, String rawContent);

    void delete(List<Integer> idlist);

    /**
     * 更新发布状态
     */
    void updateStatus(List<Integer> idlist, int status);

    List<SvcInfoModel> getByID(List<Integer> idList);

    /**
     * 根据系统的appId获取服务列表
     */
    List<SvcInfoModel> getByAppId(List<String> appIdList);

    /**
     * 根据服务代码获取服务列表
     */
    List<SvcInfoModel> getByCode(List<String> codeList);

    List<String> listCode(Map map);

    CommonObject query(Map map);

    int count(Map map);

    /**
     * 获取待下载的服务列表
     *
     * @param aid     业务系统ID
     * @param svcDirection null - 获取全部，1-获取提供的服务，2-获取订阅的服务
     */
    List<Map<String, Object>> listDownload(Integer aid, Integer svcDirection);

    /**
     * 根据业务系统ID获取服务
     *
     * @param aidList      业务系统ID
     * @param aidNotInList 排除的业务系统ID
     * @param idNotInList 排除的服务ID
     * @param queryWord 查询关键字，匹配服务代码和名称
     */
    List<SvcInfoModel> getByAid(List<Integer> aidList, List<Integer> aidNotInList, List<Integer> idNotInList, String queryWord, Integer svcStatus);

    List<SvcInfoModel> getByGroupId(List<Integer> groupIdList);
}