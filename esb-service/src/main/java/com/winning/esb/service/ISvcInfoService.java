package com.winning.esb.service;

import com.winning.esb.model.SvcInfoModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.SimpleObject;
import com.winning.esb.model.ext.SvcInfoExtModel;

import java.util.List;
import java.util.Map;

/**
 * Created by xuehao on 2017/8/9.
 */
public interface ISvcInfoService {
    /**
     * 保存服务
     * @return  item1 - 服务ID， item2 - 错误信息
     */
    SimpleObject save(SvcInfoModel obj);
    /**
     * 忽略数据的任何验证，直接新增到数据库【请慎用】
     */
    Integer insert(SvcInfoModel obj);

    /**
     * 更新上传的原始消息结构
     *
     * @param svcStructureDirection 消息方向
     */
    void updateRawContent(Integer id, Integer svcStructureDirection, String rawContent);

    String delete(Integer id);

    String delete(List<Integer> idList);

    /**
     * 发布服务
     */
    String publish(List<Integer> idList);

    /**
     * 回收（下架）服务
     */
    String rollback(List<Integer> idList);

    /**
     * 复制服务
     * @parm idList-服务id集合
     * @parm aidList-目标系统id集合
     */
    String copy(List<Integer> idList, List<Integer> aidList);

    /**
     * 返回服务所属的系统集合
     * @parm idList-服务id集合
     *
     */
    List<Integer> aidListFromSidList(List<Integer> idList);

    SvcInfoModel getByID(Integer id);

    List<SvcInfoModel> getByID(List<Integer> idList);

    /**
     * 根据系统ID获取服务列表
     */
    List<SvcInfoModel> getByAppId(String appId);
    /**
     * 根据系统ID获取服务列表
     */
    List<SvcInfoModel> getByAppId(List<String> appIdList);

    List<SvcInfoModel> getByCode(String code);

    List<SvcInfoModel> getByCode(List<String> codeList);

    /**
     * 获取服务代码列表
     * @param map   筛选条件
     */
    List<String> listCode(Map map);

    CommonObject query(Map map);

    CommonObject queryExt(Map map);

    int count(Map map);

    List<SvcInfoModel> list(Map map);

    List<SvcInfoExtModel> listExt(Map map);

    List<Map<String, Object>> listDownload(Integer aid, Integer svcDirection);

    SimpleObject download(Integer aid, List<Integer> sidList);

    /**
     * 获取ID与Name对应的简单队列列表
     */
    List<SimpleObject> listIdName();

    /**
     * 获取ID与Name对应的简单队列列表
     *
     * @param aidNotIn 排除的业务系统ID
     */
    List<SimpleObject> listIdNameByAidNotIn(Integer aidNotIn);

    /**
     * 获取ID与Name对应的简单队列列表
     *
     * @param aidNotInList 排除的业务系统ID
     */
    List<SimpleObject> listIdNameByAidNotIn(List<Integer> aidNotInList);

    /**
     * 获取id与对应的Map
     */
    Map<Integer, SvcInfoModel> mapIdObject(List<SvcInfoModel> objs);

    /**
     * 获取code与name对应的Map
     */
    Map<String, String> mapCodeName(List<String> codeList);

    /**
     * 获取code与对象对应的Map
     */
    Map<String, SvcInfoModel> mapCodeObj(List<String> codeList);

    /**
     * 根据业务系统ID获取服务
     *
     * @param aidList 业务系统ID
     */
    List<SvcInfoModel> getByAid(List<Integer> aidList);

    /**
     * 根据业务系统ID获取服务
     *
     * @param aidNotInList 排除的业务系统ID
     */
    List<SvcInfoModel> getByAidNotIn(List<Integer> aidNotInList);

    /**
     * 根据业务系统ID获取服务
     *
     * @param aidList      业务系统ID
     * @param aidNotInList 排除的业务系统ID
     */
    List<SvcInfoModel> getByAid(List<Integer> aidList, List<Integer> aidNotInList);

    /**
     * 根据业务系统ID获取服务
     *
     * @param aidList      业务系统ID
     * @param aidNotInList 排除的业务系统ID
     * @param idNotInList  排除的服务ID
     * @param queryWord    查询关键字，匹配服务代码和名称
     * @param svcStatus    服务状态
     */
    List<SvcInfoModel> getByAid(List<Integer> aidList, List<Integer> aidNotInList, List<Integer> idNotInList, String queryWord, Integer svcStatus);

    /**
     * 根据业务系统ID获取服务，并获取服务地址的状态
     *
     * @param aidList 业务系统ID
     */
    List<SvcInfoExtModel> getExtByAid(List<Integer> aidList);

    /**
     * 根据业务系统ID获取服务，并获取服务地址的状态
     *
     * @param aidNotInList 排除的业务系统ID
     */
    List<SvcInfoExtModel> getExtByAidNotIn(List<Integer> aidNotInList);

    /**
     * 根据业务系统ID获取服务，并获取服务地址的状态
     *
     * @param aidList      业务系统ID
     * @param aidNotInList 排除的业务系统ID
     */
    List<SvcInfoExtModel> getExtByAid(List<Integer> aidList, List<Integer> aidNotInList);

    List<SvcInfoModel> getByGroupId(List<Integer> groupIdList);

    /**
     * 将List转为Map，code与Object对应的Map
     */
    Map<String, SvcInfoModel> listToMapCodeObject(List<SvcInfoModel> svcInfoModelList);

    /**
     * 将List转为Map，key - 系统ID，value - 所属系统的所有服务对象列表
     */
    Map<Integer, List<SvcInfoModel>> listToMapAidSvc(List<SvcInfoModel> svcInfoModelList);

    /**
     * 获取服务代码列表
     */
    List<String> listSvcCode(List<SvcInfoModel> svcInfoModelList);
}