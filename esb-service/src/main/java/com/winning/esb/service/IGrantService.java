package com.winning.esb.service;

import com.winning.esb.model.GrantModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.ext.GrantExtModel;

import java.util.List;
import java.util.Map;

public interface IGrantService {
    Integer insert(GrantModel obj);

    String save(String strModel, String strSidList);

    String delete(Integer id);

    String delete(List<Integer> idList);

    /**
     * 根据服务ID删除
     */
    String deleteBySid(Integer sid);

    /**
     * 根据服务ID删除
     */
    String deleteBySid(List<Integer> sidList);

    /**
     * 申请授权
     */
    String apply(String userid, String strModel, String strSidList);

    /**
     * 根据系统ID获取GrantModel对象
     */
    List<GrantModel> queryByAid(Integer aid);

    /**
     * 根据系统ID获取GrantModel对象
     */
    List<GrantModel> queryByAid(List<Integer> aidList);

    /**
     * 根据系统ID获取GrantExtModel对象
     */
    List<GrantExtModel> getGrantExtList(Integer aid);

    CommonObject query(Map map);

    CommonObject queryExt(Map map);


    /**
     * 设置审批状态
     */
    String approveState(GrantModel obj);

    /**
     * 获取已授权全部服务的业务系统ID列表
     */
    List<Integer> getAidGrantAllSvc();

    /**
     * 判断服务是否被授权
     */
    List<GrantModel> getSvcGranted(Integer sid);

    /**
     * 判断服务是否被授权
     */
    List<GrantModel> getSvcGranted(List<Integer> sidList);

}