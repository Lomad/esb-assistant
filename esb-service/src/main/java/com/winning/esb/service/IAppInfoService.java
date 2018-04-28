package com.winning.esb.service;

import com.winning.esb.model.AppInfoModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.SimpleObject;
import com.winning.esb.model.common.TreeModel;
import com.winning.esb.model.ext.AppInfoExtModel;

import java.util.List;
import java.util.Map;

/**
 * Created by xuehao on 2017/8/22.
 */
public interface IAppInfoService {
    String save(AppInfoModel obj);

    /**
     * 忽略数据的任何验证，直接新增到数据库【请慎用】
     */
    void insert(AppInfoModel obj);

    /**
     * 忽略数据的任何验证，直接新增到数据库【请慎用】
     */
    void insert(List<AppInfoModel> objs);

    /**
     * 忽略数据的任何验证，直接新增到数据库【请慎用】
     */
    void update(AppInfoModel obj);

    /**
     * 忽略数据的任何验证，直接新增到数据库【请慎用】
     */
    void update(List<AppInfoModel> objs);

    /**
     * 更新状态
     */
    String updateStatus(List<Integer> ids, Integer status);

    String delete(List<Integer> idList);

    AppInfoModel getByID(Integer id);

    List<AppInfoModel> getByID(List<Integer> idList);

    CommonObject query(Map map);

    /**
     * 获取所有系统
     */
    List<AppInfoModel> list();
    List<AppInfoModel> list(Map map);

    /**
     * 获取当前在用的系统
     */
    List<AppInfoModel> listActive(Map map);

    /**
     * 获取当前在用的系统（不包含ESB）
     */
    List<AppInfoModel> listActiveWithoutEsb();

    /**
     * 获取当前在用的系统（不包含ESB）
     */
    List<AppInfoModel> listActiveWithoutEsb(Map map);

    /**
     * 获取ESB类型的系统
     */
    List<AppInfoModel> listEsb();
    /**
     * 获取ESB类型的系统
     */
    List<AppInfoModel> listEsb(Map map);

    /**
     * 获取当前在用的系统，并统计其服务信息与错误情况
     */
    List<AppInfoExtModel> listActiveWithStatistic(Map datas);

    /**
     * 根据appId获取系统信息
     */
    AppInfoModel getByAppId(String appId);
    /**
     * 根据appId列表获取系统信息
     */
    List<AppInfoModel> getByAppId(List<String> appIds);

    /**
     * 判断代码或名称是否存在
     */
    boolean existCodeOrName(Integer id, String columnName, String columnValue);

    /**
     * 获取ID列表
     */
    List<Integer> listId(List<AppInfoModel> objs);

    /**
     * 获取AppId列表
     */
    List<String> listAppId(List<AppInfoModel> objs);

    /**
     * 获取ID与Name对应的简单队列列表
     */
    List<SimpleObject> listIdName();

    /**
     * 获取ID与AppId对应的简单队列列表
     */
    List<SimpleObject> listIdAppId();

    /**
     * 获取AppId与Name对应
     */
    Map<String, String> mapAppIdName();

    /**
     * 获取AppId与Name对应
     */
    Map<String, String> mapAppIdName(String... appIds);

    /**
     * 获取AppId与Name对应
     */
    Map<String, String> mapAppIdName(List<String> appIdList);

    /**
     * 获取AppId与对象对应
     */
    Map<String, AppInfoModel> mapAppIdObj(List<String> appIdList);

    /**
     * 生成信息系统与机构的关系树
     */
    List<TreeModel> createZTree(Map<String, Object> map) throws Exception;

    /**
     * 获取id与对应的Map
     */
    Map<Integer, AppInfoModel> mapIdObject(List<AppInfoModel> objs);

    /**
     * 获取id与对应的Map
     */
    Map<String, AppInfoModel> mapAppIdObject(List<AppInfoModel> objs);

}
