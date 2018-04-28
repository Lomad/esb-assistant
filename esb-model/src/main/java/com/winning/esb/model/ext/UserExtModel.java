package com.winning.esb.model.ext;

import com.winning.esb.model.UserModel;

import java.util.List;

/**
 * Created by xuehao on 2017/9/9.
 */
public class UserExtModel {
    private UserModel obj;
    private List<Integer> appIdList;

    public UserExtModel(UserModel obj) {
        this.obj = obj;
    }

    public UserModel getObj() {
        return obj;
    }

    public void setObj(UserModel obj) {
        this.obj = obj;
    }

    public List<Integer> getAppIdList() {
        return appIdList;
    }

    public void setAppIdList(List<Integer> appIdList) {
        this.appIdList = appIdList;
    }
}