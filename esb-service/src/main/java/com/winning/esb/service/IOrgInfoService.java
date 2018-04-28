package com.winning.esb.service;

import com.winning.esb.model.OrgInfoModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.SimpleObject;

import java.util.List;
import java.util.Map;

/**
 * @author xuehao
 * @date 2017/8/9
 */
public interface IOrgInfoService {
    String save(OrgInfoModel obj);

    String delete(List<Integer> idList);

    OrgInfoModel getByID(Integer id);

    List<OrgInfoModel> getByID(List<Integer> idList);

    /**
     * 根据名称查询机构
     *
     * @param name 支持String与List
     */
    List<OrgInfoModel> getByName(Object name);

    List<OrgInfoModel> list();

    CommonObject query(Map map);

    /**
     * 获取ID与Name对应的简单队列列表
     */
    List<SimpleObject> listIdName();

    /**
     * 生成机构ID与对象的映射关系
     */
    Map<Integer, OrgInfoModel> mapIdObj(List<OrgInfoModel> list);
}