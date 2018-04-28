package com.winning.esb.dao;

import com.winning.esb.model.OrgInfoModel;
import com.winning.esb.model.common.CommonObject;

import java.util.List;
import java.util.Map;

/**
 * @author xuehao
 * @date 2017/8/9
 */
public interface IOrgInfoDao {
    /**
     * 新增机构
     * @param obj
     */
    void insert(OrgInfoModel obj);

    /**
     * 修改机构
     * @param obj
     */
    void update(OrgInfoModel obj);

    /**
     * 删除机构
     * @param id
     */
    void delete(Integer id);

    /**
     * 根据ID查询机构
     * @param id    支持Integer和List两种格式
     */
    List<OrgInfoModel> getByID(Object id);

    /**
     * 查询机构
     * @param map
     */
    CommonObject query(Map map);
}