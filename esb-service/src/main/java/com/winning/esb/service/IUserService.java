package com.winning.esb.service;

import com.winning.esb.model.UserModel;
import com.winning.esb.model.common.CommonObject;

import java.util.List;
import java.util.Map;

public interface IUserService {
    String save(UserModel obj, List<Integer> appList);
    String changePwd(UserModel obj);
    void resetPwd(Integer id);
    String delete(List<Integer> idList);

    CommonObject query(Map map);
    UserModel queryByUsername(String username);
    UserModel login(UserModel obj);
}
