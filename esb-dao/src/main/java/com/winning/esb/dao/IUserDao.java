package com.winning.esb.dao;

import com.winning.esb.model.UserModel;
import com.winning.esb.model.common.CommonObject;

import java.util.Map;

public interface IUserDao {
    Integer insert(UserModel obj);
    void update(UserModel obj);
    void changePwd(UserModel obj);
    void delete(Integer id);
    CommonObject query(Map map);
}