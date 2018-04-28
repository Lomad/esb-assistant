package com.winning.esb.controller;

import com.alibaba.fastjson.JSONArray;
import com.winning.esb.model.UserModel;
import com.winning.esb.model.enums.UserEnum;
import com.winning.esb.service.IUserService;
import com.winning.esb.utils.AppCtxUtils;
import com.winning.esb.utils.FileUtils;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.StringUtils;
import com.winning.front.frame.Model.MenuModel;
import com.winning.front.frame.Model.MessageInfo;
import com.winning.front.frame.Model.UserInfo;
import com.winning.front.frame.infrastructure.IPermissionWebservice;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Admin
 * @date 2017/7/6
 */
public class FrameController implements IPermissionWebservice {
    private List<String> filterMenuIdList = new ArrayList<>();

    @Override
    public UserInfo existLogin(String loginId, String passWord, String appid) {
        UserModel userModelIn = new UserModel();
        userModelIn.setUsername(loginId);
        userModelIn.setPassword(passWord);
        IUserService userService = AppCtxUtils.getBean("userServiceImpl");
        UserModel userModel = userService.login(userModelIn);
        if (userModel != null) {
            UserInfo user = new UserInfo();
            user.setUsername(userModel.getUsername());
            user.setUserid(userModel.getUsername());
            return user;
        }
        return null;
    }

    @Override
    public MessageInfo updatePassword(String userid, String appid, String oldPassword, String newPassword) {
        MessageInfo msg = new MessageInfo();
        msg.setStatus("true");
        return msg;
    }

    @Override
    public List<MenuModel> setPermission(String userid, String appid) {
        String configPath = FileUtils.getRootPath() + "/WEB-INF/classes/META-INF/menu.json";
        String rawConfig = FileUtils.readFile(configPath);
        List<MenuModel> menuList = JSONArray.parseArray(rawConfig, MenuModel.class);
        if (needFilter(userid, menuList)) {
            filterMenu(menuList);
        }
        return menuList;
    }

    /**
     * 是否需要过滤菜单
     */
    private boolean needFilter(String username, List<MenuModel> menuList) {
        IUserService userService = AppCtxUtils.getBean("userServiceImpl");
        UserModel userModel = userService.queryByUsername(username);
        if (userModel != null && userModel.getRole() != null && UserEnum.RoleEnum.Normal.getCode() == userModel.getRole().intValue()) {
            if (ListUtils.isEmpty(filterMenuIdList)) {
                filterMenuIdList.add("13");
                filterMenuIdList.add("1301");
                filterMenuIdList.add("14");
                filterMenuIdList.add("1401");
                filterMenuIdList.add("1402");
                filterMenuIdList.add("1403");
            }
            return true;
        } else if (userModel != null && userModel.getRole() != null && UserEnum.RoleEnum.Admin.getCode() == userModel.getRole().intValue()) {

            //获取所有菜单ID
            filterMenuIdList.addAll(getAllMenuID(menuList));

            //需要排除的菜单
            filterMenuIdList.remove("1301");
            //filterMenuIdList.remove("1401");

            return true;
        }
        return false;
    }

    /**
     * 过滤菜单
     */
    private void filterMenu(List<MenuModel> menuList) {
        if (!ListUtils.isEmpty(menuList)) {
            List<MenuModel> menuRemoveList = new ArrayList<>();
            for (MenuModel menu : menuList) {
                if (!filterMenuIdList.contains(menu.getId())) {
                    menuRemoveList.add(menu);
                }
                if (!ListUtils.isEmpty(menu.getList())) {
                    filterMenu(menu.getList());
                }
            }
            menuList.removeAll(menuRemoveList);
        }
    }

    /**
     * 获取所有菜单ID
     */
    private List<String> getAllMenuID(List<MenuModel> menuList) {
        List<String> idList = new ArrayList<>();
        for (MenuModel model : menuList) {
            idList.add(model.getId());
            if(!ListUtils.isEmpty(model.getList())) {
                idList.addAll(getAllMenuID(model.getList()));
            }
        }
        return idList;
    }

}