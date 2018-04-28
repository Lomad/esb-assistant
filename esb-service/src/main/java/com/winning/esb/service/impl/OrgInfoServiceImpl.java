package com.winning.esb.service.impl;

import com.winning.esb.dao.IOrgInfoDao;
import com.winning.esb.model.AppInfoModel;
import com.winning.esb.model.OrgInfoModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.SimpleObject;
import com.winning.esb.model.enums.QueryParameterKeys;
import com.winning.esb.service.IAppInfoService;
import com.winning.esb.service.IOrgInfoService;
import com.winning.esb.utils.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author xuehao
 * @date 2017/8/9
 */
@Service
public class OrgInfoServiceImpl implements IOrgInfoService {
    @Autowired
    private IOrgInfoDao orgInfoDao;
    @Autowired
    private IAppInfoService appInfoService;

    @Override
    public String save(OrgInfoModel obj) {
        String err = "";
        if (StringUtils.isEmpty(obj.getCode())) {
            err = "代码不能为空！";
        } else if (existCodeOrName(obj.getId(), "code", obj.getCode())) {
            err = "代码已存在！";
        }
        if (StringUtils.isEmpty(obj.getName())) {
            err = "名称不能为空！";
        } else if (existCodeOrName(obj.getId(), "name", obj.getName())) {
            err = "名称已存在！";
        }

        if (obj.getOrder_num() == null) {
            obj.setOrder_num(0);
        }

        if (StringUtils.isEmpty(err)) {
            obj.setMtime(new Date());
            if (obj.getId() == null) {
                obj.setCtime(obj.getMtime());
                orgInfoDao.insert(obj);
            } else {
                orgInfoDao.update(obj);
            }
        }
        return err;
    }

    @Override
    public String delete(List<Integer> idList) {
        Map<String, Object> map = new HashMap<>();
        String err = "";
        for (Integer id : idList) {
            map.put("orgId", id);
            CommonObject commonObject = appInfoService.query(map);
            Collection<AppInfoModel> appInfoModels = commonObject.getDatas();
            List<Integer> appStatusList = null;
            String appName = "";
            for (AppInfoModel appInfoModel : appInfoModels) {
                Integer appStatus = appInfoModel.getStatus();
                appStatusList = new ArrayList<>();
                if (appStatus.intValue() == 1) {
                    appStatusList.add(1);
                    appName = appInfoModel.getAppName();
                }
            }
            if (appStatusList != null && appStatusList.contains(1)) {
                err += "【" + appName + "】等系统正在使用，无法删除！";
            } else {
                orgInfoDao.delete(id);
            }
        }
        return err;

    }

    @Override
    public OrgInfoModel getByID(Integer id) {
        List<OrgInfoModel> list = orgInfoDao.getByID(id);
        return ListUtils.isEmpty(list) ? null : list.get(0);
    }

    @Override
    public List<OrgInfoModel> getByID(List<Integer> idList) {
        return orgInfoDao.getByID(idList);
    }

    @Override
    public List<OrgInfoModel> getByName(Object name) {
        Map map = new HashMap();
        if (name instanceof String) {
            map.put("name", name);
        } else {
            map.put("nameList", name);
        }
        CommonObject commonObject = query(map);
        return ListUtils.transferToList(commonObject.getDatas());
    }

    @Override
    public List<OrgInfoModel> list() {
        CommonObject commonObject = query(null);
        return ListUtils.transferToList(commonObject.getDatas());
    }

    @Override
    public CommonObject query(Map map) {
        return orgInfoDao.query(map);
    }

    @Override
    public List<SimpleObject> listIdName() {
        List<SimpleObject> resultList;
        CommonObject commonObject = query(null);
        if (commonObject.getDatas() != null && commonObject.getDatas().size() > 0) {
            OrgInfoModel orgInfoModel;
            resultList = new ArrayList<>();
            for (Object obj : commonObject.getDatas()) {
                orgInfoModel = (OrgInfoModel) obj;
                resultList.add(new SimpleObject(String.valueOf(orgInfoModel.getId()), orgInfoModel.getName()));
            }
        } else {
            resultList = null;
        }
        return resultList;
    }

    @Override
    public Map<Integer, OrgInfoModel> mapIdObj(List<OrgInfoModel> list) {
        if(!ListUtils.isEmpty(list)) {
            Map<Integer, OrgInfoModel> map = new HashMap<>();
            for(OrgInfoModel item : list) {
                map.put(item.getId(), item);
            }
            return map;
        }
        return null;
    }

    /**
     * 根据列名与值判断数据库是否存在
     */
    public boolean existCodeOrName(Integer id, String columnName, String columnValue) {
        Map map = new HashMap();
        map.put(QueryParameterKeys.STARTINDEX.getKey(), 0);
        map.put(QueryParameterKeys.PAGESIZE.getKey(), 2);
        map.put(columnName, columnValue);
        CommonObject commonObject = query(map);
        if (commonObject.getTotalSize() > 1) {
            return true;
        } else if (commonObject.getTotalSize() == 1) {
            OrgInfoModel orgInfoModel = (OrgInfoModel) commonObject.getDatas().iterator().next();
            if (orgInfoModel.getId() != null && id != null && orgInfoModel.getId().intValue() == id.intValue()) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
}