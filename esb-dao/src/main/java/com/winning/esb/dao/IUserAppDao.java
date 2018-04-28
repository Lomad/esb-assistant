package com.winning.esb.dao;

import com.winning.esb.model.UserAppModel;
import com.winning.esb.model.common.CommonObject;

import java.util.List;
import java.util.Map;

public interface IUserAppDao {
    void insert(List<UserAppModel> list);

    void delete(Integer id);

    void deleteByUserid(Integer userid);

    CommonObject query(Map map);
}