package com.winning.esb.service.impl;

import com.winning.esb.dao.IUserDao;
import com.winning.esb.model.UserModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.enums.QueryParameterKeys;
import com.winning.esb.model.enums.UserEnum;
import com.winning.esb.model.ext.UserExtModel;
import com.winning.esb.service.IUserAppService;
import com.winning.esb.service.IUserService;
import com.winning.esb.stable.NormalConst;
import com.winning.esb.utils.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private IUserDao userDao;
    @Autowired
    private IUserAppService userAppService;

    @Override
    public String save(UserModel obj, List<Integer> aidList) {
        String err = "";
        if (StringUtils.isEmpty(obj.getUsername())) {
            err = "用户名不能为空！";
        } else if (existName(obj.getId(), obj.getUsername())) {
            err = "用户名已存在！";
        }
        if (StringUtils.isEmpty(err)) {
            if (obj.getId() == null) {
                obj.setPassword(NormalConst.PWD_DEFFAULT);
                obj.setId(userDao.insert(obj));
            } else {
                userDao.update(obj);
            }
            //非管理员用户需要设置可访问的业务系统
            if (obj.getRole() != null && UserEnum.RoleEnum.Admin.getCode() != obj.getRole().intValue()) {
                err = userAppService.insert(obj.getId(), aidList);
            }
        }
        return err;
    }

    @Override
    public String changePwd(UserModel obj) {
        String errInfo = "";
        String userName = obj.getUsername();
        UserModel userModel = queryByUsername(userName);
        if(userModel != null){
            String oldPassword = userModel.getPassword();
            if(oldPassword.equals(obj.getPassword())){
                errInfo = "旧密码与新密码相同！请重新输入！ ";
            }
        }
        if(StringUtils.isEmpty(errInfo)) {
            userDao.changePwd(obj);
        }
        return errInfo;
    }

    @Override
    public void resetPwd(Integer id) {
        UserModel obj = new UserModel();
        obj.setId(id);
        obj.setPassword(NormalConst.PWD_DEFFAULT);
        userDao.changePwd(obj);
    }

    @Override
    public String delete(List<Integer> idList) {
        for (Integer id : idList) {
            userDao.delete(id);
        }
        return null;
    }

    @Override
    public CommonObject query(Map map) {
        CommonObject commonObject = userDao.query(map);
        if (commonObject.getTotalSize() > 0) {
            List<UserExtModel> userExtModels = new ArrayList<>();
            List<UserModel> userModels = ListUtils.transferToList(commonObject.getDatas());
            UserExtModel userExtModel;
            for (UserModel userModel : userModels) {
                userExtModel = new UserExtModel(userModel);
                userExtModel.setAppIdList(userAppService.getAidListByUserid(userModel.getId()));
                userExtModels.add(userExtModel);
            }
            commonObject.setDatas(userExtModels);
        }
        return commonObject;
    }

    @Override
    public UserModel queryByUsername(String username) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", username);
        CommonObject commonObject = query(map);
        if (commonObject.getDatas() != null && commonObject.getDatas().size() > 0) {
            UserExtModel userExtModel = (UserExtModel) commonObject.getDatas().iterator().next();
            return userExtModel.getObj();
        }
        return null;
    }

    @Override
    public UserModel login(UserModel obj) {
        String err = "";
        Map<String, Object> map = new HashMap<>();
        map.put("username", obj.getUsername());
        map.put("password", obj.getPassword());
        CommonObject commonObject = query(map);
        if (commonObject.getDatas() != null && commonObject.getDatas().size() > 0) {
            UserExtModel userExtModel = (UserExtModel) commonObject.getDatas().iterator().next();
            return userExtModel.getObj();
        }
        return null;
    }

    /**
     * 判断名称是否存在
     */
    private boolean existName(Integer id, String name) {
        Map map = new HashMap();
        map.put(QueryParameterKeys.STARTINDEX.getKey(), 0);
        map.put(QueryParameterKeys.PAGESIZE.getKey(), 2);
        map.put("username", name);
        CommonObject commonObject = query(map);
        if (commonObject.getTotalSize() > 1) {
            return true;
        } else if (commonObject.getTotalSize() == 1) {
            UserModel userModel = ((UserExtModel) commonObject.getDatas().iterator().next()).getObj();
            if (userModel.getId() != null && userModel.getId().equals(id)) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
}