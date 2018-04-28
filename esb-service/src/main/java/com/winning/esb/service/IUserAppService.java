package com.winning.esb.service;

import com.winning.esb.model.UserAppModel;
import com.winning.esb.model.common.CommonObject;

import java.util.List;
import java.util.Map;

public interface IUserAppService {
    String insert(List<UserAppModel> list);
    String insert(Integer userid, List<Integer> aidList);

    String delete(Integer id);

    String deleteByUserid(Integer userid);

    CommonObject query(Map map);

    /**
     * 根据用户ID获取业务系统ID列表
     */
    List<Integer> getAidListByUserid(Integer userid);

    /**
     * 根据用户名获取业务系统ID列表
     */
    List<Integer> getAidListByUsername(String username);
}