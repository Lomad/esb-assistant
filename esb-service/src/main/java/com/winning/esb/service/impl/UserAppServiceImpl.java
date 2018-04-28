package com.winning.esb.service.impl;

import com.winning.esb.dao.IUserAppDao;
import com.winning.esb.model.UserAppModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.service.IUserAppService;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserAppServiceImpl implements IUserAppService {
    @Autowired
    private IUserAppDao dao;

    @Override
    public String insert(List<UserAppModel> list) {
        String err = "";
        dao.insert(list);
        return err;
    }

    @Override
    public String insert(Integer userid, List<Integer> aidList) {
        String err = "";
        if (!ListUtils.isEmpty(aidList)) {
            //删除用户可访问的旧系统
            err = deleteByUserid(userid);
            //新增可访问的系统
            if (StringUtils.isEmpty(err)) {
                List<UserAppModel> list = new ArrayList<>();
                UserAppModel model;
                for (Integer aid : aidList) {
                    model = new UserAppModel();
                    model.setUserid(userid);
                    model.setAid(aid);
                    list.add(model);
                }
                err = insert(list);
            }
        }
        return err;
    }

    @Override
    public String delete(Integer id) {
        dao.delete(id);
        return null;
    }

    @Override
    public String deleteByUserid(Integer userid) {
        dao.deleteByUserid(userid);
        return null;
    }

    @Override
    public CommonObject query(Map map) {
        return dao.query(map);
    }

    @Override
    public List<Integer> getAidListByUserid(Integer userid) {
        Map<String, Object> map = new HashMap();
        map.put("userid", userid);
        CommonObject commonObject = query(map);
        return getAidList(commonObject);
    }

    @Override
    public List<Integer> getAidListByUsername(String username) {
        Map<String, Object> map = new HashMap();
        map.put("username", username);
        CommonObject commonObject = query(map);
        return getAidList(commonObject);
    }

    /**
     * 获取业务系统ID列表
     */
    private List<Integer> getAidList(CommonObject commonObject) {
        List<Integer> appIdList;
        if (commonObject.getTotalSize() > 0) {
            appIdList = new ArrayList<>();
            List<UserAppModel> userAppModels = ListUtils.transferToList(commonObject.getDatas());
            for (UserAppModel userAppModel : userAppModels) {
                appIdList.add(userAppModel.getAid());
            }
        } else {
            appIdList = null;
        }
        return appIdList;
    }
}