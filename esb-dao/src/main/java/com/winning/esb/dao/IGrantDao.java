package com.winning.esb.dao;

import com.winning.esb.model.GrantModel;
import com.winning.esb.model.common.CommonObject;

import java.util.List;
import java.util.Map;

public interface IGrantDao {
    Integer insert(GrantModel obj);
    void insert(List<GrantModel> list);
    void update(GrantModel obj);
    void delete(List<Integer> idList);

    /**
     * 根据服务ID删除
     */
    void deleteBySid(List<Integer> sidList);

    List<GrantModel> queryByAid(List<Integer> aidList);

    CommonObject query(Map map);

    /**
     * 设置审批状态
     */
    void approveState(GrantModel obj);

    /**
     * 获取已授权全部服务的业务系统ID列表
     */
    List<Integer> getAidGrantAllSvc();

}