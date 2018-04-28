package com.winning.esb.dao;

import com.winning.esb.model.AppInfoModel;
import com.winning.esb.model.common.CommonObject;

import java.util.List;
import java.util.Map;

/**
 * Created by xuehao on 2017/8/21.
 */
public interface IAppInfoDao {
    void insert(List<AppInfoModel> objs);

    void update(List<AppInfoModel> objs);

    /**
     * 更新状态
     */
    void updateStatus(List<Integer> ids, Integer status);

    /**
     * 根据id删除业务系统
     * @param id    支持Inteter与List
     */
    void delete(Object id);

    List<AppInfoModel> getByID(List<Integer> idList);

    /**
     * 根据appId获取系统
     * @param appId 支持String和List<String>
     */
    List<AppInfoModel> getByAppId(Object appId);

    CommonObject query(Map map);
}